package com.gluckentext

import com.gluckentext.quiz._
import org.scalatest.{Matchers, FlatSpec}

class QuizCreatorTests extends FlatSpec with Matchers {
  behavior of "QuizCreator - no dilution"

  def simpleCreator(words: Iterable[CharSequence]) = new QuizCreator(words, None)

  it should "find a quiz word and split the article according to it" in {
    val quiz = simpleCreator(List("of")) createQuizFrom "article of words"
    assert(quiz === List(PlainText("article "), QuizWord(8, "of"), PlainText(" words")))
  }

  it should "find multiple quiz words" in {
    val quiz = simpleCreator(List("at", "am", "and")) createQuizFrom "I am good at maths and chemistry"
    assert(quiz === List(PlainText("I "), QuizWord(2, "am"), PlainText(" good "), QuizWord(10, "at"), PlainText(" maths "), QuizWord(19, "and"), PlainText(" chemistry")))
  }

  it should "be case-insensitive" in {
    val quiz = simpleCreator(List("of")) createQuizFrom "Yes. Of course!"
    assert(quiz === List(PlainText("Yes. "), QuizWord(5, "Of"), PlainText(" course!")))
  }

  it should "identify quiz word if it's the first word in the article" in {
    val quiz = simpleCreator(List("of")) createQuizFrom "Of course"
    assert(quiz === List(QuizWord(0, "Of"), PlainText(" course")))
  }

  it should "identify quiz word if it's the last word in the article" in {
    val quiz = simpleCreator(List("by")) createQuizFrom "come by"
    assert(quiz === List(PlainText("come "), QuizWord(5, "by")))
  }

  it should "identify quiz word if there's punctuation after it" in {
    val quiz = simpleCreator(List("morning", "noon", "evening", "night")) createQuizFrom "Morning, noon. Evening? Night!"
    assert(quiz === List(QuizWord(0, "Morning"), PlainText(", "), QuizWord(9, "noon"), PlainText(". "), QuizWord(15, "Evening"), PlainText("? "), QuizWord(24, "Night"), PlainText("!")))
  }

  it should "throw a NotEnoughTextInArticleException if quiz word is not found" in {
    intercept[NotEnoughTextInArticleException] {
      simpleCreator(List("word")) createQuizFrom "article"
    }
  }

  it should "throw a NotEnoughTextInArticleException if article is empty" in {
    intercept[NotEnoughTextInArticleException] {
      simpleCreator(List("word")) createQuizFrom ""
    }
  }
}
