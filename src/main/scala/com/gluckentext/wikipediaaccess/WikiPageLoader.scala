package com.gluckentext.wikipediaaccess

import com.gluckentext.quiz.{WikiArticle, WikiPageParser}

import scala.util.matching.Regex
import scala.xml.{Node, XML}

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
        case para@Paragraph(_) =>
          val newPair = pendingHeading match {
            case pendingHeading@Some(heading) => (pendingHeading, para)
            case None => (None, para)
          }
          buildPairsAcc(rest, acc ++ Some(newPair), None)
      }
    }

  def buildPairs(elements: Seq[Element]): List[(Option[Heading], Paragraph)] =
    buildPairsAcc(elements, List(), None)

  def loadArticleByTerm(language: String, term: String): WikiArticle = {
    val url = "https://%s.wikipedia.org/wiki/Special:Export/%s".format(language, term)
    loadArticleByUri(url)
  }

  def loadArticleByUri(url: String): WikiArticle = {
    val xml = XML.load(url)
    val title = WikiPageParser.getTitle(xml)
    val wikiPage = WikiPageParser.parseWikiPage(xml)
    val body = wikiPage.map(c => "<h3>%s</h3>%s".format(c.heading, c.body)).mkString
    new WikiArticle(title, body)
  }
}
