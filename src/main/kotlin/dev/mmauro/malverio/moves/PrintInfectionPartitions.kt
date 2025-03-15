package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.rendering.TextStyles.bold
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printAsBulletList
import dev.mmauro.malverio.printSection

object PrintInfectionPartitions : PrintMove() {

    override fun name(timeline: Timeline) = "Print infection partitions"

    override fun print(timeline: Timeline) {
        printSection("INFECTION PARTITIONS") {
            for (partition in timeline.currentGame.infectionDeck.partitions) {
                println(bold("Next ${partition.size} cards are from this pool:"))
                partition.cards.sorted().printAsBulletList()
            }
        }
    }
}