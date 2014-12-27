package com.gluckentext.quiz

trait Substring {
  def start: Int

  def end: Int
}

case class TextSubstring(start: Int, end: Int) extends Substring

case class QuizWordSubstring(start: Int, end: Int) extends Substring

object splitArticleByPracticeWords {

  def apply(article: String, practiceWords: Iterable[CharSequence]): List[Substring] = {
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
    quizParts
  }
}
