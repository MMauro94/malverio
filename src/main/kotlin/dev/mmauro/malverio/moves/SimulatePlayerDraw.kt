package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.CityColor
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.PlayerCard.EpidemicCard
import dev.mmauro.malverio.PlayerCard.EventCard
import dev.mmauro.malverio.PlayerCard.ProduceSuppliesCard
import dev.mmauro.malverio.Textable
import dev.mmauro.malverio.Timeline
import kotlin.math.min

object SimulatePlayerDraw : AbstractSimulateDrawMove<PlayerCard>() {

    override fun isAllowed(timeline: Timeline) = !timeline.currentGame.isDuringEpidemic()

    override val cardTypeName = "player"

    override fun getNumberOfCardsToDraw(timeline: Timeline) = min(2, timeline.currentGame.playerDeck.undrawn.size)

    override fun Game.doRandomDraw(): RandomDrawResult<PlayerCard> {
        val card = playerDeck.randomCardFromTop()
        val game = drawPlayerCard(card)
        val withEpidemicResolved = if (card is EpidemicCard) {
            game.resolveEpidemicRandomly()
        } else {
            game
        }
        return RandomDrawResult(drawnCards = setOf(card), game = withEpidemicResolved)
    }

    override fun Terminal.printSimulationResults(results: SimulationResults<PlayerCard>) {
        println("Probability of player cards:")
        results.probabilityTree(
            { Group(it.toType(), isRelevant = true) },
            {
                when (it) {
                    is CityCard -> Group(it.city, isRelevant = true)
                    is EventCard -> Group(it, isRelevant = true)
                    else -> null
                }
            },
            {
                when (it) {
                    is CityCard -> Group(it, isRelevant = it.unsearched > 0 || it.improvement != null)
                    else -> null
                }
            },
        ).print(terminal = this)
    }

    private sealed interface CardType : Textable {
        data object Epidemic : CardType {
            override fun text() = "EPIDEMIC ☢️"
        }

        data object Produce : CardType {
            override fun text() = "Produce"
        }

        data object Event : CardType {
            override fun text() = "Event"
        }

        data class City(val color: CityColor) : CardType {
            override fun text() = color.text()
        }

        data object PortableAntiviralLab : CardType {
            override fun text() = "Portable antiviral lab"
        }
    }

    private fun PlayerCard.toType() = when (this) {
        is CityCard -> CardType.City(city.color)
        is EpidemicCard -> CardType.Epidemic
        is EventCard.RationedEventCard -> CardType.Event
        is EventCard.UnrationedEventCard -> CardType.Event
        is ProduceSuppliesCard -> CardType.Produce
        is PlayerCard.PortableAntiviralLabCard -> CardType.PortableAntiviralLab
    }
}