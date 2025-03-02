package dev.mmauro.malverio

import com.github.ajalt.mordant.input.interactiveMultiSelectList
import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles.italic
import com.github.ajalt.mordant.terminal.Terminal

val TERMINAL = Terminal()

private fun <C> Collection<C>.prepareMapForSelection(): Map<String, C> where C : Textable, C : Comparable<C> {
    val counts = groupingBy { it.text() }.eachCount()
    return sorted().associateBy {
        val txt = it.text()
        val count = counts[txt]
        if (count == 1) txt
        else "$txt (x$count)"
    }
}

fun <C> Collection<C>.select(text: String): C? where C : Textable, C : Comparable<C> {
    val items = prepareMapForSelection()

    val selection = TERMINAL.interactiveSelectList(
        items.keys.toList(),
        title = text,
    )

    return items[selection]
}

fun <C> Collection<C>.selectMultiple(text: String): Set<C>? where C : Textable, C : Comparable<C> {
    val items = prepareMapForSelection()

    val selection = TERMINAL.interactiveMultiSelectList(
        items.keys.toList(),
        title = text,
    )

    return selection?.map { items.getValue(it) }?.toSet()
}

fun <C> Deck<C>.selectAndDraw(
    n: Int,
    text: String,
    ignore: (C) -> Boolean = { false },
): Deck<C> where C : Card, C : Comparable<C> {
    var deck = this
    var drawn = 0
    while (drawn < n && deck.undrawn.isNotEmpty()) {
        val card = (deck.partitions.first().cards).select("Select card ${drawn + 1}/$n for $text") ?: continue
        deck = deck.drawCardFromTop(card)
        if (!ignore(card)) {
            drawn++
        }
    }
    return deck
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