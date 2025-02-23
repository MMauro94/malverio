package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.TERMINAL
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.moves.AbstractSimulateDrawMove.Probability

private val PROBABILITIES_COMPARATOR = compareByDescending<Map.Entry<InfectionCard, Probability>> {
    it.value.value
}.thenBy { it.key }

object SimulateInfectionDraw : AbstractSimulateDrawMove<InfectionCard>() {

    override fun isAllowed(timeline: Timeline) = !timeline.currentGame.isDuringEpidemic()

    override val cardTypeName = "infection"

    override fun getDeck(timeline: Timeline) = timeline.currentGame.infectionDeck

    override fun Terminal.printSimulationResults(results: SimulationResults<InfectionCard>) {
        val probabilities = results.probabilitiesBy { it }

        TERMINAL.println("Probabilities for each card:")
        probabilities.entries.sortedWith(PROBABILITIES_COMPARATOR).forEach { (card, probability) ->
            TERMINAL.println(" - ${card.text()}: ${probability.format()}")
        }
    }
}