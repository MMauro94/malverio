package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.Turn
import dev.mmauro.malverio.select

object EpidemicInfect : BaseMove() {

    override fun name(game: Game) = "Epidemic: Infect"

    override fun isAllowed(game: Game) = game.turn.epidemicStage == Turn.EpidemicStage.INFECT

    override fun perform(game: Game): Timeline.Item? {
        // TODO: supprt early games
        // val card = game.infectionDeck.partitions.last().cards.select("Select drawn bottom card") ?: return null
        val card = game.notInGame.select("Select drawn bottom card") ?: return null
        return game.doAction("with ${card.text()}") { infect(card) }
    }
}