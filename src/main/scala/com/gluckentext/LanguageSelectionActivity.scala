package com.gluckentext

import android.view._
import org.scaloid.common._
import android.widget._

class LanguageSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  val languageNames = Languages.all.map(_.name).toArray
  lazy val listView = new SListView()

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.selectLanguageTitle).textSize(25.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(15.dip)
      this += listView.onItemClick(onItemClick)
      listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, languageNames))
    }
  }

  private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
    onLanguageSelected(Languages.all(position))

  def onLanguageSelected(language: Language) = {
    Preferences().language = language.code
    Preferences().lastArticleName = language.defaultArticle
    Preferences().quiz = ""
    startActivity[PracticeSelectionActivity]
  }
}


