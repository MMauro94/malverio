package dev.mmauro.malverio.simulation

import androidx.compose.ui.graphics.Color
import dev.mmauro.malverio.City
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.Textable
import dev.mmauro.malverio.toType
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val PERCENT_FORMAT = DecimalFormat("##.#########%", DecimalFormatSymbols.getInstance(Locale.ROOT))


data class SimulationResults<C>(val simulations: List<Set<C>>) {

    fun <T> probabilitiesBy(group: (C) -> T): Map<T, Probability> {
        return simulations
            .map { it.map(group).toSet() }
            .flatten()
            .groupingBy { it }
            .eachCount()
            .mapValues { Probability(it.value / simulations.size.toDouble()) }
    }

    fun probabilityTree(vararg groups: (C) -> Textable?): List<ProbabilityTree> {
        return probabilityTree(groups.asList())
    }

    fun probabilityTree(groups: List<(C) -> Textable?>): List<ProbabilityTree> {
        if (groups.isEmpty()) return emptyList()

        val primaryGroupLambda = groups.first()
        val probabilities = probabilitiesBy(primaryGroupLambda)

        return probabilities.entries
            .sortedByDescending { it.value }
            .mapNotNull { (item, probability) ->
                if (item == null) return@mapNotNull null

                ProbabilityTree(
                    item = item,
                    probability = probability,
                    subTrees = probabilityTree(
                        groups
                            .drop(1)
                            .map { secondaryGroupLambda ->
                                { card: C ->
                                    if (primaryGroupLambda(card) == item) {
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
        { it.toType() },
        {
            when (it) {
                is CityCard -> it.city
                else -> it
            }
        },
        {
            when (it) {
                is CityCard -> it
                else -> null
            }
        },
    )
}

fun SimulationResults<ZombieOrInfect>.infectionProbabilityTree(): List<ProbabilityTree> {
    return probabilityTree(
        {
            when (it) {
                is ZombieOrInfect.Infect -> InfectedCity(it.card.city)
                is ZombieOrInfect.Zombie -> ZombiedCity(it.card.city)
            }
        },
        {
            it
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

data class ZombiedCity(val city: City) : Textable {
    override fun plainText() = "\uD83E\uDDDF ${city.name}"
    override fun color() = city.color()
}

data class InfectedCity(val city: City) : Textable {
    override fun plainText() = "☣ ${city.name}"
    override fun color() = city.color()
}

sealed interface ZombieOrInfect : Textable{
    val card: InfectionCard.CityCard

    data class Zombie(override val card: InfectionCard.CityCard) : ZombieOrInfect{
        override fun plainText() = "\uD83E\uDDDF ${card.plainText()}"
        override fun color() = card.color()

    }
    data class Infect(override val card: InfectionCard.CityCard) : ZombieOrInfect{
        override fun plainText() = "☣ ${card.plainText()}"
        override fun color() = card.color()
    }
}
