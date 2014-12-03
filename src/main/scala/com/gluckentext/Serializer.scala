package com.gluckentext

object Serializer {

  val quizWordPrefix = "QuizWord:"
  val plainTextPrefix = "PlainText:"

  def serializeQuiz(quiz: Iterable[QuizPart]): String = {
    quiz.map {
      case QuizWord(id, rightAnswer, isSolved) => quizWordPrefix + id + "~~" + rightAnswer + "~~" + isSolved
      case PlainText(text) => plainTextPrefix + text
    }.mkString("~#~#~")
  }

  def deserializeToQuiz(quizSerialized: String): Iterable[QuizPart] = {
    var parts = quizSerialized.split("~#~#~")
    parts.map(p => {
      if (p.startsWith(quizWordPrefix)) {
        var split = p.substring(quizWordPrefix.size).split("~~")
        QuizWord(split(0).toInt, split(1), split(2).toBoolean)
      }
      else {
        PlainText(p.substring(plainTextPrefix.size))
      }
    })
  }
}
