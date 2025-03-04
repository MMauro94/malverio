package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.Turn

object NextTurn : BaseMove() {

    override val name = "Go to next turn"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic() && game.turn.hasDrawnAllPlayerCards() && game.hasDrawnAllInfectionCards()

    override fun perform(game: Game): Timeline.Item {
        return game.doAction { nextTurn() }
    }
}