import com.gluckentext.WikiPageParser._
import com.gluckentext.{WikiPageParser, WikiPageLoader}

import scala.xml.XML


implicit class WikiString(s: String) {
  def remove(regex: String): String = {
    val res = s.replaceAll(regex, "")
    if (res != s) res.remove(regex) else res
  }

  def removeBlocks(startMarker: String, endMarker: String) = {
    def removeBlocksAcc(s: String, acc: String, nestingLevel: Int): String =
      if (s == "") acc
      else if (s.startsWith(startMarker)) {
        val tail = s.drop(startMarker.size)
        removeBlocksAcc(tail, acc, nestingLevel + 1)
      }
      else if (s.startsWith(endMarker)) {
        val tail = s.drop(endMarker.size)
        removeBlocksAcc(tail, acc, math.max(nestingLevel - 1, 0))
      }
      else {
        val head = s.head
        val tail = s.drop(1)

        if (nestingLevel == 0) removeBlocksAcc(tail, acc + head, nestingLevel)
        else removeBlocksAcc(tail, acc, nestingLevel)
      }
    removeBlocksAcc(text, "", 0)
  }

  def strip(regex: String): String = s.replaceAll(regex, "$1")
}

//
//val language = "en"
//val term = "Tatarstan"
//val url = "https://%s.wikipedia.org/wiki/Special:Export/%s".format(language, term)
//val xml = XML.load(url)
//val title = (xml \\ "title").text
//val text = (xml \\ "text").text


val cleanText = WikiPageParser.clean(text)
text.split("==+").size


WikiPageLoader.loadWikiPageXml("en", "Tatarstan")

