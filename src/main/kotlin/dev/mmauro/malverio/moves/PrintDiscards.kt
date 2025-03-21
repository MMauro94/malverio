package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printAsBulletList
import dev.mmauro.malverio.printSection

object PrintDiscards : PrintMove() {

    override fun name(timeline: Timeline) = "Print discards"

    override fun print(timeline: Timeline) {
        printSection("DISCARDS") {
            timeline.currentGame.infectionDeck.drawn.sorted().printAsBulletList()
        }
    }
}