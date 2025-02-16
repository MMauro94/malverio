package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.select

object DrawInfectionCard : Move {

    override val name = "Draw infection card"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic

    override fun perform(game: Game): Game {
        val card = game.partitionedDeck.first().select("Select drawn card") ?: return game
        return game.drawCard(card)
    }
}