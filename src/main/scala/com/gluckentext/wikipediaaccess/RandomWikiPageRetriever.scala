package com.gluckentext.wikipediaaccess

import java.net.URLEncoder

import net.liftweb.json.JsonAST._
import net.liftweb.json.`package`.parse
import scala.io.Source

case class WikiArticleLocation(title: String, url: String) {
  override def toString = title
}

object RandomWikiPageRetriever {

  def retrieveRandomPages(languageCode: String): Stream[WikiArticleLocation] = {
    val titles = getArticleTitles(languageCode)
    val urls = titles.map(generateWikipediaArticleUrl(languageCode))
    titles.zip(urls).map { case (title, url) => new WikiArticleLocation(title, url)}.toStream
  }

  def generateWikipediaArticleUrl(languageCode: String)(title: String) = {
    val withUnderscores = title.replace(' ', '_')
    val urlSuffix = URLEncoder.encode(withUnderscores, "UTF-8")
    val url = "https://%s.wikipedia.org/wiki/Special:Export/%s".format(languageCode, urlSuffix)
    url
  }

  def getArticleTitles(languageCode: String): List[String] = {
    val uri = getUri(languageCode)
    val content = Source.fromURL(uri)
    val json = parse(content.mkString)
    val articleTitles: List[String] = json filter {
      case JField("title", JString(_)) => true
      case _ => false
    } map {
      case JField("title", JString(title)) => title
    }
    articleTitles
  }

  def getUri(languageCode: String): String = {
    "http://%s.wikipedia.org/w/api.php?action=query&list=random&rnlimit=10&continue=&rnnamespace=0&format=json&utf8=true".
      format(languageCode)
  }
}
