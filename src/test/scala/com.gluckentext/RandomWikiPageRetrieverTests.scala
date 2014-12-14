package com.gluckentext

import com.gluckentext.wikipediaaccess.RandomWikiPageRetriever
import org.scalatest.{Matchers, FlatSpec}

import scala.io.Source
import scala.xml.XML

class RandomWikiPageRetrieverTests extends FlatSpec with Matchers {
  behavior of "RandomWikiPageRetriever"

  val articles = RandomWikiPageRetriever.retrieveRandomPages("en")

  it should "output at least 5 articles" in {
    assert(articles.size >= 5)
  }

  articles.foreach(a => {
    it should "generate existing article for title " + a.title in {
      println("URL is " + a.url)
      val articleXmlRoot = XML.load(a.url)
      val articleText = (articleXmlRoot \\ "text").text
      assert(articleText.size > 0)
    }
  })
}
