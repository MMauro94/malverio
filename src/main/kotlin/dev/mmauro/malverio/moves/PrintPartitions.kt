package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printAsBulletList
import dev.mmauro.malverio.printSection

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