package dev.mmauro.malverio.simulation

import androidx.compose.ui.graphics.Color
import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.Textable


data class ProbabilityTree(
    val group: Group,
    val probability: Probability,
    val subTrees: List<ProbabilityTree>,
) {

    fun hasInterestingSubtrees(): Boolean {
        return subTrees.any { it.group.isRelevant || it.hasInterestingSubtrees() }
    }

    fun leafSize(): Int {
        return if (subTrees.isEmpty()) return 1
        else subTrees.sumOf { it.leafSize() }
    }

    fun explore(
        indentation: Int = 0,
        f: (indentation: Int, ProbabilityTree) -> Unit,
    ) {
        if (subTrees.size == 1) {
            subTrees.single().explore(indentation, f)
        } else {
            f(indentation, this)
            if (hasInterestingSubtrees()) {
                for (subTree in subTrees) {
                    subTree.explore(indentation + 1, f)
                }
            }
        }
    }

    fun color(): Color? {
        val myColor = group.item.color()
        if (myColor != null) return myColor
        return subTrees.firstNotNullOfOrNull { it.color() }
    }
}

data class Group(
    val item: Textable,
    val isRelevant: Boolean,
)

fun Iterable<ProbabilityTree>.explore(f: (indentation: Int, ProbabilityTree) -> Unit) {
    forEach { it.explore(indentation = 0, f) }
}

fun Iterable<ProbabilityTree>.print(terminal: Terminal) {
    explore { indentation, node ->
        terminal.println(buildString {
            append("  ".repeat(indentation))
            append(" - ")
            append(node.group.item.text())
            append(": ")
            append(node.probability.format())
            val leafSize = node.leafSize()
            if (leafSize > 1) {
                append(" (x$leafSize)")
            }
        })

    }
}
