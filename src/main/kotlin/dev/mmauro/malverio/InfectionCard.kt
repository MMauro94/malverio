package dev.mmauro.malverio

import androidx.compose.ui.graphics.Color
import dev.mmauro.malverio.InfectionCard.CityCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val COMPARATOR = compareBy<InfectionCard> {
    when (it) {
        is CityCard -> 0
        is InfectionCard.HollowMenGather -> 1
    }
}.thenBy {
    when (it) {
        is CityCard -> it.city.name
        is InfectionCard.HollowMenGather -> null
    }
}.thenBy {
    when (it) {
        is CityCard -> it.mutations.size
        is InfectionCard.HollowMenGather -> null
    }
}

@Serializable
sealed interface InfectionCard : Card, Comparable<InfectionCard> {

    override fun compareTo(other: InfectionCard) = COMPARATOR.compare(this, other)

    fun cityOrNull() = (this as? CityCard)?.city

    @OptIn(ExperimentalUuidApi::class)
    @Serializable
    @SerialName("city")
    data class CityCard(
        override val id: Uuid = Uuid.random(),
        val city: City,
        val mutations: Set<Mutation> = emptySet(),
    ) : InfectionCard, Comparable<InfectionCard> {

        override fun toString() = buildString {
            append(city.name)
            if (mutations.isNotEmpty()) {
                append('(')
                append(mutations.joinToString())
                append(')')
            }
        }

        override fun text() = city.color.textStyle(toString())
        override fun plainText() = toString()
        override fun color() = city.color()

        enum class Mutation {
            WELL_STOCKED,
            REBIRTH,
            BROKEN_LINK,
            LOCKDOWN,
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Serializable
    @SerialName("hollow-men-gather")
    data class HollowMenGather(
        override val id: Uuid = Uuid.random(),
    ) : InfectionCard {
        override fun plainText() = "Hollow Men Gather 🧟"
        override fun color() = Color(0.3f, 0.3f, 0.3f)
    }
}