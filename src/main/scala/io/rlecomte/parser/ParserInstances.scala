package io.rlecomte.parser

import cats.data.StateT
import cats.implicits._
import Parser._

private[parser] trait ParserInstances {

  val word: Parser[String] = parser { value =>

    val points = Set(',', ';', '.', '?')

    val (word, rest) = value
      .dropWhile(c => points.contains(c) || c.isWhitespace)
      .span(_.isLetter)

    if (word.isEmpty) Left(s"Can't parse word : $rest")
    else Right(rest, word)
  }

  val digit: Parser[Int] = parser { value =>
    if (value.nonEmpty && value.head.isDigit) Right((value.tail, value.head.asDigit))
    else Left("Can't parse digit.")
  }

  val skip: Parser[Unit] = skip(0)

  def skip(n: Int): Parser[Unit] = parser { value =>
    Right((value.drop(n), ()))
  }

  def char(c: Char): Parser[Unit] = parser { value =>
    if (value.nonEmpty && value.head == c) Right(value.tail, ())
    else Left(s"Can't find char $c")
  }

  def alwaysRepeat[A](p: Parser[A]): Parser[List[A]] = {
    for {
      currentState <- StateT.get[Result, String]
      all <- p.run(currentState) match {
        case Left(_) => pure[List[A]](Nil)
        case Right((newState, result)) =>
          for {
            _ <- StateT.set[Result, String](newState)
            list <- alwaysRepeat(p)
          } yield result :: list
      }
    } yield all
  }

  def repeat[A](p: Parser[A])(n: Int): Parser[List[A]] = {
    if (n == 0) pure[List[A]](Nil)
    else {
      for {
        r <- p
        list <- repeat(p)(n - 1)
      } yield r :: list
    }
  }

  def orElse[A](parserA: Parser[A], parserB: Parser[A]): Parser[A] = parser { value =>
    parserA.run(value) match {
      case Left(_) => parserB.run(value)
      case r@Right(_) => r
    }
  }
}
