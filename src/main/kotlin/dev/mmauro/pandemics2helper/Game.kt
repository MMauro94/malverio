package dev.mmauro.pandemics2helper

data class Game(
    val discards: List<InfectionCard>,
    val partitionedDeck: List<Set<InfectionCard>>,
    val isDuringEpidemic: Boolean,
) {

    constructor(deck: Set<InfectionCard>) : this(
        discards = emptyList(),
        partitionedDeck = listOf(deck),
        isDuringEpidemic = false,
    )

    val deck = (discards + partitionedDeck.flatten()).toSet()

    val undrawnCards = partitionedDeck.flatten().toSet()

    init {
        for (partition in partitionedDeck) {
            require(discards.intersect(partition).isEmpty())
        }
    }

    fun drawCard(card: InfectionCard): Game {
        requireNotInEpidemic()
        require(card in partitionedDeck.first()) { "$card is not in top of deck (${partitionedDeck.first()})" }
        return discardCard(card)
    }

    private fun discardCard(card: InfectionCard) = Game(
        discards = discards + card,
        partitionedDeck = partitionedDeck.removeCards(setOf(card)),
        isDuringEpidemic = isDuringEpidemic,
    )

    fun infect(card: InfectionCard): Game {
        requireNotInEpidemic()
        require(card in partitionedDeck.last()) { "$card is not in bottom of deck (${partitionedDeck.last()})" }
        return discardCard(card).copy(isDuringEpidemic = true)
    }

    fun intensify(): Game {
        require(isDuringEpidemic) { "Intensify can only be performed during epidemics" }
        return Game(
            discards = emptyList(),
            partitionedDeck = listOf(discards.toSet()) + partitionedDeck,
            isDuringEpidemic = false,
        )
    }

    fun removeCard(card: InfectionCard): Game {
        require(card in discards) { "$card is not in discards ($discards)" }
        return Game(
            discards = discards - card,
            partitionedDeck = partitionedDeck,
            isDuringEpidemic = isDuringEpidemic,
        )
    }

    fun moveToTopOfDeck(card: InfectionCard): Game {
        requireNotInEpidemic()
        require(card in discards) { "$card is not in discards ($discards)" }
        return Game(
            discards = discards - card,
            partitionedDeck = listOf(setOf(card)) + partitionedDeck,
            isDuringEpidemic = isDuringEpidemic,
        )
    }
}

private fun List<Set<InfectionCard>>.removeCards(cards: Set<InfectionCard>): List<Set<InfectionCard>> {
    return map { it - cards }.filterNot { it.isEmpty() }
}

private fun Game.requireNotInEpidemic() {
    require(!isDuringEpidemic) { "Cannot perform this move during epidemic" }
}