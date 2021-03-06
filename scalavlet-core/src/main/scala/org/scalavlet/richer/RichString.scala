package org.scalavlet.richer

import rl.UrlCodingUtils
import java.util.regex.Pattern
import java.nio.charset.Charset

/**
 * RicherString
 *
 * Derived from org.scalatra.util.RicherString
 */
class RichString(orig: String) {
  def isBlank = orig == null || orig.trim.isEmpty

  def blankOption = if (isBlank) None else Some(orig)
  def nonBlank = !isBlank

  def urlEncode = UrlCodingUtils.urlEncode(orig)
  def formEncode = UrlCodingUtils.urlEncode(orig, spaceIsPlus = true)
  def urlDecode = UrlCodingUtils.urlDecode(orig)
  def formDecode = UrlCodingUtils.urlDecode(orig, plusIsSpace = true)

  def urlEncode(charset: Charset) = UrlCodingUtils.urlEncode(orig, charset)
  def formEncode(charset: Charset) = UrlCodingUtils.urlEncode(orig, charset, spaceIsPlus = true)
  def urlDecode(charset: Charset) = UrlCodingUtils.urlDecode(orig, charset)
  def formDecode(charset: Charset) = UrlCodingUtils.urlDecode(orig, charset, plusIsSpace = true)

  def /(path: String) = (orig.endsWith("/"), path.startsWith("/")) match {
    case (true, false) | (false, true) ⇒ orig + path
    case (false, false)                ⇒ orig + "/" + path
    case (true, true)                  ⇒ orig + path substring 1
  }

  def regexEscape = Pattern.quote(orig)

  def toCheckboxBool = orig.toUpperCase match {
    case "ON" | "TRUE" | "OK" | "1" | "CHECKED" | "YES" | "ENABLE" | "ENABLED" => true
    case _ => false
  }
}
