package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object RemoveInfectionCard : BaseMove() {

    override val name = "Remove infection card"

    override fun isAllowed(game: Game) = true

    override fun perform(game: Game): Timeline.Item? {
        val card = game.discards.select("Select card to remove") ?: return null
        return Timeline.Item(game.removeCard(card), "$name ${card.text()}")
    }
}