package com.gluckentext.ui

import android.view._
import android.widget._
import com.gluckentext.R
import com.gluckentext.entities._
import com.gluckentext.datahandling.Persistence
import org.scaloid.common._

import scala.collection.JavaConversions._

class PracticeSelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  lazy val listView = new SListView()

  lazy val persistence = new Persistence()
  lazy val language = persistence.loadActiveLanguage
  lazy val wordSets = persistence.loadAvailableWordSets(language)

  onCreate {
    contentView = new SVerticalLayout {
      STextView(R.string.selectPractice).textSize(25.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(15.dip)
      this += listView.onItemClick(onItemClick)

      val data = wordSets.map(set => mapAsJavaMap(Map("name" -> set.name, "words" -> set.words.mkString(",")))).toList
      listView.setAdapter(new SimpleAdapter(
        context, data, android.R.layout.simple_list_item_2,
        Array("name", "words"),
        Array(android.R.id.text1, android.R.id.text2)))
    }
  }

  private val onItemClick = (_: AdapterView[_], _: View, position: Int, _: Long) =>
    onPracticeSelected(wordSets.toList.get(position))

  def onPracticeSelected(wordSet: WordSet) = {
    persistence.saveActiveWordSet(language, wordSet.words)
    startActivity[ArticleActivity]
  }
}
