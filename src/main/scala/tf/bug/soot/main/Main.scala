package tf.bug.soot.main

import java.io.File

import fastparse.core.Parsed
import tf.bug.soot.BuildInfo
import tf.bug.soot.ast.SProgram
import tf.bug.soot.lex.parse.Parsers

import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Options]("sootc") {
      head("Soot Compiler ...", BuildInfo.version)
      head("Scala ...........", BuildInfo.scalaVersion)
      head("SBT .............", BuildInfo.sbtVersion)
      head("Scala Native ....", BuildInfo.nativeVersion)
      head("Built ...........", BuildInfo.buildTime)
      arg[File]("<file>...").unbounded().validate(f => if(f.exists()) success else failure(s"File ${f.getPath} does not exist!")).action( (x, c) => c.copy(files = c.files :+ x) ).text("Files to compile")
      help("help").text("Prints this help text")
    }
    parser.parse(args, Options()) match {
      case Some(o) =>
        o.files.foreach(f => {
          val l = Source.fromFile(f, "UTF-8").getLines().mkString("\n")
          Parsers.prog.parse(l) match {
            case Parsed.Success(p, _) => println(p)
            case failure => println(failure.get)
          }
        })
      case None =>
    }
  }

}
