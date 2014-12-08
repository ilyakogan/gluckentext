package com.gluckentext

import android.view._
import android.widget._
import org.scaloid.common._
import scala.collection.JavaConversions._

class PracticeSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  lazy val listView = new SListView()

  lazy val language = Preferences().language("en")
  lazy val practiceTypes = Languages.get(language).map(l => l.practiceTypes.toList).getOrElse {
    toast("No practice types")
    List()
  }

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.selectPractice).textSize(25.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(15.dip)
      this += listView.onItemClick(onItemClick)

      val data = practiceTypes.map(n => mapAsJavaMap(Map("category" -> n._1, "words" -> n._2.mkString(","))))
      listView.setAdapter(new SimpleAdapter(
        context, data, android.R.layout.simple_list_item_2, Array("category", "words"), Array(android.R.id.text1, android.R.id.text2)))
    }
  }

  private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
    onPracticeSelected(practiceTypes(position))

  def onPracticeSelected(practiceType: (String, Iterable[String])) = {
    Preferences().practiceWords = practiceType._2.mkString(",")
    Preferences().quiz = ""
    startActivity[ArticleActivity]
  }
}
