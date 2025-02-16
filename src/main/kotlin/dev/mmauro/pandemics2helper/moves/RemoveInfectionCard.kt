package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.select

object RemoveInfectionCard : Move {
    override val name = "Remove infection card"

    override fun isAllowed(game: Game) = true

    override fun perform(game: Game): Game {
        val card = game.discards.select("Select card to remove") ?: return game
        return game.removeCard(card)
    }
}