package com.gluckentext.ui

import android.view._
import android.widget._
import com.gluckentext.entities._
import com.gluckentext.R
import com.gluckentext.datahandling.Persistence
import org.scaloid.common._

class LanguageSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  lazy val persistence = new Persistence()
  lazy val languages = persistence.loadAvailableLanguages
  lazy val listView = new SListView()

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.selectLanguageTitle).textSize(25.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(15.dip)
      this += listView.onItemClick(onItemClick)
      listView.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1, languages.map(_.name)))
    }
  }

  private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
    onLanguageSelected(languages(position))

  def onLanguageSelected(language: Language) = {
    new Persistence().saveActiveLanguage(language.code)
    startActivity[PracticeSelectionActivity]
  }
}


