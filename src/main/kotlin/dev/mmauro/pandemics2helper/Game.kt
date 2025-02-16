package dev.mmauro.pandemics2helper

import dev.mmauro.pandemics2helper.events.DrawCardEvent
import dev.mmauro.pandemics2helper.events.Event
import dev.mmauro.pandemics2helper.events.EpidemicEvent

data class Game(
    val deck: Set<InfectionCard>,
    val discards: List<InfectionCard> = emptyList(),
    val partitionedDeck: List<Set<InfectionCard>> = listOf(deck),
) {

    init {
        for (partition in partitionedDeck) {
            require(discards.intersect(partition).isEmpty())
        }
        require(deck == (discards + partitionedDeck.flatten()).toSet())
    }

    fun addEvent(event: Event): Game {
        return when (event) {
            is DrawCardEvent -> {
                require(event.card in partitionedDeck.first()) {
                    "${event.card} is not in ${partitionedDeck.first()}"
                }
                discardCard(event.card)
            }

            is EpidemicEvent -> {
                require(event.card in partitionedDeck.last()) {
                    "${event.card} is not in ${partitionedDeck.last()}"
                }
                discardCard(event.card).intensify()
            }
        }
    }

    private fun discardCard(card: InfectionCard) = Game(
        deck = deck,
        discards = discards + card,
        partitionedDeck = partitionedDeck.map { it - card }.filterNot { it.isEmpty() },
    )

    private fun intensify() = Game(
        deck = deck,
        discards = emptyList(),
        partitionedDeck = listOf(discards.toSet()) + partitionedDeck,
    )

    fun addEvents(events: List<Event>) = events.fold(this) { prev, it -> prev.addEvent(it) }
}