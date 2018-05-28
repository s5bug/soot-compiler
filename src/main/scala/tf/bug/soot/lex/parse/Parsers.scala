package tf.bug.soot.lex.parse

import fastparse.all._
import fastparse.{all, core}
import tf.bug.soot.ast.SProgram
import tf.bug.soot.ast.arti.{SArtifact, SPackageKey}
import tf.bug.soot.ast.container.SContainerKey
import tf.bug.soot.ast.imprt._
import tf.bug.soot.ast.key.{SKeyword, SPackage}

object Parsers {

  lazy val newline: all.Parser[Unit] = P("\n")
  lazy val end: all.Parser[Unit] = P(newline | ";")
  lazy val space: all.Parser[Unit] = P(CharIn(" ").rep(1))
  lazy val pad: core.Parser[Unit, Char, String] = space.?

  lazy val keyword: core.Parser[SKeyword, Char, String] = P((CharIn('a' to 'z') | "_").rep(1)).!.map(SKeyword.apply)
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
  lazy val ikey: core.Parser[SImportTarget, Char, String] = P(
    pkey ~ (
        keyword.map(kw => SImportKeyword(kw))
      |
        ckey.map(ck => SImportContainer(ck))
      |
        ("(" ~ pad ~ (keyword ~ pad ~ "=>" ~ pad ~ keyword).rep(1, pad ~ "," ~ pad) ~ pad ~ ")").map(m => {
          m.toMap.map {
            case (k, v) => SImportMap(k, v)
          }
        }).map(l => SImportGroup(l.toSeq))
      )
    ~ &(end)
  ).map {
    case (p: SPackage, t: Option[SImportTarget]) => SImportPackage(p, t)
  }

  lazy val prog = P(artifact ~ end.rep ~ imprt.rep ~ end.rep ~ pkg /* ~ container */ ~ End).map { // TODO
    case (art, imprts, pack) =>
      SProgram(art, imprts, pack, Seq())
  }
  lazy val artifact = P("arti" ~ space ~ pkey).map(SArtifact.apply)
  lazy val imprt = P("impt" ~ space ~ ikey ~ end.rep).map(SImport.apply)
  lazy val pkg = P("pack" ~ space ~ pkey).map(SPackageKey.apply)

}
