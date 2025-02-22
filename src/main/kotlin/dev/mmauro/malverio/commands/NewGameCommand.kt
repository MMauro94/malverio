package dev.mmauro.malverio.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.path
import dev.mmauro.malverio.*
import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.PlayerCard.EventCard
import dev.mmauro.malverio.PlayerCard.ProduceSuppliesCard
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText

private typealias InfectionDeck = Set<InfectionCard>
private typealias PlayerDeck = Set<PlayerCard>

private const val SETUP_INFECTION_CARDS = 9

class NewGameCommand : CliktCommand(name = "new") {

    private val infectionDeck by option().path(mustExist = true)
        .convert { JSON.decodeFromString<InfectionDeck>(it.readText()) }
        .required()
    private val playerDeck by option().path(mustExist = true)
        .convert { JSON.decodeFromString<PlayerDeck>(it.readText()) }
        .required()
        .check("must have epidemic cards in player deck") {
            it.filterIsInstance<PlayerCard.EpidemicCard>().isNotEmpty()
        }

    private val savegame by option().path(canBeDir = false, canBeSymlink = false).required()
    private val players by option().varargValues(min = 1).required().check("Players must be 2, 3 or 4") {
        it.size in 2..4
    }

    override fun run() {
        if (players.size != players.toSet().size) {
            throw BadParameterValue("Cannot have players with the same name")
        }
        TERMINAL.println("Player deck (${playerDeck.size} total cards):")
        val counts = mapOf(
            "city cards" to playerDeck.filterIsInstance<CityCard>().countAndGroup { it.city.color.text() },
            "epidemic cards" to playerDeck.count { it is PlayerCard.EpidemicCard }.toString(),
            "rationed event cards" to playerDeck.filterIsInstance<EventCard.RationedEventCard>().map { it.event }
                .countAndList(),
            "unrationed event cards" to playerDeck.filterIsInstance<EventCard.UnrationedEventCard>().map { it.event }
                .countAndList(),
            "produce supplies cards" to playerDeck.filterIsInstance<ProduceSuppliesCard>()
                .countAndGroup { it.productions() },
        )
        for ((name, value) in counts) {
            TERMINAL.println(" - $name: $value")
        }

        val game = setup()

        savegame.createParentDirectories()
        GameLoop(
            startTimeline = Timeline(game),
            savegame = savegame,
        ).run()
    }

    private fun setup(): Game {
        val infectionDeck = Deck(infectionDeck).selectAndDraw(
            n = SETUP_INFECTION_CARDS,
            text = "infection cards setup (step 4)"
        )

        val playerCardsToDraw = players.size * when (players.size) {
            2 -> 4
            3 -> 3
            4 -> 2
            else -> error("Invalid number of players: ${players.size}")
        }
        val drawnPlayerCards = Deck(playerDeck.filterNot { it is PlayerCard.EpidemicCard }.toSet())
            .selectAndDraw(n = playerCardsToDraw, text = "player cards setup (step 6)")
            .drawn
        val playerDeck = Deck(playerDeck)
        for (card in drawnPlayerCards) {
            playerDeck.drawCardFromTop(card)
        }

        return Game(players, playerDeck, infectionDeck)
    }
}