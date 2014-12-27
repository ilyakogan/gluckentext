package com.gluckentext.datahandling

import com.gluckentext.entities.WordSet

object LanguagePredefs {
  lazy val predefWordSets =
    Map("en" -> List(
      new WordSet("prepositions", List("in", "on", "at")),
      new WordSet("articles", List("a", "the")),
      new WordSet("than/then", List("than", "then")),
      new WordSet("that/which/who", List("that", "which", "who"))),
      "de" -> List(
        new WordSet("prepositions in the top 200 words", List("zu", "in", "mit", "auf", "von", "für", "an", "aus", "um", "im", "nach", "bei", "vor", "zum", "über", "am", "ab")),
        new WordSet("definitive articles", List("der", "das", "die", "den", "dem", "des"))),
      "es" -> List(
        new WordSet("prepositions", List("en", "a", "sobre"))))

  lazy val predefArticles =
    Map("en" ->("The Ugly Duckling", "https://en.wikipedia.org/wiki/Special:Export/The_Ugly_Duckling"),
      "de" ->("Das Hässliche Entlein", "https://de.wikipedia.org/wiki/Special:Export/Das_hässliche_Entlein_(Märchen)"),
      "es" ->("El patito feo", "https://es.wikipedia.org/wiki/Special:Export/El_patito_feo"))

}
