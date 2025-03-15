package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline

object NextTurn : BaseMove() {

    override fun name(game: Game): String {
        return if (game.hasDrawnAllInfectionCards()) {
            "Go to next turn"
        } else {
            "Skip infection cards"
        }
    }

    override fun isAllowed(game: Game) = !game.isDuringEpidemic() && game.turn.hasDrawnAllPlayerCards()

    override fun perform(game: Game): Timeline.Item {
        return game.doAction { nextTurn() }
    }
}
