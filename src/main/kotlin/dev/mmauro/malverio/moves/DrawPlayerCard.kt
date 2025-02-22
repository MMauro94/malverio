package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object DrawPlayerCard : BaseMove() {

    override val name = "Draw player card"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic() && !game.turn.hasDrawnAllPlayerCards()

    override fun perform(game: Game): Timeline.Item? {
        val card = game.playerDeck.partitions.first().cards.select("Select drawn card") ?: return null
        return game.doAction(card.text()) { drawPlayerCard(card) }
    }
}