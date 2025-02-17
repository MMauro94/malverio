package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline

object EpidemicIntensify : BaseMove() {

    override val name = "Epidemic: Intensify"

    override fun isAllowed(game: Game) = game.isDuringEpidemic

    override fun perform(game: Game): Timeline.Item {
        return Timeline.Item(game.intensify(), name)
    }
}