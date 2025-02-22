package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.Turn

object EpidemicIntensify : BaseMove() {

    override val name = "Epidemic: Intensify"

    override fun isAllowed(game: Game) = game.turn.epidemicStage == Turn.EpidemicStage.INTENSIFY

    override fun perform(game: Game): Timeline.Item {
        return game.doAction { intensify() }
    }
}