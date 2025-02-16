package dev.mmauro.pandemics2helper.moves

import com.github.ajalt.mordant.input.interactiveSelectList
import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.TERMINAL
import dev.mmauro.pandemics2helper.select

object Forecast : Move {
    override val name = "Forecast"

    override fun isAllowed(game: Game) = !game.isDuringEpidemic

    override fun perform(game: Game): Game {
        val selection = TERMINAL.interactiveSelectList(
            listOf("4", "5", "6", "7", "8", "9", "10"),
            title = "Number of cards to forecast",
        )
        val cardsCount = selection?.toInt()?.coerceAtMost(game.undrawnCards.size) ?: return game

        var nextGame = game
        val cards = List(cardsCount) {
            val card = nextGame.partitionedDeck.first()
                .select("Select cards that you found (${it + 1}/$cardsCount):")
                ?: return game
            nextGame = nextGame.drawCard(card)
            card
        }.toMutableList()

        TERMINAL.println("Now, let's select the order of the cards, from top to bottom")
        val sortedCards = List(cardsCount) {
            val card = cards.select("Select card number ${it + 1}:") ?: return game
            cards -= card
            card
        }
        sortedCards.reversed().forEach { nextGame = nextGame.moveToTopOfDeck(it) }
        return nextGame
    }
}