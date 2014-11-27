package com.gluckentext

import scala.util.matching.Regex
import scala.xml.{XML, NodeSeq, Node}

object WikiPageLoader {

  private def makePlainText(paragraph: Node): String = {
    val refRegex = new Regex("\\[\\d+\\]")
    refRegex.replaceAllIn(paragraph.text, "")
  }

  abstract class Element {
    def text: String
  }

  case class Paragraph(node: Node) extends Element {
    override def text: String = makePlainText(node)
  }

  case class Heading(mainNode: Node, textNode: Node) extends Element {
    override def text: String = makePlainText(textNode)

    def level: Int = mainNode.label.substring(1).toInt
  }

  def buildPairsAcc(elements: Seq[Element], acc: List[(Option[Heading], Paragraph)], pendingHeading: Option[Heading]):
  List[(Option[Heading], Paragraph)] =
    elements match {
      case Nil => acc
      case first :: rest => first match {
        case heading@Heading(_, _) => buildPairsAcc(rest, acc, Some(heading))
        case para@Paragraph(_) => {
          val newPair = pendingHeading match {
            case pendingHeading@Some(heading) => (pendingHeading, para)
            case None => (None, para)
          }
          buildPairsAcc(rest, acc ++ Some(newPair), None)
        }
      }
    }


  def buildPairs(elements: Seq[Element]): List[(Option[Heading], Paragraph)] =
    buildPairsAcc(elements, List(), None)


  def loadWikiPageXml(language: String, term: String): String = {
    val url = "https://%s.wikipedia.org/wiki/Special:Export/%s".format(language, term)
    val xml = XML.load(url)
    val wikiPage = WikiPageParser.parseWikiPage(xml)
    wikiPage.map(c => "<h3>%s</h3><p>%s</p>".format(c.heading, c.body)).mkString
  }
//    val root = TagSoupXmlLoader.get().load(url)
//    def isParent(n: Node) = (n \ "@id").text == "mw-content-text"
//    val parentsOfText = (root \\ "div" filter isParent)
//    return (root \\ "div" map (n => (n \ "@class").text)).mkString(", ")
//    val parentOfText = parentsOfText.head
//    val headingsAndParas =
//      parentOfText.child.collect {
//        case n if (n.label == "p") => Paragraph(n)
//        case n if (n.label.matches("h\\d")) => Heading(n, n.child.head)
//      }
//    val pairs = buildPairs(headingsAndParas)
//    val html = pairs.map {
//      case (heading, para) =>
//        "<p>" + heading.map(_.text).getOrElse("No heading") + "<br><br>" + para.text + "</p>"
//    }.mkString
//    html
//  }
}
