package dev.mmauro.malverio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mmauro.malverio.CityColor
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.PlayerCard.EpidemicCard
import dev.mmauro.malverio.PlayerCard.EventCard
import dev.mmauro.malverio.PlayerCard.ProduceSuppliesCard
import dev.mmauro.malverio.Textable
import dev.mmauro.malverio.simulation.Group
import dev.mmauro.malverio.simulation.ProbabilityTree
import dev.mmauro.malverio.simulation.SimulationResults
import dev.mmauro.malverio.simulation.explore


private sealed interface PlayerCardType : Textable {
    data object Epidemic : PlayerCardType {
        override fun text() = "EPIDEMIC ☢️"
    }

    data object Produce : PlayerCardType {
        override fun text() = "Produce"
    }

    data object Event : PlayerCardType {
        override fun text() = "Event"
    }

    data class City(val color: CityColor) : PlayerCardType {
        override fun text() = color.text()
    }

    data object PortableAntiviralLab : PlayerCardType {
        override fun text() = "Portable antiviral lab"
    }
}

private fun PlayerCard.toType() = when (this) {
    is CityCard -> PlayerCardType.City(city.color)
    is EpidemicCard -> PlayerCardType.Epidemic
    is EventCard.RationedEventCard -> PlayerCardType.Event
    is EventCard.UnrationedEventCard -> PlayerCardType.Event
    is ProduceSuppliesCard -> PlayerCardType.Produce
    is PlayerCard.PortableAntiviralLabCard -> PlayerCardType.PortableAntiviralLab
}

private sealed interface InfectionCardType : Textable {
    data object City : InfectionCardType {
        override fun text() = "City 🏙️"
    }

    data object HollowMenGather : InfectionCardType {
        override fun text() = "Hollow men gather 🧟"
    }
}

private fun InfectionCard.toType() = when (this) {
    is InfectionCard.CityCard -> InfectionCardType.City
    is InfectionCard.HollowMenGather -> InfectionCardType.HollowMenGather
}

@Composable
fun PlayerCardsSimulationComposable(modifier: Modifier, cardSimulation: SimulationResults<PlayerCard>) {
    ProbabilityTreeComposable(
        modifier,
        cardSimulation.probabilityTree(
            { Group(it.toType(), isRelevant = true) },
            {
                when (it) {
                    is CityCard -> Group(it.city, isRelevant = true)
                    is EventCard -> Group(it, isRelevant = true)
                    else -> null
                }
            },
            {
                when (it) {
                    is CityCard -> Group(it, isRelevant = it.unsearched > 0 || it.improvement != null)
                    else -> null
                }
            },
        )
    )
}


@Composable
fun InfectionCardsSimulationComposable(modifier: Modifier, cardSimulation: SimulationResults<InfectionCard>) {
    ProbabilityTreeComposable(
        modifier,
        cardSimulation.probabilityTree(
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
    )
}


@Composable
fun ProbabilityTreeComposable(modifier: Modifier, probabilityTrees: List<ProbabilityTree>) {
    val nodes = buildList {
        probabilityTrees.explore { indentation, node ->
            add(indentation to node)
        }
    }
    LazyColumn(modifier) {
        items(nodes) { (indentation, node) ->
            Text(buildString {
                append("  ".repeat(indentation))
                append(" - ")
                append(node.group.item.text())
                append(": ")
                append(node.probability.format())
                val leafSize = node.leafSize()
                if (leafSize > 1) {
                    append(" (x$leafSize)")
                }
            })
        }
    }
}
