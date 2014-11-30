package com.gluckentext

import java.util.regex.Pattern

object GenerateQuizHtml {
  def apply(quiz: List[QuizPart]) =
    quiz.map {
      case PlainText(text) => text
      case w @ QuizWord(order, _) => "<a id='" + order + "' href='" + makeGuessUrl(w) + "'>[_______]</a>"
    }.mkString
}

object makeGuessUrl {
  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def apply(word: QuizWord) = "com.gluckentext://" + word.order + "/" + word.rightAnswer
  def unapply(url: String): Option[QuizWord] =
    url match { case r"com.gluckentext://(.+)${order}/(.+)${word}" => Some(QuizWord(order.toInt, word)) }
}
