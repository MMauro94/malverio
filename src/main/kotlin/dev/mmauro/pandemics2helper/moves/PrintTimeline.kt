package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.printSection

object PrintTimeline : PrintMove() {

    override val name = "Print timeline"

    override fun print(timeline: Timeline) {
        printSection("TIMELINE OF EVENTS") {
            for ((i, item) in timeline.games.withIndex()) {
                println("${i + 1}: ${item.description}")
            }
        }
    }
}