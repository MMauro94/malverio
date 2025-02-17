package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline

object Rollback : Move {

    override val name = "Rollback"

    override fun isAllowed(timeline: Timeline) = timeline.games.size > 1

    override fun perform(timeline: Timeline): Timeline {
        return Timeline(timeline.games.dropLast(1))
    }
}