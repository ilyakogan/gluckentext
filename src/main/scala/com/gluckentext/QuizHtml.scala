package com.gluckentext

import java.util.regex.Pattern

object QuizHtml {

  def getTagId(quizWord : QuizWord) = quizWord.order

  object makeGuessUrl {
    implicit class Regex(sc: StringContext) {
      def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
    }

    def apply(word: QuizWord) = "com.gluckentext://" + word.order + "/" + word.rightAnswer

    def unapply(url: String): Option[QuizWord] =
      url match {
        case r"com.gluckentext://(.+)${order}/(.+)${word}" => Some(QuizWord(order.toInt, word))
      }
  }

  object generateQuizHtml {
    val header = "<html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' /></head>" +
      "<body style='line-height: 200%'>"
    val footer = "</body></html>"

    def apply(quiz: List[QuizPart]) = {
      val body = quiz.map {
        case PlainText(text) => text
        case w@QuizWord(_, _) => "<a id='" + getTagId(w) + "' href='" + makeGuessUrl(w) + "'>[_______]</a>"
      }.mkString
      header + body + footer
    }
  }
}
