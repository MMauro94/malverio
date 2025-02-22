package dev.mmauro.malverio.moves

import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.Timeline

object SimulatePlayerDraw : AbstractSimulateDrawMove<PlayerCard>() {

    override fun isAllowed(timeline: Timeline) = DrawPlayerCard.isAllowed(timeline)

    override val cardTypeName = "player"

    override fun getDeck(timeline: Timeline) = timeline.currentGame.playerDeck
}