package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.select

object RemoveInfectionCard : BaseMove() {

    override val name = "Remove infection card"

    override fun isAllowed(game: Game) = true

    override fun perform(game: Game): Timeline.Item? {
        val card = game.discards.select("Select card to remove") ?: return null
        return Timeline.Item(game.removeCard(card), "$name ${card.text()}")
    }
}