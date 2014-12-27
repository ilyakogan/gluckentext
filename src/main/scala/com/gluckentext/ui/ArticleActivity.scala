package com.gluckentext.ui

import android.os.AsyncTask
import android.view.View._
import android.view._
import android.webkit.{WebChromeClient, WebView, WebViewClient}
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import com.gluckentext.datahandling.Persistence
import com.gluckentext.quiz.QuizWord
import com.gluckentext.ui.QuizHtml._
import org.scaloid.common._

import scala.concurrent.{Future, ExecutionContext}

class ArticleActivity extends SActivity with NavigationMenuTrait {

  implicit val tag = LoggerTag("Gluckentext")
  implicit val exec = ExecutionContext.fromExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

  lazy val menuAnchor = new STextView()
  lazy val webView = new SWebView()
  lazy val progressBar = new SProgressBar()
  lazy val persistence = new Persistence()
  lazy val language = persistence.loadActiveLanguage
  lazy val quizDefinition = persistence.loadQuizDefinition(language)

  onCreate {
    contentView = new SVerticalLayout {
      this += webView
      this += menuAnchor.textSize(1.dip)
      this += progressBar
    }
  }

  onResume {
    getActionBar.setTitle(quizDefinition.articleName)
    progressBar.visibility = GONE
    persistence.loadQuizStatus(quizDefinition) match {
      case Some(quizStatus) =>
        val quizText = generateQuizHtml(quizStatus)
        populateWebView(quizText)
      case None => startActivity[ArticleSelectionActivity]
    }
  }

  def populateWebView(quizHtml: String) = {
    prepareWebView(webView)
    webView.loadData(quizHtml, "text/html; charset=UTF-8", null)
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
    val f = Future {
      persistence.loadQuizStatus(quizDefinition) match {
        case Some(quizStatus) => (url, quizStatus) match {
          case makeGuessUrl(quizWord) => Some(quizWord)
          case _ => toast("Error getting word"); None
        }
        case None => toast("Looks like the quiz object is missing from the app prefs"); None
      }
    }

    f.onSuccess { case Some(quizWord) =>
      runOnUiThread(showGuessesPopupMenu(quizWord))
    }
  }

  def showGuessesPopupMenu(quizWord: QuizWord) {
    val popupMenu: PopupMenu = new PopupMenu(this, menuAnchor)
    val menu = popupMenu.getMenu
    menu.clear()
    quizDefinition.practiceWords.foreach(guess => menu.add(guess))
    popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener {
      override def onMenuItemClick(menuItem: MenuItem): Boolean = {
        guessClicked(quizWord, menuItem.getTitle.toString)
        true
      }
    })
    popupMenu.show()
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

