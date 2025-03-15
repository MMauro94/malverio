package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object RemoveInfectionCard : BaseMove() {

    override fun name(game: Game) = "Remove infection card"

    override fun isAllowed(game: Game) = game.infectionDeck.drawn.isNotEmpty()

    override fun perform(game: Game): Timeline.Item? {
        val card = game.infectionDeck.drawn.select("Select card to remove") ?: return null
        return game.doAction(card.text()){
            game.removeCardFromDrawn(card)
        }
    }
}