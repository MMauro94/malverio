package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.Turn
import dev.mmauro.malverio.select

object EpidemicIncrease : BaseMove() {

    override val name = "Epidemic: Increase"

    override fun isAllowed(game: Game) = game.turn.epidemicStage == Turn.EpidemicStage.INCREASE

    override fun perform(game: Game): Timeline.Item {
        return game.doAction { increase() }
    }
}