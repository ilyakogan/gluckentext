package com.gluckentext

object GenerateQuizHtml {
  def apply(quiz: List[QuizPart]) =
    quiz.map {
      case PlainText(text) => text
      case QuizWord(word) => "<a href='#'>_______</a>"
    }.mkString
}
