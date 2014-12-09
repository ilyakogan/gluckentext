package com.gluckentext.ui

import android.os.AsyncTask
import android.view.{ActionMode, Gravity, Menu, MenuItem}
import android.webkit.{WebChromeClient, WebView, WebViewClient}
import com.gluckentext.datahandling.Persistence
import com.gluckentext.quiz.{QuizWord, WikiPageLoader, createQuiz}
import com.gluckentext.ui.QuizHtml._
import org.scaloid.common._

import scala.concurrent.{ExecutionContext, Future}

class ArticleActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")
  implicit val exec = ExecutionContext.fromExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

  lazy val titleText = new STextView()
  lazy val webView = new SWebView()
  lazy val persistence = new Persistence()
  lazy val language = persistence.loadActiveLanguage
  lazy val quizDefinition = persistence.loadQuizDefinition(language)

  onCreate {
    contentView = new SVerticalLayout {
      this += titleText.textSize(20.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(5.dip).>>
      this += webView
    }
  }

  onResume {
    titleText.text = quizDefinition.articleName
    persistence.loadQuizStatus(quizDefinition) match {
      case Some(quizStatus) =>
        val quizText = generateQuizHtml(quizStatus)
        populateWebView(quizText)
      case None => loadArticle()
    }
  }

  def populateWebView(quizHtml: String) = {
    prepareWebView(webView)
    webView.loadData(quizHtml, "text/html; charset=UTF-8", null)
  }

  def loadArticle() = {
    val f = Future {
      val article = WikiPageLoader.loadArticleByUri(quizDefinition.articleUri)
      val quiz = createQuiz about quizDefinition.practiceWords from article.body
      val quizText = generateQuizHtml(quiz)
      runOnUiThread {
        persistence.saveQuizStatus(quizDefinition, quiz)
        populateWebView(quizText)
      }
    }
    f.onFailure { case x => x.printStackTrace()}
  }

  def prepareWebView(webView: SWebView) {
    webView.settings.setJavaScriptEnabled(true)
    webView.setWebChromeClient(new WebChromeClient)
    webView.webViewClient = new WebViewClient {
      override def shouldOverrideUrlLoading(view: WebView, url: String) = {
        quizWordClicked(url.replaceAll("\\?", ""))
        true
      }
    }
  }

  def quizWordClicked(url: String) = {
    info("Quiz word clicked: " + url)
    url match {
      case makeGuessUrl(quizWord) =>
        startActionMode(new ActionMode.Callback {
          override def onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            quizDefinition.practiceWords.foreach(guess => menu.add(guess).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS))
            true
          }

          override def onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean = {
            guessClicked(quizWord, menuItem.getTitle.toString)
            actionMode.finish()
            true
          }

          override def onDestroyActionMode(actionMode: ActionMode): Unit = {}

          override def onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean = true
        })
    }
  }

  def guessClicked(word: QuizWord, guess: String) = {
    if (word.rightAnswer.toString.toLowerCase == guess.toLowerCase) markRightAnswer(word)
    else toast("This is not the right answer. Want to try again?")
  }

  def markRightAnswer(answeredWord: QuizWord) {
    val jsUrl = "javascript:markSolved(" + getTagId(answeredWord) + ")"
    webView.loadUrl(jsUrl)
    persistence.loadQuizStatus(quizDefinition) match {
      case Some(quizStatus) =>
        val quizWithSolvedWord = quizStatus.map {
          case w@QuizWord(answeredWord.id, _, _) => w.solved
          case x => x
        }
        persistence.saveQuizStatus(quizDefinition, quizWithSolvedWord)
      case None => toast("Looks like the quiz object is missing from the app prefs")
    }
  }
}

