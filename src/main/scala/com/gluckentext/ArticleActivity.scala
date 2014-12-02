package com.gluckentext

import android.content.{BroadcastReceiver, IntentFilter}
import android.graphics.Color
import android.os.{Bundle, AsyncTask}
import android.text.Html
import android.view.{MenuItem, Menu, ActionMode}
import android.webkit.{WebView, WebChromeClient, WebViewClient, WebSettings}
import com.gluckentext.QuizHtml._
import org.scaloid.common._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

class ArticleActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")
  implicit val exec = ExecutionContext.fromExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

  val quizSubject = List("of", "in", "at", "on")
  var webViewOption: Option[SWebView] = None

  onCreate {
    contentView = new SRelativeLayout {
      val loadButton = SButton(R.string.load).<<.wrap.alignParentRight.alignParentLeft.alignParentTop.>>.onClick(loadArticle())
      val webView: SWebView = SWebView().<<.wrap.alignParentRight.alignParentLeft.alignParentBottom.below(loadButton).>>
      webViewOption = Some(webView)
    }
  }

  def loadArticle() = {
    def populateWebView(quizHtml: String) = {
      prepareWebView(webViewOption.get)
      webViewOption.get.loadData(quizHtml, "text/html; charset=UTF-8", null)
    }

    val f = Future {
      val article = WikiPageLoader.loadWikiPageXml("en", "Standard_Chinese")
      val quiz: List[QuizPart] = createQuiz about quizSubject from article
      val quizText = generateQuizHtml(quiz)
      runOnUiThread {
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
        quizWordClicked(url.replaceAll("\\?",""))
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
            quizSubject.foreach(guess => menu.add(guess).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS))
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
    if (word.rightAnswer == guess) {
      //val jsUrl = "javascript:document.getElementById('" + getTagId(word) + "').className=\"solved\";"
      val jsUrl = "javascript:markSolved(" + getTagId(word) + ")"
      //val jsUrl = "javascript:document.getElementById('" + getTagId(word) + "').innerHTML = '" + guess + "'"
      //val jsUrl = "javascript:alert(document.getElementById('" + getTagId(word) + "').innerHTML)"
      webViewOption.get.loadUrl(jsUrl)
    }
    else toast("This is not the right answer. Want to try again?")
  }

//  override def onSaveInstanceState(outState: Bundle): Unit = {
//    super.onSaveInstanceState(outState)
//
//    webViewOption match {
//      case Some(webView) =>
//        webView.addJavascriptInterface(new {
//          def showHtml(html: String) = {
//            toast(html)
//            outState.putString("webViewContent", html)
//          }
//        }, "HtmlViewer")
//        webView.loadUrl("javascript:window.HtmlViewer.showHtml" +
//          "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
//    }
//  }
//
//  override def onRestoreInstanceState(state: Bundle): Unit = {
//    val html = state.getString("webViewContent")
//    if (html != null) {
//      webViewOption match {
//        case Some(webView) => webView.loadData(html, "text/html; charset=UTF-8", null)
//      }
//    }
//  }
}

