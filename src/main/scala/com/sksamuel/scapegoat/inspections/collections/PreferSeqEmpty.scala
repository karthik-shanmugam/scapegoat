package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat._

/** @author Stephen Samuel */
class PreferSeqEmpty extends Inspection("Prefer Seq.empty", Levels.Info) {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = Some apply new context.Traverser {

      import context.global._

      private val ApplyTerm = TermName("apply")
      private val SeqTerm = TermName("Seq")

      override def inspect(tree: Tree): Unit = {
        tree match {
          case a@Apply(TypeApply(Select(Select(_, SeqTerm), ApplyTerm), _), List())
            if (!a.tpe.toString.startsWith("scala.collection.mutable.")) =>
              warn(tree)
          case _ => continue(tree)
        }
      }

      private def warn(tree: Tree): Unit = {
        context.warn(tree.pos, self,
          "Seq[T]() allocates an intermediate object. Consider Seq.empty wich returns a singleton instance without creating a new object. " +
            tree.toString().take(500))
      }
    }
  }
}