package com.gluckentext

import android.content.{BroadcastReceiver, IntentFilter}
import android.graphics.Color
import android.os.AsyncTask
import android.text.Html
import android.view.{MenuItem, Menu, ActionMode}
import android.webkit.{WebChromeClient, WebViewClient, WebSettings}
import org.scaloid.common._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

class ArticleActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")
  implicit val exec = ExecutionContext.fromExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

  val words = List("of", "in", "at", "on")
  var webView: Option[SWebView] = None

  onCreate {
    contentView = new SRelativeLayout {
      val loadButton = SButton(R.string.load).<<.wrap.alignParentRight.alignParentLeft.alignParentTop.>>.onClick(loadArticle())
      webView = Some(SWebView().<<.wrap.alignParentRight.alignParentLeft.alignParentBottom.below(loadButton).>>)
    }
  }

  def loadArticle() = {
    def populateWebView(quizText: String) = {
      webView.get.setWebChromeClient(new WebChromeClient)
      webView.get.settings.setJavaScriptEnabled(true)
      webView.get.webViewClient = new WebViewClient {
        override def shouldOverrideUrlLoading(view: android.webkit.WebView, url: String) = {
          quizWordClicked(url)
          true
        }
      }

      val start = "<html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' /></head>" +
        "<body style='line-height: 200%'>"
      val end = "</body></html>"
      webView.get.loadData(start + quizText + end, "text/html; charset=UTF-8", null)
    }

    val f = Future {
      val article = WikiPageLoader.loadWikiPageXml("en", "Tatarstan")
      val quiz: List[QuizPart] = createQuiz about words from article
      val quizText = GenerateQuizHtml(quiz)
      runOnUiThread {
        populateWebView(quizText)
      }
    }
    f.onFailure { case x => x.printStackTrace()}
  }

  def quizWordClicked(url: String) = {
    info("Quiz word clicked: " + url)
    url match {
      case makeGuessUrl(quizWord) =>
        startActionMode(new ActionMode.Callback {
          override def onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            words.foreach(guess => menu.add(guess).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)) //.onClick(guessClicked(quizWord, guess)))
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
    if (word.rightAnswer == guess)
      webView.get.loadUrl("javascript:document.getElementById('" + word.order + "').innerHTML = '" + guess + "'")
    else toast("This is not the right answer. Want to try again?")
  }
}

