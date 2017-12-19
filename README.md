# fp-parser
simple example of a functional string parser in Scala

## Example

```scala
import io.rlecomte.parser._
import io.rlecomte.parser.instances._
import cats.implicits._

case class Name(value: String) extends AnyVal

def hello(name: String): String = s"Hello $name!"

val extractName: Parser[Name] = for {
  _ <- word
  name <- word
} yield Name(name)

println(extractName.parse(hello("Romain")).right.get)
```