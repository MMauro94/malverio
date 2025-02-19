package dev.mmauro.malverio

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal

val TERMINAL = Terminal()

fun <C> Collection<C>.select(text: String): C? where C : Card, C : Comparable<C> {
    val cards = sorted().withIndex().associateBy { (i, card) ->
        "${i + 1}: ${card.text()}"
    }

    val selection = TERMINAL.interactiveSelectList(
        cards.keys.toList(),
        title = text,
    )

    return cards[selection]?.value
}

fun printSection(name: String, block: Terminal.() -> Unit) {
    TERMINAL.println(TextColors.cyan("-- $name --"))
    TERMINAL.block()
    TERMINAL.println()
}

fun List<InfectionCard>.printAsBulletList() {
    for (card in this) {
        TERMINAL.println(card.text())
    }
}