package com.gluckentext.ui

import android.view.View
import android.widget.{AdapterView, SimpleAdapter}
import com.gluckentext.R
import com.gluckentext.datahandling.Persistence
import com.gluckentext.entities.QuizDefinition
import com.gluckentext.quiz.createQuiz
import com.gluckentext.wikipediaaccess.{RandomWikiPageRetriever, WikiArticleLocation, WikiPageLoader}
import org.scaloid.common._

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

  var articlesShown = Stream[WikiArticleLocation]()

  def findRandomArticles: Future[Stream[WikiArticleLocation]] = {
    Future {
      RandomWikiPageRetriever.retrieveRandomPages(language)
    }
  }

  onCreate {
    contentView = new SVerticalLayout {
      this += listView.onItemClick(onItemClick)
    }
  }

  onResume {
    getActionBar.setTitle(R.string.selectArticle)
    findRandomArticles.onSuccess {
      case articles: Stream[WikiArticleLocation] =>
        runOnUiThread {
          val data = articles.map(a => mapAsJavaMap(Map("title" -> a.title, "url" -> a.url))).toList
          listView.setAdapter(new SimpleAdapter(
            this, data, android.R.layout.simple_list_item_2,
            Array("title", "url"),
            Array(android.R.id.text1, android.R.id.text2)))
        }
        articlesShown = articles
    }
  }

  private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
    onArticleSelected(articlesShown.get(position))

  def onArticleSelected(articleLocation: WikiArticleLocation) = {
    val quizDefinition: QuizDefinition = new QuizDefinition(activeWordSet, articleLocation.title, articleLocation.url)
    persistence.saveQuizDefinition(language, quizDefinition)

    // TODO: move this to a different context
    //progressBar.visibility = VISIBLE
    val f = Future {
      val article = WikiPageLoader.loadArticleByUri(articleLocation.url)
      if (article.body.isEmpty) toast("No relevant text in article")
      val quiz = createQuiz about activeWordSet from article.body
      runOnUiThread {
        persistence.saveQuizStatus(quizDefinition, quiz)
        startActivity[ArticleActivity]
        //progressBar.visibility = GONE
      }
    }.recover {
      case e => e.printStackTrace(); toast("Error loading article: " + e)
    }
    f.onFailure { case x => x.printStackTrace()}
  }
}