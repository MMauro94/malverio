package dev.mmauro.malverio.simulation

import dev.mmauro.malverio.Card
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.toType
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val PERCENT_FORMAT = DecimalFormat("##.#########%", DecimalFormatSymbols.getInstance(Locale.ROOT))


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

fun SimulationResults<PlayerCard>.playerProbabilityTree(): List<ProbabilityTree> {
    return probabilityTree(
        { Group(it.toType(), isRelevant = true) },
        {
            when (it) {
                is CityCard -> Group(it.city, isRelevant = true)
                else -> Group(it, isRelevant = true)
            }
        },
        {
            when (it) {
                is CityCard -> Group(it, isRelevant = it.unsearched > 0 || it.improvement != null)
                else -> null
            }
        },
    )
}

fun SimulationResults<InfectionCard>.infectionProbabilityTree(): List<ProbabilityTree> {
    return probabilityTree(
        { Group(it.toType(), isRelevant = true) },
        {
            when (it) {
                is InfectionCard.CityCard -> Group(it.city, isRelevant = true)
                is InfectionCard.HollowMenGather -> Group(it, isRelevant = false)
            }
        },
        {
            when (it) {
                is InfectionCard.CityCard -> Group(it, isRelevant = it.mutations.isNotEmpty())
                is InfectionCard.HollowMenGather -> null
            }
        },
    )
}

@JvmInline
value class Probability(val value: Double) : Comparable<Probability> {
    init {
        require(value in 0.0..1.0) { "probability is not in 0-1 range: $value" }
    }

    fun format(): String = PERCENT_FORMAT.format(value)

    override fun compareTo(other: Probability) = value.compareTo(other.value)
}