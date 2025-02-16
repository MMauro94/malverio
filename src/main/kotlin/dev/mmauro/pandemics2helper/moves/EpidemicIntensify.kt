package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game

object EpidemicIntensify : Move {
    override val name = "Epidemic: Intensify"

    override fun isAllowed(game: Game) = game.isDuringEpidemic

    override fun perform(game: Game): Game {
        return game.intensify()
    }
}