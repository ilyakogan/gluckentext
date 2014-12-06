package com.gluckentext

import android.view.{View, Gravity}
import android.widget.{ArrayAdapter, AdapterView}
import org.scaloid.common._

class PracticeSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.selectPractice).textSize(25.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(10.dip)
      STextView(R.string.iWantToPractice).textSize(20.dip).margin(5.dip)

      val language = Preferences().language("en")
      val practiceTypes = Languages.get(language).map(l => l.practiceTypes.toList).getOrElse {
        toast("No practice types")
        List()
      }
      val practiceNames = practiceTypes.map(_._1).toArray

      private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
        onPracticeSelected(practiceTypes(position))

      var listView = SListView().onItemClick(onItemClick)
      listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, practiceNames))

      def onPracticeSelected(practiceType: (String, Iterable[String])) = {
        Preferences().practiceWords = practiceType._2.mkString(",")
        Preferences().quiz = ""
        startActivity[ArticleActivity]
      }
    }
  }
}
