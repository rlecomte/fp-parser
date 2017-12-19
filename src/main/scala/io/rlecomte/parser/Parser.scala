package io.rlecomte.parser

import cats.data.StateT
import cats.implicits._

object Parser {

  def parser[A](f: String => Result[(String, A)]): Parser[A] = StateT[Either[String, ?], String, A](f)

  def pure[A](v: A): Parser[A] = StateT.pure[Either[String, ?], String, A](v)
}
