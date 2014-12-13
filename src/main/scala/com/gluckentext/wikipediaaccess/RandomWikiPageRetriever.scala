package com.gluckentext.wikipediaaccess

object RandomWikiPageRetriever {
  def retrieveRandomPage(languageCode: String) = {
    val uri = "http://%s.wikipedia.org/w/api.php?action=query&list=random&rnlimit=10&rnnamespace=0".
      format(languageCode)

  }
}
