package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object DrawInfectionCard : BaseMove() {

    override val name = "Draw infection card"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic

    override fun perform(game: Game): Timeline.Item? {
        val card = game.infectionDeck.partitions.first().select("Select drawn card") ?: return null
        return Timeline.Item(game.drawInfectionCard(card), "$name ${card.text()}")
    }
}