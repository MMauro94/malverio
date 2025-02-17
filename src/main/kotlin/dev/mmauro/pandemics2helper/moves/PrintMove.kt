package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Timeline

abstract class PrintMove : Move {

    override fun isAllowed(timeline: Timeline) = true

    override fun perform(timeline: Timeline): Timeline {
        print(timeline)
        return timeline
    }

    abstract fun print(timeline: Timeline)
}