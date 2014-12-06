package com.gluckentext

class Language(val code: String, val name: String, val practiceTypes: Map[String, Iterable[String]], val defaultArticle: String)

object Languages {
  def get(code: String) = all.find(_.code == code)

  lazy val all = List(
    new Language("en", "English",
      Map(
        "prepositions" -> List("in", "on", "at"),
        "articles" -> List("a", "the"),
        "that/which/who" -> List("that", "which", "who")),
      "The_Ugly_Duckling"),
    //new Language("fr", "French", List("en", "dans", "à"), "Le_Vilain_Petit_Canard"),
    new Language("de", "German",
      Map(
        "prepositions" -> List("mit", "in", "von", "an"),
        "definite articles" -> List("der", "das", "die", "den", "dem")),
      "Das_hässliche_Entlein_(Märchen)"),
    new Language("es", "Spanish",
      Map(
        "prepositions" -> List("en", "a", "sobre")),
      "El_patito_feo")
  )
}