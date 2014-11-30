package com.gluckentext


trait QuizPart

case class PlainText(text: CharSequence) extends QuizPart

case class QuizWord(order: Int, rightAnswer: CharSequence) extends QuizPart

class QuizParameters(val words: Iterable[CharSequence]) {
}

class QuizCreator(params: QuizParameters) {
  def from(article: String) = {
    val caseInsensitive = "(?i)"
    val quizWordRegex = (caseInsensitive + "\\b(" + params.words.mkString("|") + ")\\b").r

    def splitAcc(remainingArticle: CharSequence, acc: List[QuizPart], quizWordCount: Int): List[QuizPart] =
      if (remainingArticle == "") acc
      else quizWordRegex.findFirstMatchIn(remainingArticle) match {
        case None =>
          acc :+ PlainText(remainingArticle)
        case Some(m) if m.start == 0 =>
          splitAcc(m.after, acc :+ QuizWord(quizWordCount, m.matched), quizWordCount + 1)
        case Some(m) =>
          splitAcc(m.after, acc :+ PlainText(m.before) :+ QuizWord(quizWordCount, m.matched), quizWordCount + 1)
      }

    splitAcc(article, List(), 0)
  }
}

object createQuiz {
  def about(words: Iterable[CharSequence]) = new QuizCreator(new QuizParameters(words))
}