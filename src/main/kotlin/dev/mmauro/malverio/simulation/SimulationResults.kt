package dev.mmauro.malverio.simulation

import dev.mmauro.malverio.Card
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val PERCENT_FORMAT = DecimalFormat("##.##%", DecimalFormatSymbols.getInstance(Locale.ROOT))


data class SimulationResults<C : Card>(val simulations: List<Set<C>>) {

    fun <T> probabilitiesBy(group: (C) -> T): Map<T, Probability> {
        return simulations
            .map { it.map(group).toSet() }
            .flatten()
            .groupingBy { it }
            .eachCount()
            .mapValues { Probability(it.value / simulations.size.toDouble()) }
    }

    fun probabilityTree(vararg groups: (C) -> Group?): List<ProbabilityTree> {
        return probabilityTree(groups.asList())
    }

    fun probabilityTree(groups: List<(C) -> Group?>): List<ProbabilityTree> {
        if (groups.isEmpty()) return emptyList()

        val primaryGroupLambda = groups.first()
        val probabilities = probabilitiesBy(primaryGroupLambda)

        return probabilities.entries
            .sortedByDescending { it.value }
            .mapNotNull { (group, probability) ->
                if (group == null) return@mapNotNull null

                ProbabilityTree(
                    group = group,
                    probability = probability,
                    subTrees = probabilityTree(
                        groups
                            .drop(1)
                            .map { secondaryGroupLambda ->
                                { card: C ->
                                    if (primaryGroupLambda(card) == group) {
                                        secondaryGroupLambda(card)
                                    } else {
                                        null
                                    }
                                }
                            },
                    ),
                )
            }
    }
}

@JvmInline
value class Probability(val value: Double) : Comparable<Probability> {
    init {
        require(value in 0.0..1.0) { "probability is not in 0-1 range: $value" }
    }

    fun format(): String = PERCENT_FORMAT.format(value)

    override fun compareTo(other: Probability) = value.compareTo(other.value)
}