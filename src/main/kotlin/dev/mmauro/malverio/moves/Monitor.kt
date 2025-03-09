package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.input.interactiveSelectList
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.TERMINAL
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.select

object Monitor : BaseMove() {

    override val name = "Monitor"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic()

    override fun perform(game: Game): Timeline.Item? {
        val range = 1..(10.coerceAtMost(game.playerDeck.undrawn.size))
        val cardsCount = TERMINAL.interactiveSelectList(
            range.map { it.toString() },
            title = "Number of cards to monitor",
        )?.toInt() ?: return null

        var playerDeck = game.playerDeck
        val cards = List(cardsCount) {
            val card = playerDeck.partitions.first().cards
                .select("Select cards that you found (${it + 1}/$cardsCount):")
                ?: return null
            playerDeck = playerDeck.drawCardFromTop(card)
            card
        }.toMutableList()

        var ret = game
        TERMINAL.println("Now, let's discard the cards, one at a time")
        repeat(cardsCount) {
            val card = cards.select("Select card number ${it + 1}:") ?: return null
            ret = ret.monitorPlayerCard(card)
            cards -= card
        }
        return game.doAction("Monitored $cardsCount cards") {
            ret
        }
    }
}