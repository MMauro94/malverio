package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.Turn

object EpidemicIncrease : BaseMove() {

    override fun name(game: Game) = "Epidemic: Increase"

    override fun isAllowed(game: Game) = game.turn.epidemicStage == Turn.EpidemicStage.INCREASE

    override fun perform(game: Game): Timeline.Item {
        return game.doAction { increase() }
    }
}