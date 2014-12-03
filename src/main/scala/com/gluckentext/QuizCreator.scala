package com.gluckentext

trait QuizPart

case class PlainText(text: CharSequence) extends QuizPart

case class QuizWord(id: Int, rightAnswer: CharSequence, isSolved: Boolean) extends QuizPart {
  def solved = QuizWord(id, rightAnswer, isSolved = true)
}

class QuizParameters(val words: Iterable[CharSequence]) {
}

class QuizCreator(params: QuizParameters) {

  trait Substring {
    def start: Int

    def end: Int
  }

  case class TextSubstring(start: Int, end: Int) extends Substring

  case class QuizWordSubstring(start: Int, end: Int) extends Substring

  def from(article: String): List[QuizPart] = {
    val caseInsensitive = "(?i)"
    val quizWordRegex = (caseInsensitive + "\\b(" + params.words.mkString("|") + ")\\b").r
    val quizWordMatches = quizWordRegex.findAllMatchIn(article)
    val quizWords = (quizWordMatches map (m => QuizWordSubstring(m.start, m.end))).toStream

    val quizPartsExceptLast = quizWords.foldLeft(List[Substring]())(
      (acc, nextWord: QuizWordSubstring) =>
        if (nextWord.start > 0)
          acc :+ TextSubstring(acc.lastOption.map(_.end).getOrElse(0), nextWord.start) :+ nextWord
        else acc :+ nextWord)

    val lastPos = quizPartsExceptLast.last.end
    val quizParts =
      if (lastPos == article.length) quizPartsExceptLast
      else quizPartsExceptLast :+ TextSubstring(lastPos, article.length)

    quizParts.map {
      case t: TextSubstring => PlainText(article.substring(t.start, t.end))
      case w: QuizWordSubstring => QuizWord(w.start, article.substring(w.start, w.end), false)
    }
  }
}

object createQuiz {
  def about(words: Iterable[CharSequence]) = new QuizCreator(new QuizParameters(words))
}