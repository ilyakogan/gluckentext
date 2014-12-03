package com.gluckentext

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object Serializer {

  def serializeQuiz(quiz: List[QuizPart]): String = {
    val quizJson =
      "quiz" ->
        ("parts" ->
          quiz.map {
            case QuizWord(id, rightAnswer, isSolved) => ("type" -> "quizWord") ~ ("id" -> id) ~ ("rightAnswer" -> rightAnswer.toString) ~ ("isSolved" -> isSolved)
            case PlainText(text) => ("type" -> "plainText") ~ ("text" -> text.toString)
          })

    compact(render(quizJson))
  }

  def deserializeToQuiz(quizSerialized: String): Iterable[QuizPart] = {
    val json = parse(quizSerialized)

    val data: Iterable[(String, BigInt, String, Boolean, String)] = for {
      JObject(quizPart) <- json
      JField("type", JString(typ)) <- quizPart
      JField("id", JInt(id)) <- quizPart
      JField("rightAnswer", JString(rightAnswer)) <- quizPart
      JField("isSolved", JBool(isSolved)) <- quizPart
      JField("text", JString(text)) <- quizPart
    } yield (typ, id, rightAnswer, isSolved, text)

    val quiz = data map {
      case ("quizWord", id, rightAnswer, isSolved, _) => QuizWord(id.intValue(), rightAnswer, isSolved)
      case (_, _, _, _, text) => PlainText(text)
    }
    quiz
  }
}
