package com.gluckentext

import java.util.regex.Pattern

object QuizHtml {

  def getTagId(quizWord: QuizWord) = quizWord.id

  object makeGuessUrl {

    implicit class Regex(sc: StringContext) {
      def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
    }

    def apply(word: QuizWord) = "com.gluckentext://" + word.id + "/" + word.rightAnswer

    def unapply(url: String): Option[QuizWord] =
      url match {
        case r"com.gluckentext://(.+)${order}/(.+)${word}" => Some(QuizWord(order.toInt, word))
      }
  }

  object generateQuizHtml {
    private val styleTag =
      "<style> " +
        ".unsolved > span { display: none} " +
        ".unsolved > input { background-color: white; border-color: green; width: 70px; } " +
        ".solved > span { color: green; font-weight: bold } " +
        ".solved > input { display: none } " +
        " </style>"
    private val scriptTag =
      "<script>" +
        "function markSolved(id) { document.getElementById(id).className='solved'; } " +
        "</script>"
    val header = "<html><head>" +
      "<meta http-equiv='Content-Type' content='text/html' charset='UTF-8' />" +
      styleTag +
      scriptTag +
      "</head>" +
      "<body style='line-height: 200%'>"
    val footer = "</body></html>"

    def apply(quiz: List[QuizPart]) = {
      val body = quiz.map {
        case PlainText(text) => text
        case w@QuizWord(_, _) =>
          "<form style='display: inline' class='unsolved' id='" + getTagId(w) + "' action='" + makeGuessUrl(w) + "'>" +
            "<input type=\"submit\" id='" + getTagId(w) + "' value=''/>" +
            "<span>" + w.rightAnswer + "</span>" +
            "</form>"
      }.mkString
      header + body + footer
    }
  }

}
