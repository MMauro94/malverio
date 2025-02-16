package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game

abstract class PrintMove : Move {

    override fun isAllowed(game: Game) = true

    override fun perform(game: Game): Game {
        print(game)
        return game
    }

    abstract fun print(game: Game)
}