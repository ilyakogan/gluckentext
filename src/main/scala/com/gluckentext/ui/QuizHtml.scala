package com.gluckentext.ui

import com.gluckentext.quiz.{PlainText, QuizPart, QuizWord}

object QuizHtml {

  def getTagId(quizWord: QuizWord) = quizWord.id

  object makeGuessUrl {

    implicit class Regex(sc: StringContext) {
      def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
    }

    def apply(word: QuizWord) = "com.gluckentext://" + word.id //+ "/" + word.rightAnswer

    def unapply(args: (String, Iterable[QuizPart])): Option[QuizWord] =
      args match {
        case (url, quiz) =>
          url match {
            case r"com.gluckentext://(.+)${selectedId}" =>
              val selectedQuizPart = quiz.collect { case w: QuizWord if w.id.toString == selectedId => w }
              selectedQuizPart.toList match {
                case w :: tail => Some(w)
                case Nil => None
              }
            case _ => None
          }
        case _ => None
      }
  }

  object generateQuizHtml {
    private val styleTag =
      "<style> " +
        ".unsolved > span { display: none} " +
        ".unsolved > input { background-color: white; border-color: blue; width: 55px; } " +
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

    def getClass(w: QuizWord): String = {
      if (w.isSolved) "solved" else "unsolved"
    }

    def quizWordHtml(w: QuizWord): String =
      "<form style='display: inline' class='" + getClass(w) + "' id='" + getTagId(w) + "' action='" + makeGuessUrl(w) + "'>" +
        "<input type=\"submit\" id='" + getTagId(w) + "' value=''/>" +
        "<span>" + w.rightAnswer + "</span>" +
        "</form>"

    def apply(quiz: Iterable[QuizPart]) = {
      val body = quiz.map {
        case PlainText(text) => text
        case w@QuizWord(_, _, isSolved) => quizWordHtml(w)
      }.mkString
      header + body + footer
    }
  }

}
