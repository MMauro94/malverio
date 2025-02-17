package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.printAsBulletList
import dev.mmauro.pandemics2helper.printSection

object PrintDiscards : PrintMove() {

    override val name = "Print discards"

    override fun print(timeline: Timeline) {
        printSection("DISCARDS") {
            timeline.currentGame.discards.sorted().printAsBulletList()
        }
    }
}