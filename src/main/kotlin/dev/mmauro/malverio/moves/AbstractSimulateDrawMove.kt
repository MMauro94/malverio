package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.Card
import dev.mmauro.malverio.Deck
import dev.mmauro.malverio.TERMINAL
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.printAsBulletList
import dev.mmauro.malverio.printSection

abstract class AbstractSimulateDrawMove<C> : PrintMove() where C : Card, C: Comparable<C> {

    protected abstract val cardTypeName: String

    protected abstract fun getDeck(timeline: Timeline): Deck<C>

    override val name get() = "Simulate $cardTypeName draw"

    override fun print(timeline: Timeline) {
        val selection = TERMINAL.interactiveSelectList(
            listOf("1", "2", "3", "4", "5"),
            title = "Number of $cardTypeName cards to draw",
        )
        val cards = selection?.toInt()
        if (cards != null) {
            printSection("RUNNING SIMULATION") {
                printDrawProbabilities(getDeck(timeline).partitions, cards)
            }
        }
    }

    private fun Terminal.printDrawProbabilities(partitions: List<Set<C>>, n: Int) {
        if (partitions.isEmpty()) {
            println("RIP, end of deck")
        } else if (partitions.first().size <= n) {
            println("For sure, you'll draw these cards:")
            partitions.first().sorted().printAsBulletList()
            val remaining = n - partitions.first().size
            if (remaining > 0) {
                printDrawProbabilities(partitions.drop(1), remaining)
            }
        } else {
            println("You'll draw $n out of these ${partitions.first().size} cards:")
            partitions.first().sorted().printAsBulletList()
        }
    }
}