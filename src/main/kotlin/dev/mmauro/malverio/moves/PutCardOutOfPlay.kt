package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object PutCardOutOfPlay : BaseMove() {

    override fun name(game: Game) = "Put card out of play"

    override fun isAllowed(game: Game) =
        game.infectionDeck.drawn.filterIsInstance<InfectionCard.CityCard>().isNotEmpty()

    override fun perform(game: Game): Timeline.Item? {
        val card = game.infectionDeck.drawn.filterIsInstance<InfectionCard.CityCard>()
            .select("Select card to put out of play") ?: return null
        return game.doAction(card.text()) {
            removeInfectionCardFromPlay(card)
        }
    }
}