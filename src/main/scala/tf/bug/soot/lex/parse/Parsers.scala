package tf.bug.soot.lex.parse

import fastparse.all._
import fastparse.core
import tf.bug.soot.ast.container.SContainerKey
import tf.bug.soot.ast.imprt._
import tf.bug.soot.ast.key.{SKeyword, SPackage}

object Parsers {

  lazy val end = P("\n" | ";").rep(1)
  lazy val space = P(CharIn(" ").rep(1))

  lazy val keyword: core.Parser[SKeyword, Char, String] = P(CharIn('a' to 'z') ~ (CharIn('a' to 'z') | "_").rep).!.map(SKeyword.apply)
  lazy val ckey: core.Parser[SContainerKey, Char, String] = P(CharIn('A' to 'Z') ~ CharIn('A' to 'Z', 'a' to 'z').rep).!.map(SContainerKey.apply)
  lazy val pkey: core.Parser[SPackage, Char, String] = P(keyword.rep(1, ".")).map {
    l: Seq[SKeyword] =>
      def pack(head: SKeyword, tail: Seq[SKeyword]): SPackage = {
        (head, tail) match {
          case (emptyH, Seq()) => SPackage(emptyH)
          case (nonEmptyH, nonEmptyT) => SPackage(nonEmptyH, Some(pack(nonEmptyT.head, nonEmptyT.tail)))
        }
      }

      pack(l.head, l.tail)
  }
  lazy val ikey: core.Parser[SImportPackage, Char, String] = P(pkey ~ (ckey.map(ck => Some(SImportContainer.apply(ck))) | ("{" ~ (keyword ~ "=>" ~ keyword).rep(1, ",") ~ "}").map(m => {
    m.toMap.map {
      case (k, v) => SImportMap(k, v)
    }
  }) | end.!.map(_ => None))).map {
    case (p: SPackage, t: Option[SImportTarget]) => SImportPackage(p, t)
  }

  lazy val prog = P(artifact ~ imprt.rep ~ pkg /* ~ container */)
  lazy val artifact = P("arti" ~ space ~ pkey ~ end)
  lazy val imprt = P("impt" ~ space ~ ikey ~ end)
  lazy val pkg = P("pack" ~ space ~ pkey ~ end)

}
