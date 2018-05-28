package tf.bug.soot.ast.imprt

import tf.bug.soot.ast.key.{SKeyword, SPackage}

case class SImportPackage(pack: SPackage, child: Option[SImportTarget]) extends SImportTarget {

}
