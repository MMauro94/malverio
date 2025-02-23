package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.Timeline

object SimulateInfectionDraw : AbstractSimulateDrawMove<InfectionCard>() {

    override fun isAllowed(timeline: Timeline) = !timeline.currentGame.isDuringEpidemic()

    override val cardTypeName = "infection"

    override fun getDeck(timeline: Timeline) = timeline.currentGame.infectionDeck

    override fun getNumberOfCardsToDraw(timeline: Timeline): Int? {
        val drawnEpidemics = timeline.currentGame.playerDeck.drawn.count { it is PlayerCard.EpidemicCard }
        return when (drawnEpidemics) {
            0, 1, 2 -> 2
            3, 4 -> 3
            5, 6 -> 4
            else -> 5
        }
    }

    override fun Terminal.printSimulationResults(results: SimulationResults<InfectionCard>) {
        println("Probability of infection cards:")
        results.probabilityTree(
            { Group(it.city, isRelevant = true) },
            { Group(it, isRelevant = it.mutations.isNotEmpty()) },
        ).print(terminal = this)
    }
}