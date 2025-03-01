package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Timeline

object SimulateInfectionDraw : AbstractSimulateDrawMove<InfectionCard>() {

    override fun isAllowedMove(timeline: Timeline) = !timeline.currentGame.isDuringEpidemic()

    override val cardTypeName = "infection"

    override fun getDeck(timeline: Timeline) = timeline.currentGame.infectionDeck

    override fun getNumberOfCardsToDraw(timeline: Timeline): Int {
        return timeline.currentGame.infectionMarker.cards
    }

    override fun Terminal.printSimulationResults(results: SimulationResults<InfectionCard>) {
        println("Probability of infection cards:")
        results.probabilityTree(
            { Group(it.city, isRelevant = true) },
            { Group(it, isRelevant = it.mutations.isNotEmpty()) },
        ).print(terminal = this)
    }
}