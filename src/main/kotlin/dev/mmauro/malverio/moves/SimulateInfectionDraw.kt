package dev.mmauro.malverio.moves

import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Timeline

object SimulateInfectionDraw : AbstractSimulateDrawMove<InfectionCard>() {

    override fun isAllowed(timeline: Timeline) = DrawInfectionCard.isAllowed(timeline)

    override val cardTypeName = "infection"

    override fun getDeck(timeline: Timeline) = timeline.currentGame.infectionDeck
}