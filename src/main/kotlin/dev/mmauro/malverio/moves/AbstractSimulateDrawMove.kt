package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.Card
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printSection
import dev.mmauro.malverio.simulation.SimulationResults

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
}