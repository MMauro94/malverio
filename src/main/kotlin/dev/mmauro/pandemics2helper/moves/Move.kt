package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game

interface Move {

    val name: String

    fun isAllowed(game: Game): Boolean

    fun perform(game: Game): Game
}