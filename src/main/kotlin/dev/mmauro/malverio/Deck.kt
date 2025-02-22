package dev.mmauro.malverio

import kotlinx.serialization.Serializable

@Serializable
data class Deck<T>(
    val partitions: List<Set<T>>,
    val drawn: List<T>,
) {

    init {
        for (partition in partitions) {
            require(drawn.intersect(partition).isEmpty()) { "Intersection found between drawn and one partition" }
        }
    }

    val undrawnCards = partitions.flatten().toSet()
    val deck = drawn.toSet() + undrawnCards
    val size = deck.size

    constructor(deck: Set<T>) : this(
        partitions = listOf(deck),
        drawn = emptyList(),
    )

    fun drawCardFromTop(card: T): Deck<T> {
        require(card in partitions.first()) { "$card is not in top of deck (${partitions.first()})" }
        return discardCard(card)
    }

    fun drawCardFromBottom(card: T): Deck<T> {
        require(card in partitions.last()) { "$card is not in bottom of deck (${partitions.last()})" }
        return discardCard(card)
    }

    private fun discardCard(card: T) = Deck(
        drawn = drawn + card,
        partitions = partitions.removeCards(setOf(card)),
    )

    fun shuffleDrawnAndPlaceOnTop(): Deck<T> {
        if (drawn.isEmpty()) {
            return this
        }
        return Deck(
            drawn = emptyList(),
            partitions = listOf(drawn.toSet()) + partitions,
        )
    }

    fun removeCardFromDrawn(card: T): Deck<T> {
        require(card in drawn) { "$card is not in drawn cards ($drawn)" }
        return Deck(
            drawn = drawn - card,
            partitions = partitions,
        )
    }

    fun moveFromDrawnToTopOfDeck(card: T): Deck<T> {
        require(card in drawn) { "$card is not in drawn cards ($drawn)" }
        return Deck(
            drawn = drawn - card,
            partitions = listOf(setOf(card)) + partitions,
        )
    }
}


private fun <T> List<Set<T>>.removeCards(cards: Set<T>): List<Set<T>> {
    return map { it - cards }.filterNot { it.isEmpty() }
}
