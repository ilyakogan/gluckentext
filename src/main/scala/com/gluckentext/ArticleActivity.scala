package com.gluckentext

import android.graphics.Color
import android.os.AsyncTask
import android.webkit.WebSettings
import org.scaloid.common._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

class ArticleActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")
  implicit val exec = ExecutionContext.fromExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

  val words = List("of", "underneath", "in", "at", "on")
  var webView: Option[SWebView] = None

  onCreate {
    contentView = new SRelativeLayout {
      val loadButton = SButton(R.string.load).<<.wrap.alignParentRight.alignParentLeft.alignParentTop.>>.onClick(loadArticle())
      val wordsTable = new STableLayout {
        words.grouped(3).foreach(group =>
          this += new STableRow {
            group.map(word => SButton(word).<<.wrap.>>.onClick(wordButtonClicked(word)))
          }.<<.fill.>>
        )
      }.<<.alignParentRight.alignParentLeft.alignParentBottom.wrap.>>
      this += wordsTable
      webView = Some(SWebView().<<.wrap.alignParentRight.alignParentLeft.above(wordsTable).below(loadButton).>>)
    }.padding(20.dip)
  }

  def loadArticle() = {
    val f = Future {
      val result = WikiPageLoader.loadWikiPageXml("en", "Tatarstan")
      runOnUiThread {
        val start = "<html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' /></head><body>"
        val end = "</body></html>"

        webView.get.loadData(start + result + end, "text/html; charset=UTF-8", null)
        //webView.loadData(result, "text/html", null)}
      }
    }
    f.onFailure { case x => x.printStackTrace()}
  }

  def wordButtonClicked(word: String) = toast(word)

}

