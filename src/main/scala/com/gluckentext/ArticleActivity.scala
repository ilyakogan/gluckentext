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

  onCreate {
    contentView = new SVerticalLayout {
      style {
        case b: SButton => b.textColor(Color.RED)
        case t: STextView => t.textSize(15 dip)
      }


      val button = SButton(R.string.red)

      val webView = SWebView()
      webView.getSettings.setDefaultTextEncodingName("utf-8")

      button.onClick(
      {
        val f = Future {
          val result = WikiPageLoader.loadWikiPageXml("en", "Tatarstan")
          runOnUiThread {
            val start = "<html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' /></head><body>"
            val end = "</body></html>"

            webView.loadData(start + result + end, "text/html; charset=UTF-8", null)
            //webView.loadData(result, "text/html", null)}
          }
        }
        f.onFailure { case x => x.printStackTrace}
      })
    }.padding(20 dip)
  }

}
