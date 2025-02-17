package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.select

object DrawInfectionCard : BaseMove() {

    override val name = "Draw infection card"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic

    override fun perform(game: Game): Timeline.Item? {
        val card = game.partitionedDeck.first().select("Select drawn card") ?: return null
        return Timeline.Item(game.drawCard(card), "$name ${card.text()}")
    }
}