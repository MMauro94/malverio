package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.rendering.TextColors.cyan
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline

abstract class BaseMove : Move {

    abstract fun name(game: Game): String
    abstract fun isAllowed(game: Game): Boolean
    abstract fun perform(game: Game): Timeline.Item?

    override fun isAllowed(timeline: Timeline) = isAllowed(timeline.currentGame)
    override fun name(timeline: Timeline) = name(timeline.currentGame)

    override fun perform(timeline: Timeline): Timeline {
        val game = perform(timeline.currentGame)
        return if (game != null) {
            timeline.plus(game)
        } else {
            timeline
        }
    }

    fun Game.doAction(details: String? = null, action: Game.() -> Game): Timeline.Item {
        return Timeline.Item(
            this.action(),
            description = "${cyan(turn.currentPlayer.text())}: ${name(this)} ${details.orEmpty()}"
        )
    }
}
