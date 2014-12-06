package com.gluckentext

import android.os.AsyncTask
import android.view.{Gravity, ActionMode, Menu, MenuItem}
import android.webkit.{WebChromeClient, WebView, WebViewClient}
import com.gluckentext.QuizHtml._
import com.gluckentext.Serializer._
import org.scaloid.common._

import scala.concurrent.{ExecutionContext, Future}

class ArticleActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")
  implicit val exec = ExecutionContext.fromExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

  var webViewOption: Option[SWebView] = None
  var titleTextOption: Option[STextView] = None


  onCreate {
    contentView = new SVerticalLayout {
      titleTextOption = Some(STextView().textSize(20.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(5.dip).>>)
      webViewOption = Some(SWebView())
    }
  }

  onResume {
    val quiz = getPersistedQuiz
    quiz match {
      case None => loadArticle()
      case Some(q) =>
        val quizText = generateQuizHtml(q)
        populateWebView(quizText)
    }
  }

  def populateWebView(quizHtml: String) = {
    webViewOption match {
      case Some(webView) =>
        prepareWebView(webView)
        webView.loadData(quizHtml, "text/html; charset=UTF-8", null)
      case None => toast("Looks like the webview is missing")
    }
  }

  def loadArticle() = {
    val f = Future {
      val article = WikiPageLoader.loadWikiPageXml(Preferences().language("en"), Preferences().lastArticleName("Spam"))
      val quiz = createQuiz about getPracticeWords from article.body
      val quizText = generateQuizHtml(quiz)
      runOnUiThread {
        persistQuiz(quiz)
        populateWebView(quizText)
        titleTextOption.get.text = article.title
      }
    }
    f.onFailure { case x => x.printStackTrace()}
  }

  def getPracticeWords: Array[String] = {
    Preferences().practiceWords("in").split(",")
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
            getPracticeWords.foreach(guess => menu.add(guess).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS))
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
    webViewOption match {
      case Some(webView) => webView.loadUrl(jsUrl)
    }
    getPersistedQuiz match {
      case Some(quiz) =>
        val quizWithSolvedWord = quiz.map {
          case w@QuizWord(answeredWord.id, _, _) => w.solved
          case x => x
        }
        persistQuiz(quizWithSolvedWord)
      case None => toast("Looks like the quiz object is missing from the app prefs")
    }
  }

  def getPersistedQuiz: Option[Iterable[QuizPart]] = {
    Preferences().quiz("") match {
      case "" => None
      case q => Some(deserializeToQuiz(q))
    }
  }

  def persistQuiz(quiz: Iterable[QuizPart]) = {
    val quizSerialized: String = serializeQuiz(quiz)
    Preferences().quiz = quizSerialized
  }
}

