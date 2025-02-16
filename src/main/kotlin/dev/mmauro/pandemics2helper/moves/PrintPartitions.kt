package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.printSection

object PrintPartitions : PrintMove() {
    override val name = "Print partitions"

    override fun print(game: Game) {
        printSection("PARTITIONS") {
            for (partition in game.partitionedDeck) {
                println("Next ${partition.size} cards are:")
                for (card in partition) {
                    println(" - $card")
                }
            }
        }
    }
}