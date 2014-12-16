package com.gluckentext.quiz

trait QuizPart

case class PlainText(text: CharSequence) extends QuizPart

case class QuizWord(id: Int, rightAnswer: CharSequence, isSolved: Boolean) extends QuizPart {
  def solved = QuizWord(id, rightAnswer, isSolved = true)
}

object QuizWord {
  def apply(id: Int, rightAnswer: CharSequence) = new QuizWord(id, rightAnswer, isSolved = false)
}

class QuizCreator(practiceWords: Iterable[CharSequence]) {

  trait Substring {
    def start: Int

    def end: Int
  }

  case class TextSubstring(start: Int, end: Int) extends Substring

  case class QuizWordSubstring(start: Int, end: Int) extends Substring

  def from(article: String): List[QuizPart] = {
    val caseInsensitive = "(?i)"
    val quizWordRegex = (caseInsensitive + "\\b(" + practiceWords.mkString("|") + ")\\b").r
    val quizWordMatches = quizWordRegex.findAllMatchIn(article)
    val quizWords = (quizWordMatches map (m => QuizWordSubstring(m.start, m.end))).toStream

    val quizPartsExceptLast = quizWords.foldLeft(List[Substring]())(
      (acc, nextWord: QuizWordSubstring) =>
        if (nextWord.start > 0)
          acc :+ TextSubstring(acc.lastOption.map(_.end).getOrElse(0), nextWord.start) :+ nextWord
        else acc :+ nextWord)

    if (quizPartsExceptLast.isEmpty) throw new NotEnoughTextInArticleException

    val lastPos = quizPartsExceptLast.last.end

    val quizParts =
      if (lastPos == article.length) quizPartsExceptLast
      else quizPartsExceptLast :+ TextSubstring(lastPos, article.length)

    quizParts.map {
      case t: TextSubstring => PlainText(article.substring(t.start, t.end))
      case w: QuizWordSubstring => QuizWord(w.start, article.substring(w.start, w.end))
    }
  }
}

object createQuiz {
  def about(words: Iterable[CharSequence]) = new QuizCreator(words)
}