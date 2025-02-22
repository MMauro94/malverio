package dev.mmauro.malverio

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.rendering.TextStyles.italic
import com.github.ajalt.mordant.terminal.Terminal

val TERMINAL = Terminal()

fun <C> Collection<C>.select(text: String): C? where C : Card, C : Comparable<C> {
    val counts = groupingBy { it.text() }.eachCount()
    val cards = sorted().associateBy {
        val txt = it.text()
        txt + " (${counts[txt]})"
    }

    val selection = TERMINAL.interactiveSelectList(
        cards.keys.toList(),
        title = text,
    )

    return cards[selection]
}

fun printSection(name: String, block: Terminal.() -> Unit) {
    TERMINAL.println(TextColors.cyan("-- $name --"))
    TERMINAL.block()
    TERMINAL.println()
}

fun List<Card>.printAsBulletList() {
    if (isEmpty()) {
        TERMINAL.println(italic("Empty"))
    } else {
        for (card in this) {
            TERMINAL.println(" - " + card.text())
        }
    }
}

fun <T> List<T>.countAndGroup(selector: (T) -> String): String {
    val counts = groupingBy { selector(it) }.eachCount()
    return size.toString() + " (" + counts.entries.joinToString { (key, value) -> "${key}: $value" } + ")"
}

fun <T> List<T>.countAndList(): String {
    return size.toString() + if (isNotEmpty()) " (" + joinToString() + ")" else ""
}