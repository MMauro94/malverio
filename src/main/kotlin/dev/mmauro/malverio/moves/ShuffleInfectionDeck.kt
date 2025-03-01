package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline

object ShuffleInfectionDeck : BaseMove() {

    override val name = "Shuffle infection deck"

    override fun isAllowed(game: Game) = game.infectionDeck.undrawn.isEmpty()

    override fun perform(game: Game): Timeline.Item {
        return game.doAction { shuffleInfectionDeck() }
    }
}