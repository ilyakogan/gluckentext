package com.gluckentext.datahandling

import com.gluckentext.entities.WordSet

object LanguagePredefs {
  lazy val predefWordSets =
    Map("en" -> List(
      new WordSet("prepositions", List("in", "on", "at")),
      new WordSet("articles", List("a", "the")),
      new WordSet("that/which/who", List("that", "which", "who"))),
      "de" -> List(
        new WordSet("prepositions", List("mit", "in", "im", "von", "an", "bei")),
        new WordSet("definite articles", List("der", "das", "die", "den", "dem"))),
      "es" -> List(
        new WordSet("prepositions", List("en", "a", "sobre"))))

  lazy val predefArticles =
    Map("en" ->("The Ugly Duckling", "https://en.wikipedia.org/wiki/Special:Export/The_Ugly_Duckling"),
      "de" ->("Das Hässliche Entlein", "https://de.wikipedia.org/wiki/Special:Export/Das_hässliche_Entlein_(Märchen)"),
      "es" ->("El patito feo", "https://es.wikipedia.org/wiki/Special:Export/El_patito_feo"))

}
