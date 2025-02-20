package dev.mmauro.malverio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val COMPARATOR = compareBy<PlayerCard> {
    when (it) {
        is PlayerCard.CityCard -> 1
        is PlayerCard.EventCard.RationedEventCard -> 2
        is PlayerCard.EventCard.UnrationedEventCard -> 3
        is PlayerCard.ProduceSuppliesCard -> 4
        is PlayerCard.EpidemicCard -> 5
    }
}

@Serializable
sealed class PlayerCard : Card, Comparable<PlayerCard> {

    override fun compareTo(other: PlayerCard) = COMPARATOR.compare(this, other)

    @Serializable
    @SerialName("epidemic")
    class EpidemicCard : PlayerCard() {
        override fun text() = "EPIDEMIC ☢️"
    }

    @Serializable
    @SerialName("produce-supplies")
    class ProduceSuppliesCard(
        val usedSystemWideProductions: Int,
        val totalSystemWideProductions: Int,
    ) : PlayerCard() {
        init {
            require(usedSystemWideProductions < totalSystemWideProductions) {
                "system wide productions: used must be < than total"
            }
            require(totalSystemWideProductions > 0) {
                "system wide productions: total must be > than 0"
            }
        }

        fun productions() = "$usedSystemWideProductions/$totalSystemWideProductions"

        override fun text() = "Produce supplies (used system wide productions: ${productions()})"
    }

    sealed class EventCard : PlayerCard() {

        @Serializable
        @SerialName("rationed-event")
        class RationedEventCard(val event: RationedEvent) : EventCard() {
            override fun text() = "Rationed event (${event.name})"
        }

        @Serializable
        @SerialName("unrationed-event")
        class UnrationedEventCard(val event: UnrationedEvent) : EventCard() {
            override fun text() = "Unrationed event (${event.name})"
        }
    }

    @Serializable
    @SerialName("city")
    class CityCard(
        val city: City,
        /**
         * The number of not scratched off search slots in this card
         */
        val unsearched: Int = 0,
        /**
         * The improvement sticker that this card has
         */
        val improvement: Improvement? = null,
    ) : PlayerCard() {

        override fun toString() = buildString {
            append(city.name)
            val modifiers = listOfNotNull(
                unsearched.takeIf { it > 0 }?.let { "$it searchable slots" },
                improvement?.name,
            )
            if (modifiers.isNotEmpty()) {
                append(" (")
                append(modifiers.joinToString())
                append(')')
            }
        }

        override fun text() = city.color.textStyle(toString())

        @Serializable
        enum class Improvement(val description: String) {
            FOUNDATIONS("Counts as 2 cards for building supply center"),
            INFRASTRUCTURE("Counts as 3 cards for building supply center"),
        }
    }
}