package org.scalavlet.route

import org.scalatest.{Matchers, FunSuite}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class SinatraPathPatternParserTest extends FunSuite with Matchers {
  test("should match exactly on a simple path") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/simple/path")

    pattern.toString should equal ("""^/simple/path$""")
    names should equal (Nil)
  }

  test("should match with a trailing slash") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/simple/path/")

    pattern.toString should equal ("""^/simple/path/$""")
    names should equal (Nil)
  }

  test("should replace a splat with a capturing group") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/splat/path/*")

    pattern.toString should equal ("""^/splat/path/(.*?)$""")
    names should equal (List("splat"))
  }

  test("should capture named groups") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/path/:group")

    pattern.toString should equal ("""^/path/([^/?#]+)$""")
    names should equal (List("group"))
  }

  test("should escape special regex characters") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/special/$.+()")

    pattern.toString should equal ("""^/special/\$\.\+\(\)$""")
    names should equal (Nil)
  }

  test("allow optional named groups") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/optional/?:stuff?")

    pattern.toString should equal ("""^/optional/?([^/?#]+)?$""")
    names should equal (List("stuff"))
  }

  test("should support seperate named params for filename and extension") {
    val PathPattern(pattern, names) = SinatraPathPatternParser.parse("/path-with/:file.:extension")

    pattern.toString should equal ("""^/path-with/([^/?#]+)\.([^/?#]+)$""")
    names should equal (List("file", "extension"))
  }
}
