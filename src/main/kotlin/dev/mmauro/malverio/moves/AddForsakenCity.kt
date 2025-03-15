package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object AddForsakenCity : BaseMove() {

    override fun name(game: Game) = "Add forsaken city"

    override fun isAllowed(game: Game) = true

    override fun perform(game: Game): Timeline.Item? {
        val city = game
            .infectionDeck
            .deck
            .mapNotNull { it.cityOrNull() }
            .toSet()
            .select("Select city to forsake")
            ?: return null

        return game.doAction { copy(forsakenCities = forsakenCities + city) }
    }
}