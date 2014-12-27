package com.gluckentext

import com.gluckentext.datahandling.Serializer._
import com.gluckentext.quiz.QuizCreator
import org.scalatest.{FlatSpec, Matchers}

class SerializerTests extends FlatSpec with Matchers {
  behavior of "Serializer"

  it should "serialize a quiz into a non-empty string" in {
    val quiz = new QuizCreator(List("of"), None) createQuizFrom "article of words"
    assert(serializeQuizStatus(quiz).size > 0)
  }

  it should "be reversible" in {
    val quiz = new QuizCreator(List("of"), None) createQuizFrom "article of words"
    val serialized = serializeQuizStatus(quiz)
    val deserialized = deserializeToQuizStatus(serialized)
    assert(quiz === deserialized)
  }
}
