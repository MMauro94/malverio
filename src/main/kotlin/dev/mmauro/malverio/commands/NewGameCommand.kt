package dev.mmauro.malverio.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
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
    private val players by option().varargValues(min = 1).required()

    override fun run() {
        if (players.size != players.toSet().size) {
            throw BadParameterValue("Cannot have players with the same name")
        }
        TERMINAL.println("Player deck (${playerDeck.size} total cards):")
        val counts = mapOf(
            "city cards" to playerDeck.filterIsInstance<CityCard>().countAndGroup { it.city.color.text() },
            "epidemic cards" to playerDeck.count { it is PlayerCard.EpidemicCard }.toString(),
            "rationed event cards" to playerDeck.filterIsInstance<EventCard.RationedEventCard>().map { it.event }.countAndList(),
            "unrationed event cards" to playerDeck.filterIsInstance<EventCard.UnrationedEventCard>().map { it.event }.countAndList(),
            "produce supplies cards" to playerDeck.filterIsInstance<ProduceSuppliesCard>().countAndGroup { it.productions() },
        )
        for ((name, value) in counts) {
            TERMINAL.println(" - $name: $value")
        }

        val playerCardsWithoutEpidemics = playerDeck.filterNot { it is PlayerCard.EpidemicCard }
        var playerDeck = Deck(playerDeck)

        val setupPlayerCards = players.size * 2
        while (playerDeck.drawn.size < setupPlayerCards) {
            val card = (playerCardsWithoutEpidemics - playerDeck.drawn.toSet()).select(
                "Select card ${playerDeck.drawn.size + 1}/$setupPlayerCards setup player card"
            ) ?: throw PrintMessage("Game setup not complete")
            playerDeck = playerDeck.drawCardFromTop(card)
        }

        savegame.createParentDirectories()
        GameLoop(
            startTimeline = Timeline(Game(players, playerDeck, Deck(infectionDeck))),
            savegame = savegame,
        ).run()
    }
}