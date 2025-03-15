package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printSection

object PrintTimeline : PrintMove() {

    override fun name(timeline: Timeline) = "Print timeline"

    override fun print(timeline: Timeline) {
        printSection("TIMELINE OF EVENTS") {
            for ((i, item) in timeline.games.withIndex()) {
                println("${i + 1}: ${item.description}")
            }
        }
    }
}