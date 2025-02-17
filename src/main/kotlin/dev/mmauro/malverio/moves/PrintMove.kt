package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline

abstract class PrintMove : Move {

    override fun isAllowed(timeline: Timeline) = true

    override fun perform(timeline: Timeline): Timeline {
        print(timeline)
        return timeline
    }

    abstract fun print(timeline: Timeline)
}