import net.liftweb.json.JsonAST.{JString, JField, JValue}
import net.liftweb.json.`package`.parse

import scala.io.Source
import net.liftweb.json._

val content = Source.fromURL("http://en.wikipedia.org/w/api.php?action=query&list=random&rnlimit=225&rnnamespace=0&format=json")
val str = content.mkString
val json : JValue = parse(str)
pretty(render(json))
//json \\ "random"
val filtered = json filter (_ => true)
filtered

filtered map {
  case net.liftweb.json.JsonAST.JField("title", net.liftweb.json.JsonAST.JString(s)) =>
    s
}

json filter (_ => true) map {
  case net.liftweb.json.JsonAST.JField("title", net.liftweb.json.JsonAST.JString(s)) =>
    s
}
filtered map {
  case net.liftweb.json.JsonAST.JField("title", net.liftweb.json.JsonAST.JString(s)) =>
    s
}