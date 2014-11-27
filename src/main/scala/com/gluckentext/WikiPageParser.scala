package com.gluckentext

import scala.xml.{Utility, Elem}

object WikiPageParser {

  def clean(text: String): String = {
    implicit class WikiString(text: String) {
      def remove(regex: String): String = {
        val res = text.replaceAll(regex, "")
        if (res != text) res.remove(regex) else res
      }

      def removeBlocks(startMarker: String, endMarker: String) = {
        //        object headAndTail {
        //          def unapply(s: String) = s.fir.headOption.map { (_, s.tail) }
        //        }
        val startMarkerSize = startMarker.size
        val endMarkerSize = endMarker.size
        val startMarkerHead = startMarker.head
        val endMarkerHead = endMarker.head

        def removeBlocksAcc(s: String, acc: String, nestingLevel: Int): String =
        //          s match {
        //            case headAndTail(head, tail) =>
        //              if (head.)
        //
        //          }
          if (s.isEmpty) acc
          else {
            val (startMarkerCandidate, tail1) = s.splitAt(startMarkerSize)
            if (startMarkerCandidate == startMarker) removeBlocksAcc(tail1, acc, nestingLevel + 1)
            else {
              val (endMarkerCandidate, tail2) = s.splitAt(endMarkerSize)
              if (endMarkerCandidate == endMarker) removeBlocksAcc(tail2, acc, math.max(nestingLevel - 1, 0))
              else {
                val (safePart, candidate) = s.tail.span(c => c != startMarkerHead && c != endMarkerHead)
                if (nestingLevel == 0) removeBlocksAcc(candidate, acc + s.head + safePart, nestingLevel)
                else removeBlocksAcc(candidate, acc, nestingLevel)
              }
            }
          }
        removeBlocksAcc(text, "", 0)
      }

      def removeReferences = text.replaceAll("<ref[^>]*>(.+?)</ref>", "")

      def strip(regex: String) = text.replaceAll(regex, "$1")

      def insertSpacesAfterPunctuation = text.replaceAll("([.,])([A-Z])", "$1 $2")

      def removeSpacesBeforePunctuation = text.replaceAll("(\\w) +([,.])", "$1$2")
    }

    def multiline = "(?s)"
    def brackets(s: String) = "\\[" + s + "\\]"
    def anyNumOfBraces(s: String) = "\\{+" + s + "\\}+"

    val templates = multiline + anyNumOfBraces(".+?")
    val references = multiline + "<ref>.*?</ref>"
    val internalLinks = multiline + brackets(brackets("(?:[^]]*?\\|)?(.+?)"))
    val externalLinks = multiline + brackets(".+?")
    val files = multiline + brackets(brackets("File:.*?"))
    var italicMarkup = "''(.+?)''"
    var boldMarkup = "'''(.+?)'''"

    val italicTags = "<i>$1</i>"
    val boldTags = "<b>$1</b>"

    text.
      replaceAll(boldMarkup, boldTags).
      replaceAll(italicMarkup, italicTags).
      removeBlocks("{", "}").
      removeReferences.
      strip(internalLinks).
      removeBlocks("[", "]").
      removeBlocks("(", ")").
      insertSpacesAfterPunctuation.
      removeSpacesBeforePunctuation
  }

  def chapterize(text: String) = {
    val parts = text.split("==+").map(_.trim)
    val pairs = parts.grouped(2)
    pairs.map(p => new Chapter(p.head, p.last))
  }

  def parseWikiPage(root: Elem) = {
    val title = (root \\ "title").text
    var text = (root \\ "text").text
    text = clean(text)
    val chapters = chapterize(title + "== " + text)

    chapters.filter(c => !c.hasLists && !c.hasTables && !c.hasLeftovers && c.isLongEnough)
  }
}

case class Chapter(val heading: String, val body: String) {
  def hasLists = body.contains('#') || body.contains('*')

  def hasTables = body.contains("|")

  def hasLeftovers = body.contains("]") || body.contains("}")

  def isLongEnough = body.size >= 100
}
