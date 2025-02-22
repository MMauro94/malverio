package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.Turn
import dev.mmauro.malverio.select

object EpidemicInfect : BaseMove() {

    override val name = "Epidemic: Infect"

    override fun isAllowed(game: Game) = game.turn.epidemicStage == Turn.EpidemicStage.INFECT

    override fun perform(game: Game): Timeline.Item? {
        val card = game.infectionDeck.partitions.last().select("Select drawn bottom card") ?: return null
        return game.doAction("with ${card.text()}") { infect(card) }
    }
}