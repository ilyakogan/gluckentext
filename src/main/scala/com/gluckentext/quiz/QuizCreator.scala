package com.gluckentext.quiz

trait QuizPart

case class PlainText(text: CharSequence) extends QuizPart

case class QuizWord(id: Int, rightAnswer: CharSequence, isSolved: Boolean) extends QuizPart {
  def solved = QuizWord(id, rightAnswer, isSolved = true)
}

object QuizWord {
  def apply(id: Int, rightAnswer: CharSequence) = new QuizWord(id, rightAnswer, isSolved = false)
}

class QuizCreator(practiceWords: Iterable[CharSequence], minCharsBetweenQuizWords: Option[Int]) {

  class Diluter(minCharsBetween: Int) {
    def diluteQuizWords(substrings: List[Substring]): List[Substring] = {
      substrings match {
        case TextSubstring(start1, end1) :: QuizWordSubstring(_, _) :: TextSubstring(_, end2) :: tail
          if end1 - start1 < minCharsBetween =>
          diluteQuizWords(TextSubstring(start1, end2) :: tail)
        case head :: tail => head :: diluteQuizWords(tail)
        case Nil => Nil
      }
    }
  }

  def createQuizFrom(article: String): List[QuizPart] = {
    val quizParts = splitArticleByPracticeWords(article, practiceWords)

    val dilutedQuizParts =
      minCharsBetweenQuizWords match {
        case None => quizParts
        case Some(minDistance) => new Diluter(minDistance).diluteQuizWords(quizParts)
      }


    dilutedQuizParts.map {
      case t: TextSubstring => PlainText(article.substring(t.start, t.end))
      case w: QuizWordSubstring => QuizWord(w.start, article.substring(w.start, w.end))
    }
  }
}