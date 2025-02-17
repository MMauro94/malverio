package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.printAsBulletList
import dev.mmauro.pandemics2helper.printSection

object PrintPartitions : PrintMove() {

    override val name = "Print partitions"

    override fun print(timeline: Timeline) {
        printSection("PARTITIONS") {
            for (partition in timeline.currentGame.partitionedDeck) {
                println("Next ${partition.size} cards are:")
                partition.sorted().printAsBulletList()
            }
        }
    }
}