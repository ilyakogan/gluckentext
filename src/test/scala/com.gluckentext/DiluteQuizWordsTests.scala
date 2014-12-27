package com.gluckentext

import com.gluckentext.quiz.{NotEnoughTextInArticleException, PlainText, QuizWord, QuizCreator}
import org.scalatest.{Matchers, FlatSpec}

class DiluteQuizWordsTests extends FlatSpec with Matchers {
  behavior of "QuizCreator - dilution"

  val quizCreator = new QuizCreator(List("QQQ"), Some(30))

  it should "not dilute if quiz words are far apart" in {
    val quiz = quizCreator.createQuizFrom(
      "some long prefix in the beginning QQQ a little more than thirty characters QQQ a b")
    assert(quiz.collect { case w: QuizWord => w}.size === 2)
  }

  it should "cancel second word if it's too close to the first one" in {
    val quiz = quizCreator.createQuizFrom(
      "some long prefix in the beginning QQQ 8chars QQQ a b")
    assert(quiz.collect { case w: QuizWord => w}.size === 1)
    assert(quiz(2) === PlainText(" 8chars QQQ a b"))
  }

  it should "cancel two words if both are too close to the first one, but leave the next one which is far enough" in {
    val quiz = quizCreator.createQuizFrom(
      "some long prefix in the beginning QQQ 8chars QQQ 8chars QQQ 8chars QQQ m n")
    assert(quiz.collect { case w: QuizWord => w}.size === 2)
    assert(quiz(2) === PlainText(" 8chars QQQ 8chars QQQ 8chars "))
    assert(quiz(4) === PlainText(" m n"))
  }

  it should "throw NotEnoughTextInArticleException if the quiz has no quiz words" in {
    intercept[NotEnoughTextInArticleException] {
      val quiz = quizCreator.createQuizFrom("a b c d")
    }
  }
}
