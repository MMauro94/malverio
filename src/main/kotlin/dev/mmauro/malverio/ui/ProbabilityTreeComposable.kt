package dev.mmauro.malverio.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.mmauro.malverio.simulation.ProbabilityTree

@Composable
fun ProbabilityTreesComposable(probabilityTrees: List<ProbabilityTree>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ProbabilityTreesComposable(probabilityTrees )
    }
}

@Composable
private fun ColumnScope.ProbabilityTreesComposable(probabilityTrees: List<ProbabilityTree>, indentation: Int = 0) {
    for (node in probabilityTrees) {
        ProbabilityTreeComposable(node, indentation)
    }
}

@Composable
private fun ColumnScope.ProbabilityTreeComposable(
    node: ProbabilityTree,
    indentation: Int
) {
    if (node.subTrees.size == 1) {
        ProbabilityTreesComposable(node.subTrees, indentation)
        return
    }
    var expand by remember { mutableStateOf(false) }
    PercentageBar(
        Modifier.clickable {
            expand = !expand
        },
        node.probability.value.toFloat(),
        node.color() ?: MaterialTheme.colorScheme.surfaceVariant,
        buildString {
            append(node.group.item.plainText())
            append(": ")
            append(node.probability.format())
            val leafSize = node.leafSize()
            if (leafSize > 1) {
                append(" (x$leafSize)")
            }
        },
        textModifier = Modifier.padding(start = indentation * 4.dp)
    )
    if (expand) {
        ProbabilityTreesComposable(node.subTrees, indentation + 1)
    }
}

@Composable
private fun PercentageBar(
    modifier: Modifier,
    percentage: Float,
    color: Color,
    text: String,
    textModifier: Modifier = Modifier,
) {
    Surface(shape = MaterialTheme.shapes.medium) {
        Box(
            modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(color, Offset.Zero, Size(this.size.width * percentage, this.size.height))
                }
        ) {
            Text(text, modifier = textModifier.padding(8.dp), color = Color.White)
        }
    }
}
