package com.gluckentext

import android.view._
import org.scaloid.common._
import android.widget._

class LanguageSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.selectLanguageTitle).textSize(25.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(10.dip)
      STextView(R.string.imLearning).textSize(20.dip).margin(5.dip)

      val languageNames = Languages.all.map(_.name).toArray
      private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
        onLanguageSelected(Languages.all(position))

      var listView = SListView().onItemClick(onItemClick)
      listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, languageNames))

      def onLanguageSelected(language: Language) = {
        Preferences().language = language.code
        Preferences().lastArticleName = language.defaultArticle
        Preferences().quiz = ""
        startActivity[PracticeSelectionActivity]
      }
    }
  }

}


