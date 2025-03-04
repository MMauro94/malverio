package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object DrawInfectionCard : BaseMove() {

    override val name = "Draw infection card"

    override fun isAllowed(game: Game): Boolean {
        return !game.isDuringEpidemic() && game.turn.hasDrawnAllPlayerCards() && game.infectionDeck.undrawn.isNotEmpty() && !game.hasDrawnAllInfectionCards()
    }

    override fun perform(game: Game): Timeline.Item? {
        val card = game.infectionDeck.partitions.first().cards.select("Select drawn card") ?: return null
        return game.doAction(card.text()) { drawInfectionCard(card) }
    }
}