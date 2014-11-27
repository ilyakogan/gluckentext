package com.gluckentext

import org.scalatest.{Matchers, FlatSpec}

class QuizFinderTests extends FlatSpec with Matchers {
  behavior of "QuizFinder"

  it should "find the quiz words and split the article according to them" in {
    val finder = new QuizFinder("of")
    val result = finder.split("article of words")
    assert(result === List(PlainText("article ", QuizWord("of"), PlainText(" words"))))
  }
}
