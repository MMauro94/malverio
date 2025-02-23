package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.Card
import dev.mmauro.malverio.Deck
import dev.mmauro.malverio.JSON
import dev.mmauro.malverio.TERMINAL
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printSection
import kotlinx.serialization.json.Json
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.writeText

private val PERCENT_FORMAT = DecimalFormat("##.##%", DecimalFormatSymbols.getInstance(Locale.ROOT))

abstract class AbstractSimulateDrawMove<C> : PrintMove() where C : Card, C : Comparable<C> {

    protected abstract val cardTypeName: String

    protected abstract fun getDeck(timeline: Timeline): Deck<C>

    protected abstract fun getNumberOfCardsToDraw(timeline: Timeline): Int?

    override val name get() = "Simulate $cardTypeName draw"

    override fun print(timeline: Timeline) {
        val cards = getNumberOfCardsToDraw(timeline)
        if (cards != null) {
            printSection("RUNNING SIMULATION FOR $cards CARDS") {
                val simulationResults = getDeck(timeline).simulateDrawRandomCards(cards = cards, times = 10_000)
                printSimulationResults(simulationResults)
            }
        }
    }

    protected abstract fun Terminal.printSimulationResults(results: SimulationResults<C>)

    private fun Deck<C>.simulateDrawRandomCards(cards: Int, times: Int): SimulationResults<C> {
        return SimulationResults(
            buildList {
                repeat(times) {
                    add(simulateDrawRandomCards(cards))
                }
            },
        )
    }

    private fun Deck<C>.simulateDrawRandomCards(cards: Int): Set<C> {
        var deck = this
        return buildSet {
            repeat(cards) {
                val partition = deck.partitions.first()
                val data = partition.data.flatMap { d -> List(d.size) { d } }
                val card = data.random().cards.random()
                add(card)
                deck = deck.drawCardFromTop(card)
            }
        }
    }

    data class SimulationResults<C : Card>(val simulations: List<Set<C>>) {

        fun <T> probabilitiesBy(group: (C) -> T): Map<T, Probability> {
            return simulations
                .map { it.map(group).toSet() }
                .flatten()
                .groupingBy { it }
                .eachCount()
                .mapValues { Probability(it.value / simulations.size.toDouble()) }
        }
    }

    @JvmInline
    value class Probability(val value: Double) {
        init {
            require(value in 0.0..1.0) { "probability is not in 0-1 range: $value" }
        }

        fun format() = PERCENT_FORMAT.format(value)
    }
}