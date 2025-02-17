package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.Timeline

abstract class BaseMove : Move {

    abstract fun isAllowed(game: Game): Boolean
    abstract fun perform(game: Game): Timeline.Item?

    override fun isAllowed(timeline: Timeline) = isAllowed(timeline.currentGame)

    override fun perform(timeline: Timeline): Timeline {
        val game = perform(timeline.currentGame)
        return if (game != null) {
            timeline.plus(game)
        } else {
            timeline
        }
    }
}