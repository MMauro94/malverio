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
import kotlin.system.exitProcess

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
    private val players by option()
        .convert { Player(it) }
        .varargValues(min = 1)
        .required()
        .validate {
            if (it.size !in 2..4) fail("Players must be 2, 3 or 4")
            if (it.toSet().size != it.size) fail("Players must have different names")
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
            "portable antiviral labs" to playerDeck
                .count { it is PlayerCard.PortableAntiviralLabCard }.takeIf { it > 0 }
        )
        for ((name, value) in counts) {
            if (value != null) {
                TERMINAL.println(" - $name: $value")
            }
        }

        val game = setup()

        savegame.createParentDirectories()
        GameLoop(
            startTimeline = Timeline(game),
            savegame = savegame,
        ).run()
    }

    private fun setupForsakenCities(): Set<City> {
        val forsakenCities = infectionDeck
            .filterIsInstance<InfectionCard.CityCard>()
            .map { it.city }
            .toSet()
            .selectMultiple("Select fallen cities (0 population)")
            ?: throw PrintMessage("You must select the forsaken cities")
        return forsakenCities
    }

    private fun setupInfectionDeck(forsakenCities: Set<City>): Deck<InfectionCard> {
        val (hollowMenCards, otherCards) = infectionDeck.partition { it is InfectionCard.HollowMenGather }

        val deck = Deck(otherCards.toSet()).selectAndDraw(
            n = SETUP_INFECTION_CARDS,
            text = "infection cards setup (step 4)",
            ignore = { it.cityOrNull() in forsakenCities },
        )

        return Deck(
            partitions = deck.partitions,
            drawn = hollowMenCards + deck.drawn.filterNot { it.cityOrNull() in forsakenCities },
        )
    }

    private fun setupPlayerDeck(): Deck<PlayerCard> {
        val playerCardsToDraw = players.size * when (players.size) {
            2 -> 4
            3 -> 3
            4 -> 2
            else -> error("Invalid number of players: ${players.size}")
        }
        val playerDeckNoEpidemics = Deck(playerDeck.filterNot { it is PlayerCard.EpidemicCard }.toSet())
            .selectAndDraw(n = playerCardsToDraw, text = "player cards setup (step 6)")

        val epidemics = playerDeck.filterIsInstance<PlayerCard.EpidemicCard>()
        val playerCardsPerPile = playerDeckNoEpidemics.undrawn.size / epidemics.size
        val remainingPlayerCards = playerDeckNoEpidemics.undrawn.size % epidemics.size

        val playerDeck = Deck(
            partitions = epidemics.mapIndexed { i, epidemic ->
                Deck.Partition(
                    setOf(
                        Deck.Partition.Data(size = 1, cards = setOf(epidemic)),
                        Deck.Partition.Data(
                            size = playerCardsPerPile + if (i < remainingPlayerCards) 1 else 0,
                            cards = playerDeckNoEpidemics.undrawn,
                        ),
                    ),
                )
            },
            drawn = playerDeckNoEpidemics.drawn,
        )

        return playerDeck
    }

    private fun setupPlayers(): List<Player> {
        val first = players.select("Select the first player")
        return players.dropWhile { it != first } + players.takeWhile { it != first }
    }

    private fun setup(): Game {
        val forsakenCities = setupForsakenCities()

        val infectionDeck = setupInfectionDeck(forsakenCities)
        val playerDeck = setupPlayerDeck()

        return Game(
            forsakenCities = forsakenCities,
            players = setupPlayers(),
            playerDeck = playerDeck,
            infectionDeck = infectionDeck,
        )
    }
}