package dev.mmauro.malverio.simulation

import dev.mmauro.malverio.City
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.Textable
import dev.mmauro.malverio.toType
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val PERCENT_FORMAT = DecimalFormat("##.##%", DecimalFormatSymbols.getInstance(Locale.ROOT))

val PLAYER_CARD_DIMENSIONS: List<(PlayerCard) -> Textable?> = listOf(
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
val INFECTION_CARD_DIMENSIONS: List<(ZombieOrInfect) -> Textable> = listOf(
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


class MultiDimensionSimulationBuilder<C>(
    val groups: List<(C) -> Textable?>,
) {
    val counter = GroupsCounter(
        groups.first(),
        groups.subList(1, groups.size),
    )
    val totalSimulations: Long get() = counter.occurrences

    class GroupsCounter<C>(
        val groupBy: (C) -> Textable?,
        val subGroups: List<(C) -> Textable?>,
        var occurrences: Long = 0,
    ) {
        val counters = mutableMapOf<Textable, GroupsCounter<C>>()

        fun addSimulation(drawnCards: Collection<C>) {
            occurrences++
            for ((item, cards) in drawnCards.groupBy(groupBy)) {
                if (item != null) {
                    counters.compute(item) { _, counter ->
                        val c = when {
                            counter != null -> counter
                            subGroups.isEmpty() -> GroupsCounter({ null }, emptyList())
                            else -> GroupsCounter(subGroups.first(), subGroups.subList(1, subGroups.size))
                        }
                        c.addSimulation(cards)
                        c
                    }
                }
            }
        }

        fun toProbabilityTree(
            totalSimulations: Long,
        ): List<ProbabilityTree> {
            return counters.entries
                .sortedByDescending { it.value.occurrences }
                .map { (item, counter) ->
                    ProbabilityTree(
                        item = item,
                        probability = Probability(counter.occurrences / totalSimulations.toDouble()),
                        subTrees = counter.toProbabilityTree(totalSimulations),
                    )
                }
        }
    }

    fun addSimulation(drawnCards: Set<C>) {
        counter.addSimulation(drawnCards)
    }

    fun toProbabilityTree(): List<ProbabilityTree> {
        return counter.toProbabilityTree(totalSimulations)
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

data class ZombiedCity(val city: City) : Textable {
    override fun plainText() = "\uD83E\uDDDF ${city.name}"
    override fun color() = city.color()
}

data class InfectedCity(val city: City) : Textable {
    override fun plainText() = "☣ ${city.name}"
    override fun color() = city.color()
}

sealed interface ZombieOrInfect : Textable {
    val card: InfectionCard.CityCard

    data class Zombie(override val card: InfectionCard.CityCard) : ZombieOrInfect {
        override fun plainText() = "\uD83E\uDDDF ${card.plainText()}"
        override fun color() = card.color()

    }

    data class Infect(override val card: InfectionCard.CityCard) : ZombieOrInfect {
        override fun plainText() = "☣ ${card.plainText()}"
        override fun color() = card.color()
    }
}
