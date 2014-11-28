package com.gluckentext


trait QuizPart

case class PlainText(text: CharSequence) extends QuizPart

case class QuizWord(word: CharSequence) extends QuizPart

class QuizParameters(val words: Iterable[CharSequence]) {
}

class QuizCreator(params: QuizParameters) {
  def from(article: String) = {
    val caseInsensitive = "(?i)"
    val quizWordRegex = (caseInsensitive + "\\b(" + params.words.mkString("|") + ")\\b").r

    def splitAcc(remainingArticle: CharSequence, acc: List[QuizPart]): List[QuizPart] =
      if (remainingArticle == "") acc
      else quizWordRegex.findFirstMatchIn(remainingArticle) match {
        case None => acc :+ PlainText(remainingArticle)
        case Some(m) if m.start == 0 => splitAcc(m.after, acc :+ QuizWord(m.matched))
        case Some(m) => splitAcc(m.after, acc :+ PlainText(m.before) :+ QuizWord(m.matched))
      }

    splitAcc(article, List())
  }
}

object createQuiz {
  def about(words: Iterable[CharSequence]) = new QuizCreator(new QuizParameters(words))
}