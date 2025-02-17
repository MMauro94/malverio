package dev.mmauro.pandemics2helper

private val COMPARATOR = compareBy<InfectionCard> { it.city.name}.thenBy { it.mutations.size }

class InfectionCard(val city: City, val mutations: Set<Mutation> = emptySet()): Comparable<InfectionCard> {

    override fun toString() = buildString {
        append(city.name)
        if (mutations.isNotEmpty()) {
            append('(')
            append(mutations.joinToString())
            append(')')
        }
    }

    fun text() = city.color.textStyle(toString())

    override fun compareTo(other: InfectionCard) = COMPARATOR.compare(this, other)

    enum class Mutation {
        WELL_STOCKED
    }
}