package com.gluckentext

import org.scalatest.{Matchers, FlatSpec}
import Serializer._

class SerializerTests extends FlatSpec with Matchers {
  behavior of "Serializer"

  it should "serialize a quiz into a non-empty string" in {
    val quiz = createQuiz about List("of") from "article of words"
    assert(serializeQuiz(quiz).size > 0)
  }

  it should "be reversible" in {
    val quiz = createQuiz about List("of") from "article of words"
    val serialized = serializeQuiz(quiz)
    val deserialized = deserializeToQuiz(serialized)
    assert(quiz === deserialized)
  }
}
