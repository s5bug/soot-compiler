package tf.bug.soot.ast

import tf.bug.soot.ast.arti.{SArtifact, SPackageKey}
import tf.bug.soot.ast.container.SContainer
import tf.bug.soot.ast.imprt.SImport
import tf.bug.soot.ast.key.SPackage

case class SProgram(
                    artifact: SArtifact,
                    topLevelImports: Seq[SImport],
                    spackage: SPackageKey,
                    containers: Seq[SContainer]
                  ) extends SootAST {

}
