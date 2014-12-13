package com.gluckentext

import com.gluckentext.wikipediaaccess.{Chapter, WikiPageParser}
import WikiPageParser._

import org.scalatest.{Matchers, FlatSpec}

class WikiPageParserTests extends FlatSpec with Matchers {
  "clean" should "return the same text if it's all alphanumeric" in {
    assert(clean("abcdef 123456") === "abcdef 123456")
  }

  it should "remove templates" in {
    assert(clean("a{b}c{d}") === "ac")
  }

  it should "remove templates with double braces" in {
    assert(clean("a{{b}}c{{d}}") === "ac")
  }

  it should "remove nested templates" in {
    assert(clean("a { b { c { d } c } b } a { b { c } b { c } b } a") === "a  a  a")
  }

  it should "remove multiline templates" in {
    assert(clean("a{b \n c}d") === "ad")
  }

  it should "remove references" in {
    assert(clean("a<ref>b</ref>c") === "ac")
  }

  it should "remove reference tags if characters are misleading" in {
    assert(clean("a<<<re<<<ref>bcd</<</ref>>e") === "a<<<re<<>e")
  }

  it should "remove reference tags with attributes" in {
    assert(clean("a <ref name='bla'> something </ref> b") === "a  b")
  }

  it should "remove external links" in {
    assert(clean("a[b]c[d e]") === "ac")
  }

  it should "strip internal links with custom text" in {
    assert(clean("a[[b|c]]") === "ac")
  }

  it should "strip internal links without custom text" in {
    assert(clean("a[[b]]") === "ab")
  }

  it should "strip multiple internal links" in {
    assert(clean("a [[b|c]] d [[e]] f [[g|h]]") === "a c d e f h")
  }

  it should "remove parantheses" in {
    assert(clean("a(b)c") === "ac")
  }

  it should "replace bold markup with <b>" in {
    assert(clean("a'''b'''c") === "a<b>b</b>c")
  }

  it should "replace italic markup with <i>" in {
    assert(clean("a''b''c") === "a<i>b</i>c")
  }

  it should "insert a space between a dot or comma and a capital letter" in {
    assert(clean("a.B.C.d") === "a. B. C.d")
  }

  it should "remove a space before a dot or comma" in {
    assert(clean("a , b") === "a, b")
  }

  "chapterize" should "split text into chapters" in {
    val text = "title 1== body 1 ===title 2=== body 2 ==== title 3 ==== body 3"
    val chapters = chapterize(text).toList
    assert(chapters.size === 3)
    chapters match {
      case Chapter(h1, b1) :: Chapter(h2, b2) :: Chapter(h3, b3) :: Nil =>
        assert(h1 === "title 1")
        assert(b1 === "body 1")
        assert(h2 === "title 2")
        assert(b2 === "body 2")
        assert(h3 === "title 3")
        assert(b3 === "body 3")
    }
  }
}
