package dev.mmauro.pandemics2helper

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.muted
import dev.mmauro.pandemics2helper.events.DrawCardEvent
import dev.mmauro.pandemics2helper.events.Event
import dev.mmauro.pandemics2helper.events.IntensifyEvent
import kotlin.system.exitProcess

val TERMINAL = Terminal()

val INFECTION_DECK = setOf(
    InfectionCard(City.ATLANTA),
    InfectionCard(City.BUENO_AIRES),
    InfectionCard(City.BUENO_AIRES),
    InfectionCard(City.CAIRO),
    InfectionCard(City.CAIRO),
    InfectionCard(City.CAIRO),
    InfectionCard(City.FRANKFURT),
    InfectionCard(City.FRANKFURT),
    InfectionCard(City.ISTANBUL),
    InfectionCard(City.ISTANBUL),
    InfectionCard(City.ISTANBUL),
    InfectionCard(City.JACKSONVILLE),
    InfectionCard(City.JACKSONVILLE),
    InfectionCard(City.JACKSONVILLE),
    InfectionCard(City.LAGOS),
    InfectionCard(City.LAGOS),
    InfectionCard(City.LAGOS),
    InfectionCard(City.LIMA),
    InfectionCard(City.LONDON),
    InfectionCard(City.LONDON),
    InfectionCard(City.LONDON),
    InfectionCard(City.LOS_ANGELES),
    InfectionCard(City.NEW_YORK),
    InfectionCard(City.NEW_YORK),
    InfectionCard(City.NEW_YORK),
    InfectionCard(City.PARIS),
    InfectionCard(City.PARIS),
    InfectionCard(City.SANTIAGO),
    InfectionCard(City.SAO_PAULO),
    InfectionCard(City.SAO_PAULO),
    InfectionCard(City.SAO_PAULO),
    InfectionCard(City.TRIPOLI),
    InfectionCard(City.TRIPOLI),
    InfectionCard(City.TRIPOLI),
    InfectionCard(City.WASHINGTON),
    InfectionCard(City.WASHINGTON),
    InfectionCard(City.WASHINGTON, setOf(InfectionCard.Mutation.WELL_STOCKED)),
)

private val EVENTS = mapOf<String, (Game) -> List<Event>>(
    "Draw card" to ::selectDraw,
    "Simulate draw" to ::simulateDraw,
    "Epidemic" to ::epidemic,
    "Print discards" to ::printDiscards,
    "Exit" to ::exit,
)

fun main() {
    var game = Game(deck = INFECTION_DECK, timeline = emptyList())
    TERMINAL.println("Numbers of cards in the deck: ${game.deck.size}")

    while (true) {
        val selection = TERMINAL.interactiveSelectList(
            EVENTS.keys.toList(),
            title = "Select event",
        )
        if (selection != null) {
            val events = EVENTS.getValue(selection)(game)
            for (event in events) {
                TERMINAL.muted(event.toString())
            }
            game = game.addEvents(events)
        }
    }
}

private fun printDiscards(game: Game): List<Event> {
    TERMINAL.println(cyan("DISCARDS ARE:"))
    game.discards().sortedBy { it.city.name }.forEach { card ->
        TERMINAL.println(" - $card")
    }
    TERMINAL.println()

    return emptyList()
}

private fun selectDraw(game: Game): List<Event> {
    val card = game.deckPartition().first().select("Select drawn card") ?: return emptyList()
    return listOf(DrawCardEvent(card))
}

private fun epidemic(game: Game): List<Event> {
    val card = game.deckPartition().last().select("Select drawn bottom card") ?: return emptyList()
    return listOf(DrawCardEvent(card), IntensifyEvent)
}

private fun Set<InfectionCard>.select(text: String): InfectionCard? {
    val cards = sortedBy { it.city.name }.withIndex().associateBy { (i, card) ->
        "${i + 1}: $card"
    }

    val selection = TERMINAL.interactiveSelectList(
        cards.keys.toList(),
        title = text,
    )

    return cards[selection]?.value
}

private fun simulateDraw(game: Game): List<Event> {
    val selection = TERMINAL.interactiveSelectList(
        listOf("1", "2", "3", "4", "5"),
        title = "Number of cards to draw",
    )
    val cards = selection?.toInt()
    if (cards != null) {
        TERMINAL.println(cyan("RUNNING SIMULATION"))
        game.deckPartition().printDrawProbabilities(cards)
        TERMINAL.println()
    }

    return emptyList()
}

private fun List<Set<InfectionCard>>.printDrawProbabilities(n: Int) {
    if (isEmpty()) {
        TERMINAL.println("RIP, mazzo finito")
    } else if (first().size <= n) {
        TERMINAL.println("For sure, you'll draw these cards:")
        for (card in first()) {
            TERMINAL.println(" - $card")
        }
        val remaining = n - first().size
        if (remaining > 0) {
            drop(1).printDrawProbabilities(remaining)
        }
    } else {
        TERMINAL.println("You'll draw $n out of these ${first().size} cards:")
        for (card in first()) {
            TERMINAL.println(" - $card")
        }
    }
}

private fun exit(game: Game): Nothing {
    TERMINAL.println("Goodbye!")
    exitProcess(0)
}