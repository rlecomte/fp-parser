package io.rlecomte

import cats.data.StateT
import cats.implicits._

package object parser {

  type Result[A] = Either[String, A]

  type Parser[A] = StateT[Result, String, A]

  implicit class ParserOps[A](parser: Parser[A]) {

    def parse(value: String): Result[A] = parser.run(value).map(_._2)

    def orElse(other: Parser[A]): Parser[A] = {
      instances.orElse(parser, other)
    }

    def alwaysRepeat: Parser[List[A]] = instances.alwaysRepeat(parser)

    def repeat(n: Int): Parser[List[A]] = instances.repeat(parser)(n)
  }

  object instances extends ParserInstances
}
