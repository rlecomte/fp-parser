package io.rlecomte.parser

import org.scalatest.{FlatSpec, Matchers}

class ParserSpec extends FlatSpec with Matchers {

  "A parser" should "decode different phone numbers" in {
    import instances._
    import cats.implicits._

    case class PhoneNumber(value: List[Int])

    val allPhoneNumbers = List(
      "0612345670",
      "06-12-34-56-71",
      "06.12.34.56.72",
      "+33612345673"
    )

    def parserWithDelimiter(delimiter: Char) = for {
      firstDigits <- digit.repeat(2)
      nextDigits <-
      (for {
        _ <- char(delimiter)
        digits <- digit.repeat(2)
      } yield digits).repeat(4).map(_.flatten)
    } yield PhoneNumber(firstDigits ::: nextDigits)

    val pattern1 = digit.repeat(10).map(PhoneNumber)

    val pattern2 = parserWithDelimiter('-')

    val pattern3 = parserWithDelimiter('.')

    val pattern4 = (char('+') *> digit.repeat(2) *> digit.repeat(9)).map(digits => PhoneNumber(0 :: digits))

    val phoneNumberParser = pattern1.orElse(pattern2).orElse(pattern3).orElse(pattern4)

    val tested = allPhoneNumbers.traverse[Result, PhoneNumber](phoneNumberParser.parse)

    tested.right.get should contain allElementsOf Seq(
      PhoneNumber(List(0, 6, 1, 2, 3, 4, 5, 6, 7, 0)),
      PhoneNumber(List(0, 6, 1, 2, 3, 4, 5, 6, 7, 1)),
      PhoneNumber(List(0, 6, 1, 2, 3, 4, 5, 6, 7, 2)),
      PhoneNumber(List(0, 6, 1, 2, 3, 4, 5, 6, 7, 3))
    )
  }
}
