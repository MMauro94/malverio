package dev.mmauro.malverio

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val COMPARATOR = compareBy<InfectionCard> { it.city.name}.thenBy { it.mutations.size }

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class InfectionCard(
    override val id: Uuid = Uuid.random(),
    val city: City,
    val mutations: Set<Mutation> = emptySet(),
): Card, Comparable<InfectionCard> {

    override fun toString() = buildString {
        append(city.name)
        if (mutations.isNotEmpty()) {
            append('(')
            append(mutations.joinToString())
            append(')')
        }
    }

    override fun text() = city.color.textStyle(toString())

    override fun compareTo(other: InfectionCard) = COMPARATOR.compare(this, other)

    enum class Mutation {
        WELL_STOCKED
    }
}