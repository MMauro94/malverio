package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Timeline

object Rollback : Move {

    override val name = "Rollback"

    override fun isAllowed(timeline: Timeline) = timeline.games.size > 1

    override fun perform(timeline: Timeline): Timeline {
        return Timeline(timeline.games.dropLast(1))
    }
}