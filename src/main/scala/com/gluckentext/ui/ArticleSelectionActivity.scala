package com.gluckentext.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.{AbsListView, AdapterView, ArrayAdapter}
import com.gluckentext.R
import com.gluckentext.datahandling.Persistence
import com.gluckentext.entities.QuizDefinition
import com.gluckentext.quiz.{NotEnoughTextInArticleException, createQuiz}
import com.gluckentext.wikipediaaccess.{RandomWikiPageRetriever, WikiArticleLocation, WikiPageLoader}
import org.scaloid.common._

import scala.actors.Actor
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ArticleSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  lazy val listView = new SListView()

  lazy val persistence = new Persistence()
  lazy val language = persistence.loadActiveLanguage
  lazy val wordSets = persistence.loadAvailableWordSets(language)
  lazy val activeWordSet = persistence.loadActiveWordSet(language)

  lazy val articleArrayAdapter = new ArrayAdapter[WikiArticleLocation](this, android.R.layout.simple_list_item_1)

  val receiver = new Actor {
    def act() {
      loop {
        react {
          case article: WikiArticleLocation =>
            runOnUiThread {
              articleArrayAdapter.add(article)
            }
        }
      }
    }
  }
  receiver.start()

  def findRandomArticlesAsync() = {
    Future {
      RandomWikiPageRetriever.retrieveRandomPages(language).foreach(a =>
        receiver ! a)
    }
  }

  onCreate {
    contentView = new SVerticalLayout {
      this += listView.onScroll(onScroll).onItemClick(onItemClick)
    }
  }

  val visibleThreshold = 5
  var currentPage = 0
  var previousTotal = 0
  var loading = true

  def onScroll = (_: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) => {
    if (loading && totalItemCount > previousTotal) {
      loading = false
      previousTotal = totalItemCount
      currentPage += 1
    }
    if (!loading && (firstVisibleItem + visibleItemCount) >= (totalItemCount - 1)) {
      findRandomArticlesAsync()
      loading = true
    }
  }

  onResume {
    getActionBar.setTitle(R.string.selectArticle)
    listView.setAdapter(articleArrayAdapter)
    if (articleArrayAdapter.isEmpty) findRandomArticlesAsync()
  }

  private val onItemClick = (_: AdapterView[_], view: View, position: Int, _: Long) => {
    val article = articleArrayAdapter.getItem(position)
    onArticleSelected(article)
  }

  def onArticleSelected(articleLocation: WikiArticleLocation) = {
    val quizDefinition: QuizDefinition = new QuizDefinition(activeWordSet, articleLocation.title, articleLocation.url)
    persistence.saveQuizDefinition(language, quizDefinition)
    loadArticle(quizDefinition)
  }

  def loadArticle(quizDefinition: QuizDefinition) = {
    val progressDialog = ProgressDialog.show(this, quizDefinition.articleName, getString(R.string.loadingArticle), true)
    val f = Future {
      val article = WikiPageLoader.loadArticleByUri(quizDefinition.articleUri)
      val quiz = createQuiz about quizDefinition.practiceWords from article.body
      persistence.saveQuizStatus(quizDefinition, quiz)
    }
    f.onFailure {
      case e: NotEnoughTextInArticleException => alert(quizDefinition.articleName, R.string.notEnoughTextInArticle)
      case e => e.printStackTrace(); toast("Error loading article: " + e)}
    f.onSuccess { case _ =>
      runOnUiThread {
        startActivity[ArticleActivity]
      }
    }
    f.onComplete { case _ =>
      runOnUiThread {
        progressDialog.dismiss()
      }
    }
  }

  override def onSaveInstanceState(outState: Bundle) {
    val articles = Range(0, articleArrayAdapter.getCount - 1).map(i => articleArrayAdapter.getItem(i))
    outState.putStringArray("titles", articles.map(_.title).toArray)
    outState.putStringArray("urls", articles.map(_.url).toArray)
  }

  override def onRestoreInstanceState(savedInstanceState: Bundle): Unit = {
    val titles = savedInstanceState.getStringArray("titles")
    val urls = savedInstanceState.getStringArray("urls")
    val articles = titles.zip(urls).map { case (title, url) => WikiArticleLocation(title, url)}
    articleArrayAdapter.clear()
    articleArrayAdapter.addAll(asJavaCollection(articles))
  }

}