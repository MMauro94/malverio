package dev.mmauro.malverio.simulation

import androidx.compose.ui.graphics.Color
import dev.mmauro.malverio.Textable


data class ProbabilityTree(
    val item: Textable,
    val probability: Probability,
    val subTrees: List<ProbabilityTree>,
) {

    fun leafSize(): Int {
        return if (subTrees.isEmpty()) return 1
        else subTrees.sumOf { it.leafSize() }
    }

    fun color(): Color? {
        val myColor = item.color()
        if (myColor != null) return myColor
        return subTrees.firstNotNullOfOrNull { it.color() }
    }
}
