package dev.mmauro.pandemics2helper

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.pandemics2helper.moves.DrawInfectionCard
import dev.mmauro.pandemics2helper.moves.EpidemicInfect
import dev.mmauro.pandemics2helper.moves.EpidemicIntensify
import dev.mmauro.pandemics2helper.moves.Forecast
import dev.mmauro.pandemics2helper.moves.PrintDiscards
import dev.mmauro.pandemics2helper.moves.PrintPartitions
import dev.mmauro.pandemics2helper.moves.RemoveInfectionCard
import dev.mmauro.pandemics2helper.moves.SimulateDraw
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

private val MOVES = listOf(
    DrawInfectionCard,
    EpidemicInfect,
    EpidemicIntensify,
    RemoveInfectionCard,
    Forecast,
    SimulateDraw,
    PrintPartitions,
    PrintDiscards,
)

fun main() {
    var game = Game(deck = INFECTION_DECK)
    TERMINAL.println("Numbers of cards in the deck: ${game.deck.size}")

    while (true) {
        val moves = MOVES.filter { it.isAllowed(game) }
        val selection = TERMINAL.interactiveSelectList(
            moves.map { it.name },
            title = "Select move",
        ) ?: exit()

        game = moves.single { it.name == selection }.perform(game)
    }
}

fun Collection<InfectionCard>.select(text: String): InfectionCard? {
    val cards = sortedBy { it.city.name }.withIndex().associateBy { (i, card) ->
        "${i + 1}: $card"
    }

    val selection = TERMINAL.interactiveSelectList(
        cards.keys.toList(),
        title = text,
    )

    return cards[selection]?.value
}

private fun exit(): Nothing {
    TERMINAL.println("Goodbye!")
    exitProcess(0)
}

fun printSection(name: String, block: Terminal.() -> Unit) {
    TERMINAL.println(cyan("-- $name --"))
    TERMINAL.block()
    TERMINAL.println()
}