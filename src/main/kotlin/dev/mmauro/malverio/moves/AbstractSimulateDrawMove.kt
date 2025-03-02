package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.Card
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Textable
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printSection
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val PERCENT_FORMAT = DecimalFormat("##.##%", DecimalFormatSymbols.getInstance(Locale.ROOT))

abstract class AbstractSimulateDrawMove<C> : PrintMove() where C : Card, C : Comparable<C> {

    protected abstract val cardTypeName: String

    protected abstract fun getNumberOfCardsToDraw(timeline: Timeline): Int

    protected abstract fun Game.doRandomDraw(): RandomDrawResult<C>

    override val name get() = "Simulate $cardTypeName draw"

    override fun print(timeline: Timeline) {
        val cards = getNumberOfCardsToDraw(timeline)
        printSection("RUNNING SIMULATION FOR $cards CARDS") {
            val simulationResults = simulateDrawRandomCards(
                timeline = timeline,
                cards = cards,
                times = 10_000,
            )
            printSimulationResults(simulationResults)
        }
    }

    protected abstract fun Terminal.printSimulationResults(results: SimulationResults<C>)

    protected data class Group(
        val item: Textable,
        val isRelevant: Boolean,
    )

    protected data class ProbabilityTree(
        val group: Group,
        val probability: Probability,
        val subTrees: List<ProbabilityTree>,
    ) {

        fun hasInterestingSubtrees(): Boolean {
            return subTrees.any { it.group.isRelevant || it.hasInterestingSubtrees() }
        }

        fun leafSize(): Int {
            return if (subTrees.isEmpty()) return 1
            else subTrees.sumOf { it.leafSize() }
        }

        fun print(terminal: Terminal, indentation: Int = 0) {
            if (subTrees.size == 1) {
                subTrees.single().print(terminal, indentation)
            } else {
                val leafSize = leafSize()
                terminal.println(buildString {
                    append("  ".repeat(indentation))
                    append(" - ")
                    append(group.item.text())
                    append(": ")
                    append(probability.format())
                    if (leafSize > 1) {
                        append(" (x$leafSize)")
                    }
                })

                if (hasInterestingSubtrees()) {
                    for (subTree in subTrees) {
                        subTree.print(terminal, indentation + 1)
                    }
                }
            }
        }
    }

    protected fun Iterable<ProbabilityTree>.print(terminal: Terminal) {
        forEach { it.print(terminal = terminal) }
    }

    private fun simulateDrawRandomCards(timeline: Timeline, cards: Int, times: Int): SimulationResults<C> {
        return SimulationResults(
            buildList {
                repeat(times) {
                    add(simulateDrawRandomCards(timeline, cards))
                }
            },
        )
    }

    private fun simulateDrawRandomCards(timeline: Timeline, cards: Int): Set<C> {
        var game = timeline.currentGame
        return buildSet {
            repeat(cards) {
                val (drawnCards, nextGame) = game.doRandomDraw()
                addAll(drawnCards)
                game = nextGame
            }
        }
    }

    protected data class RandomDrawResult<C: Card>(
        val drawnCards: Set<C>,
        val game: Game,
    )

    protected data class SimulationResults<C : Card>(val simulations: List<Set<C>>) {

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
}