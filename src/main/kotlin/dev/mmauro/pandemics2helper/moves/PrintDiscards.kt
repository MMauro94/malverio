package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.printSection

object PrintDiscards : PrintMove() {
    override val name = "Print discards"

    override fun print(game: Game) {
        printSection("DISCARDS") {
            game.discards.sortedBy { it.city.name }.forEach { card ->
                println(" - $card")
            }
        }
    }
}