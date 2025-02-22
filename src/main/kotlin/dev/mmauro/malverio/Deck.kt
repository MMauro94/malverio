package dev.mmauro.malverio

import kotlinx.serialization.Serializable

@Serializable
data class Deck<C>(
    val partitions: List<Set<C>>,
    val drawn: List<C>,
) {

    init {
        for (partition in partitions) {
            require(drawn.intersect(partition).isEmpty()) { "Intersection found between drawn and one partition" }
        }
    }

    val undrawn = partitions.flatten().toSet()
    val deck = drawn.toSet() + undrawn
    val size = deck.size

    constructor(deck: Set<C>) : this(
        partitions = listOf(deck),
        drawn = emptyList(),
    )

    fun drawCardFromTop(card: C): Deck<C> {
        require(card in partitions.first()) { "$card is not in top of deck (${partitions.first()})" }
        return discardCard(card)
    }

    fun drawCardFromBottom(card: C): Deck<C> {
        require(card in partitions.last()) { "$card is not in bottom of deck (${partitions.last()})" }
        return discardCard(card)
    }

    private fun discardCard(card: C) = Deck(
        drawn = drawn + card,
        partitions = partitions.removeCards(setOf(card)),
    )

    fun shuffleDrawnAndPlaceOnTop(): Deck<C> {
        if (drawn.isEmpty()) {
            return this
        }
        return Deck(
            drawn = emptyList(),
            partitions = listOf(drawn.toSet()) + partitions,
        )
    }

    fun removeCardFromDrawn(card: C): Deck<C> {
        require(card in drawn) { "$card is not in drawn cards ($drawn)" }
        return Deck(
            drawn = drawn - card,
            partitions = partitions,
        )
    }

    fun moveFromDrawnToTopOfDeck(card: C): Deck<C> {
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
