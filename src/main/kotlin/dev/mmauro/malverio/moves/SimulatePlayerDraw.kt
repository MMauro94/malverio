package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.CityColor
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.TERMINAL
import dev.mmauro.malverio.Timeline

object SimulatePlayerDraw : AbstractSimulateDrawMove<PlayerCard>() {

    private val PROBABILITIES_COMPARATOR = compareByDescending<Map.Entry<CardType, Probability>> {
        it.value.value
    }.thenBy { it.key.toString() }


    override fun isAllowed(timeline: Timeline) = !timeline.currentGame.isDuringEpidemic()

    override val cardTypeName = "player"

    override fun getDeck(timeline: Timeline) = timeline.currentGame.playerDeck

    override fun Terminal.printSimulationResults(results: SimulationResults<PlayerCard>) {
        val probabilityByType = results
            .probabilitiesBy { it.toType() }
            .entries
            .sortedWith(PROBABILITIES_COMPARATOR)

        TERMINAL.println("Probability of drawing:")
        for ((type, probability) in probabilityByType) {
            TERMINAL.println(" - $type: ${probability.format()}")
        }
    }

    private sealed interface CardType {
        data object Epidemic : CardType
        data object Produce : CardType
        data object Event : CardType
        data class City(val color: CityColor) : CardType {
            override fun toString() = color.text()
        }
    }

    private fun PlayerCard.toType() = when (this) {
        is PlayerCard.CityCard -> CardType.City(city.color)
        is PlayerCard.EpidemicCard -> CardType.Epidemic
        is PlayerCard.EventCard.RationedEventCard -> CardType.Event
        is PlayerCard.EventCard.UnrationedEventCard -> CardType.Event
        is PlayerCard.ProduceSuppliesCard -> CardType.Produce
    }
}