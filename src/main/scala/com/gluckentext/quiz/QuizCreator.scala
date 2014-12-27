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
  def from(article: String): List[QuizPart] = {
    val quizParts = splitArticleByPracticeWords(article, practiceWords)

    quizParts.map {
      case t: TextSubstring => PlainText(article.substring(t.start, t.end))
      case w: QuizWordSubstring => QuizWord(w.start, article.substring(w.start, w.end))
    }
  }
}

object createQuiz {
  def about(words: Iterable[CharSequence]) = new QuizCreator(words)
}