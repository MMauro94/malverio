package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.Timeline

object EpidemicIntensify : BaseMove() {

    override val name = "Epidemic: Intensify"

    override fun isAllowed(game: Game) = game.isDuringEpidemic

    override fun perform(game: Game): Timeline.Item {
        return Timeline.Item(game.intensify(), name)
    }
}