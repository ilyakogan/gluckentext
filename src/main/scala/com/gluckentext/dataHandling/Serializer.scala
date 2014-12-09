package com.gluckentext.datahandling

import com.gluckentext.quiz.{PlainText, QuizPart, QuizWord}

object Serializer {

  val quizWordPrefix = "QuizWord:"
  val plainTextPrefix = "PlainText:"

  def serializeQuizStatus(quizParts: Iterable[QuizPart]): String = {
    quizParts.map {
      case QuizWord(id, rightAnswer, isSolved) => quizWordPrefix + id + "~~" + rightAnswer + "~~" + isSolved
      case PlainText(text) => plainTextPrefix + text
    }.mkString("~#~#~")
  }

  def deserializeToQuizStatus(quizSerialized: String): Iterable[QuizPart] = {
    val parts = quizSerialized.split("~#~#~")
    parts.map(p => {
      if (p.startsWith(quizWordPrefix)) {
        val split = p.substring(quizWordPrefix.size).split("~~")
        QuizWord(split(0).toInt, split(1), split(2).toBoolean)
      }
      else {
        PlainText(p.substring(plainTextPrefix.size))
      }
    })
  }
}
