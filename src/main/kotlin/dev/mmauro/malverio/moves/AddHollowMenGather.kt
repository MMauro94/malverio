package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Timeline
import kotlin.uuid.ExperimentalUuidApi

object AddHollowMenGather : BaseMove() {

    override fun name(game: Game) = "Add Hollow Men Gather card to discard pile"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic()

    @OptIn(ExperimentalUuidApi::class)
    override fun perform(game: Game): Timeline.Item {
        return Timeline.Item(
            game = game.copy(
                infectionDeck = game.infectionDeck.copy(
                    drawn = game.infectionDeck.drawn + InfectionCard.HollowMenGather()
                )
            ),
            description = "Add Hollow Men Gather cart to discard pile"
        )
    }
}