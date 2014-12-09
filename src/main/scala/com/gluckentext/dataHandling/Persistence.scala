package com.gluckentext.datahandling

import android.content.Context
import com.gluckentext.entities._
import com.gluckentext.quiz.QuizPart
import com.gluckentext.datahandling.Serializer._
import org.scaloid.common.Preferences

class Persistence(implicit val ctx: Context) {

  def saveQuizDefinition(languageCode: String, quizDefinition: QuizDefinition): Unit = {
    val scope = languageCode + "_"
    Preferences().updateDynamic(scope + "practiceWords")(quizDefinition.practiceWords.mkString(","))
    Preferences().updateDynamic(scope + "quizArticleName")(quizDefinition.articleName)
    Preferences().updateDynamic(scope + "quizArticleUri")(quizDefinition.articleUri)
  }

  def loadQuizDefinition(languageCode: String): QuizDefinition = {
    val scope = languageCode + "_"
    val practiceWords = Preferences().applyDynamic(scope + "practiceWords")("").split(",")
    val articleName = Preferences().applyDynamic(scope + "quizArticleName")("")
    val articleUri = Preferences().applyDynamic(scope + "quizArticleUri")("")
    (practiceWords, articleName, articleUri) match {
      case (words, name, uri) if words.nonEmpty && name != "" && uri != "" =>
        new QuizDefinition(words, name, uri)
      case _ =>
        fallbackQuizDefinition(languageCode)
    }
  }

  def loadQuizStatus(quizDefinition: QuizDefinition): Option[Iterable[QuizPart]] = {
    Preferences().applyDynamic(makeQuizStatusKey(quizDefinition))("") match {
      case "" => None
      case q => Some(deserializeToQuizStatus(q))
    }
  }

  def saveQuizStatus(quizDefinition: QuizDefinition, quizStatus: Iterable[QuizPart]) = {
    val quizSerialized: String = serializeQuizStatus(quizStatus)
    Preferences().updateDynamic(makeQuizStatusKey(quizDefinition))(quizSerialized)
  }

  def makeQuizStatusKey(quizDefinition: QuizDefinition): String = {
    val hash = quizDefinition.articleUri + "_" + quizDefinition.practiceWords.mkString(",")
    hash + "_status"
  }

  def saveActiveLanguage(languageCode: String) =
    Preferences().activeLanguage = languageCode

  def loadActiveLanguage = Preferences().activeLanguage("en")

  def saveActiveWordSet(languageCode: String, practiceWords: Iterable[String]) =
    Preferences().updateDynamic(languageCode + "_activeWords")(practiceWords.mkString(","))

  def loadActiveWordSet(languageCode: String): Iterable[String] =
    Preferences().applyDynamic(languageCode + "_activeWords")("") match {
      case "" => fallbackActiveWordSet(languageCode)
      case str => str.split(",")
    }

  def saveAvailableLanguages(languages: Iterable[Language]) =
    Preferences().availableLanguages = languages.map(l => l.code + "," + l.name).mkString(";")

  def loadAvailableLanguages =
    Preferences().availableLanguages("en,English;de,German;es,Spanish")
      .split(";")
      .map(_.split(",", 2))
      .map { case Array(code, name) => new Language(code, name)}

  def saveAvailableWordSets(languageCode: String, wordSets: Iterable[WordSet]) = {
    val serialized = wordSets
      .map(s => s.name + "=" + s.words.mkString(","))
      .mkString(";")
    Preferences().updateDynamic(languageCode + "_availableWords")(serialized)
  }

  def loadAvailableWordSets(languageCode: String): Iterable[WordSet] = {
    val serialized = Preferences().applyDynamic(languageCode + "_availableWords")("")
    serialized match {
      case "" => fallbackAvailableWordSets(languageCode)
      case _ =>
        serialized
          .split(";")
          .map(_.split("=", 2))
          .map { case Array(name, words) => new WordSet(name, words.split(","))
        }
    }
  }

  def fallbackAvailableWordSets(languageCode: String) = LanguagePredefs.predefWordSets(languageCode)

  def fallbackActiveWordSet(languageCode: String) = loadAvailableWordSets(languageCode).head.words

  def fallbackQuizDefinition(languageCode: String) = {
    val predefArticle = LanguagePredefs.predefArticles(languageCode)
    new QuizDefinition(loadActiveWordSet(languageCode), predefArticle._1, predefArticle._2)
  }
}
