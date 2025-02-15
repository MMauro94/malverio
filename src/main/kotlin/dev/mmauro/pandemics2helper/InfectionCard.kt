package dev.mmauro.pandemics2helper


class InfectionCard(val city: City, val mutations: Set<Mutation> = emptySet()) {

    override fun toString() = buildString {
        append(city.name)
        if (mutations.isNotEmpty()) {
            append('(')
            append(mutations.joinToString())
            append(')')
        }
    }

    enum class Mutation {
        WELL_STOCKED
    }
}