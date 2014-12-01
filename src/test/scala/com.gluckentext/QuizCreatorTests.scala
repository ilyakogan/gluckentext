package com.gluckentext

import org.scalatest.{Matchers, FlatSpec}

class QuizCreatorTests extends FlatSpec with Matchers {
  behavior of "QuizCreator"

  it should "find a quiz word and split the article according to it" in {
    val quiz = createQuiz about List("of") from "article of words"
    assert(quiz === List(PlainText("article "), QuizWord(8, "of"), PlainText(" words")))
  }

  it should "find multiple quiz words" in {
    val quiz = createQuiz about List("at", "am", "and") from "I am good at maths and chemistry"
    assert(quiz === List(PlainText("I "), QuizWord(2, "am"), PlainText(" good "), QuizWord(10, "at"), PlainText(" maths "), QuizWord(19, "and"), PlainText(" chemistry")))
  }

  it should "be case-insensitive" in {
    val quiz = createQuiz about List("of") from "Yes. Of course!"
    assert(quiz === List(PlainText("Yes. "), QuizWord(5, "Of"), PlainText(" course!")))
  }

  it should "identify quiz word if it's the first word in the article" in {
    val quiz = createQuiz about List("of") from "Of course"
    assert(quiz === List(QuizWord(0, "Of"), PlainText(" course")))
  }

  it should "identify quiz word if it's the last word in the article" in {
    val quiz = createQuiz about List("by") from "come by"
    assert(quiz === List(PlainText("come "), QuizWord(5, "by")))
  }

  it should "identify quiz word if there's punctuation after it" in {
    val quiz = createQuiz about List("morning", "noon", "evening", "night") from "Morning, noon. Evening? Night!"
    assert(quiz === List(QuizWord(0, "Morning"), PlainText(", "), QuizWord(9, "noon"), PlainText(". "), QuizWord(15, "Evening"), PlainText("? "), QuizWord(24, "Night"), PlainText("!")))
  }
}
