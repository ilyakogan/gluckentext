package com.gluckentext

import android.view.Gravity
import org.scaloid.common._

class SelectionActivity extends SActivity {

  implicit val tag = LoggerTag("Gluckentext")

  val languages = List(
    new Language("en", "English", List("in", "on", "at"), "The_Ugly_Duckling"),
    //new Language("fr", "French", List("en", "dans", "à"), "Le_Vilain_Petit_Canard"),
    new Language("de", "German", List("mit", "in", "von", "an"), "Das_hässliche_Entlein_(Märchen)"),
    new Language("es", "Spanish", List("en", "a", "sobre"), "El_patito_feo")
  )

  onCreate {
    val views = languages.map(l => (l.code, new SRadioButton(l.name)))
    contentView = new SVerticalLayout {
      STextView(R.string.selectLanguage).textSize(20.dip).gravity(Gravity.CENTER_HORIZONTAL).margin(5.dip)
      val group = SRadioGroup().gravity(Gravity.CENTER_HORIZONTAL)
      views.foreach { case (_, view) => group += view}
      SButton(R.string.selectButton).onClick(selectClicked())

      def selectLanguage(code: String) = views.foreach { case (c, view) => if (c == code) view.checked = true}

      def selectedLanguage: Language = {
        val checkedLanguages =
          for ((code, view) <- views; language <- languages
               if code == language.code && view.checked) yield language
        assert(checkedLanguages.size == 1)
        checkedLanguages.head
      }

      selectLanguage("de")

      def selectClicked() = {
        Preferences().language = selectedLanguage.code
        Preferences().lastArticleName = selectedLanguage.defaultArticle
        Preferences().practiceWords = selectedLanguage.practiceWords.mkString(",")
        Preferences().quiz = ""
        startActivity[ArticleActivity]
      }
    }
  }
}


