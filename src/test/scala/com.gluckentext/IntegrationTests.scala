package com.gluckentext

import com.gluckentext.datahandling.Serializer
import com.gluckentext.quiz.{QuizWord, QuizCreator}
import com.gluckentext.wikipediaaccess.WikiPageLoader
import org.scalatest.{Matchers, FlatSpec}

class IntegrationTests extends FlatSpec with Matchers {
  behavior of "WikiPageLoader and QuizCreator"

  it should "leave diacritics decoded" in {
    val article = WikiPageLoader.loadArticleByTerm("de", "The_Event")
    assert(article.body.contains("für"))
    val quiz = new QuizCreator(List("für"), None).createQuizFrom(article.body)
    val serialized = Serializer.serializeQuizStatus(quiz)
    val deserializedQuiz = Serializer.deserializeToQuizStatus(serialized)
    val rightAnswers = quiz.collect { case QuizWord(_, rightAnswer, _) => rightAnswer}
    assert(rightAnswers(0).toString.toLowerCase === "für")
  }
}
