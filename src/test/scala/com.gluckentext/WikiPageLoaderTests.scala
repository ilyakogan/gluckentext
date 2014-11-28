package com.gluckentext

import org.scalatest._


class WikiPageLoaderTests extends FlatSpec with Matchers {

  List("Tatarstan", "Russian_Empire", "Russian_language", "Russia", "Moscow").foreach(title => {

    behavior of "Article " + title + " after parsing"
    val article = WikiPageLoader.loadWikiPageXml("en", title)

    it should "contain keyword Russia" in {
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
