package com.gluckentext

import org.scalatest._


class WikiPageLoaderTests extends FlatSpec with Matchers {
<<<<<<< HEAD

  List("Tatarstan", "Russian_Empire", "Russian_language", "Russia", "Moscow").foreach(title => {

    behavior of "Article " + title + " after parsing"
    val article = WikiPageLoader.loadWikiPageXml("en", title)

    it should "contain keyword Russia" in {
=======
  behavior of "WikiPageLoader"

  List("Tatarstan", "Russian_Empire", "Russian_language", "Russia", "Moscow").foreach(title => {
    val article = WikiPageLoader.loadWikiPageXml("en", title)
    "Article " + title + " after parsing" should "contain keyword Russia" in {
>>>>>>> bebb77ef7227e3ea5592f126b22d5d9011b3ee96
      assert(article.contains("Russia"))
    }

    "|{}[]()".foreach(char => {
      it should "not contain " + char in {
        assert(!article.contains(char))
      }
    })

    it should "be long" in {
      assert(article.length > 100)
    }

  })
}
