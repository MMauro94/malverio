package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.select

object EpidemicInfect : BaseMove() {

    override val name = "Epidemic: Infect"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic

    override fun perform(game: Game): Timeline.Item? {
        val card = game.partitionedDeck.last().select("Select drawn bottom card") ?: return null
        return Timeline.Item(game.infect(card), "$name with ${card.text()}")
    }
}