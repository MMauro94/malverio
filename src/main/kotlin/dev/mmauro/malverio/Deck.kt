package dev.mmauro.malverio

import kotlinx.serialization.Serializable

@Serializable
data class Deck<C : Card>(
    val partitions: List<Partition<C>>,
    val drawn: List<C>,
    val removed: List<C> = emptyList(),
) {

    val undrawn = partitions.flatMap { it.cards }.toSet()
    val deck = drawn.toSet() + undrawn
    val size = deck.size

    init {
        require(drawn.all { c -> partitions.none { p -> c in p } }) {
            "Intersection found between drawn and one partition"
        }
        require(deck.size == partitions.sumOf { it.size } + drawn.size) {
            "partitions sizes (${partitions.sumOf { it.size }}) + drawn (${drawn.size}) must match deck size (${deck.size})"
        }
    }

    constructor(cards: Set<C>) : this(
        partitions = listOf(cards.asPartition()),
        drawn = emptyList(),
        removed = emptyList(),
    )

    fun randomCardFromTop(): C {
        return partitions.first().randomCard()
    }

    fun randomCardFromBottom(): C {
        return partitions.last().randomCard()
    }

    fun randomCard(): C {
        return undrawn.random()
    }

    fun drawCard(card: C): Deck<C> {
        require(card in undrawn) { "$card is not in the deck (${partitions.first()})" }
        return Deck(
            drawn = drawn + card,
            partitions = partitions.mapNotNull { it.tryDrawCard(card) },
            removed = removed,
        )
    }

    fun drawCardFromTop(card: C): Deck<C> {
        require(card in partitions.first()) { "$card is not in top of deck (${partitions.first()})" }
        return Deck(
            drawn = drawn + card,
            partitions = listOfNotNull(partitions.first().drawCard(card)) + partitions.drop(1)
                .mapNotNull { it.removeCard(card) },
            removed = removed,
        )
    }

    fun drawCardFromBottom(card: C): Deck<C> {
        require(card in partitions.last()) { "$card is not in bottom of deck (${partitions.last()})" }
        return Deck(
            drawn = drawn + card,
            partitions = partitions.dropLast(1).mapNotNull { it.removeCard(card) } + listOfNotNull(
                partitions.last().drawCard(card)
            ),
            removed = removed,
        )
    }

    fun shuffleDrawnAndPlaceOnTop(): Deck<C> {
        if (drawn.isEmpty()) {
            return this
        }
        return Deck(
            drawn = emptyList(),
            partitions = listOf(drawn.toSet().asPartition()) + partitions,
            removed = removed,
        )
    }

    fun removeCard(card: C): Deck<C> {
        return Deck(
            drawn = drawn - card,
            partitions = partitions.mapNotNull { it.removeCard(card) },
            removed = removed + card,
        )
    }

    fun removeCardFromDrawn(card: C): Deck<C> {
        require(card in drawn) { "$card is not in drawn cards ($drawn)" }
        return Deck(
            drawn = drawn - card,
            partitions = partitions,
            removed = removed + card,
        )
    }

    fun moveFromDrawnToTopOfDeck(card: C): Deck<C> {
        require(card in drawn) { "$card is not in drawn cards ($drawn)" }
        return Deck(
            drawn = drawn - card,
            partitions = listOf(setOf(card).asPartition()) + partitions,
            removed = removed,
        )
    }

    @Serializable
    data class Partition<C : Card>(val data: Set<Data<C>>) {

        val size = data.sumOf { it.size }
        val cards = data.flatMap { it.cards }.toSet()

        init {
            require(data.isNotEmpty())
            for (data1 in data) {
                for (data2 in data) {
                    if (data1 !== data2) {
                        require(data1.cards.intersect(data2.cards).isEmpty()) {
                            "intersection found within partition data: $data1, $data2"
                        }
                    }
                }
            }
        }

        operator fun contains(card: C) = card in cards

        fun randomCard(): C {
            val data = data.flatMap { d -> List(d.size) { d } }
            return data.random().cards.random()
        }

        fun drawCard(card: C): Partition<C>? {
            require(card in this) { "cannot draw card $card from this partition" }
            return tryDrawCard(card)
        }

        fun tryDrawCard(card: C): Partition<C>? {
            return mapData { it.tryDrawCard(card) }
        }

        fun removeCard(card: C): Partition<C>? {
            return when {
                card !in this -> this
                else -> mapData { it.removeCard(card) }
            }
        }

        private fun mapData(mapper: (Data<C>) -> Data<C>?): Partition<C>? {
            return data.mapNotNull { mapper(it) }.ifEmpty { null }?.let { Partition(it.toSet()) }
        }

        /**
         * @property size number of cards in this data
         * @property cards set of cards that could be present in this partition
         */
        @Serializable
        data class Data<C : Card>(val size: Int, val cards: Set<C>) {
            init {
                require(size in 1..cards.size)
                require(cards.isNotEmpty())
            }

            operator fun contains(card: C) = card in cards

            fun tryDrawCard(card: C): Data<C>? {
                return when {
                    card !in this -> this
                    size == 1 -> null
                    else -> Data(
                        size = size - 1,
                        cards = cards - card,
                    )
                }
            }

            fun removeCard(card: C): Data<C>? {
                return when {
                    card !in this -> this
                    size == 1 -> null
                    else -> Data(
                        size = size,
                        cards = cards - card,
                    )
                }
            }
        }
    }
}

private fun <C : Card> Set<C>.asPartition() = Deck.Partition(setOf(Deck.Partition.Data(size = size, cards = this)))
