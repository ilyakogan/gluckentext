package com.gluckentext

import com.gluckentext.wikipediaaccess.WikiPageLoader
import org.scalatest._


class WikiPageLoaderTests extends FlatSpec with Matchers {

  List("Tatarstan", "Russian_Empire", "Russian_language", "Russia", "Moscow").foreach(title => {

    behavior of "WikiPageLoader - Article " + title + " after parsing"
    val article = WikiPageLoader.loadArticleByTerm("en", title)

    it should "contain keyword Russia" in {
      assert(article.body.contains("Russia"))
    }

    "|{}[]()".foreach(char => {
      it should "not contain " + char in {
        assert(!article.body.contains(char))
      }
    })

    it should "be long" in {
      assert(article.body.length > 100)
    }

  })
}


