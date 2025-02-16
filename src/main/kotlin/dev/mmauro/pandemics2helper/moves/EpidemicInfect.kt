package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.select

object EpidemicInfect : Move {
    override val name = "Epidemic: Infect"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic

    override fun perform(game: Game): Game {
        val card = game.partitionedDeck.last().select("Select drawn bottom card") ?: return game
        return game.infect(card)
    }
}