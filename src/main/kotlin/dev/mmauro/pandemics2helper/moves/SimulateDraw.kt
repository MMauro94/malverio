package dev.mmauro.pandemics2helper.moves

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.warning
import dev.mmauro.pandemics2helper.InfectionCard
import dev.mmauro.pandemics2helper.TERMINAL
import dev.mmauro.pandemics2helper.Timeline
import dev.mmauro.pandemics2helper.printAsBulletList
import dev.mmauro.pandemics2helper.printSection

object SimulateDraw : PrintMove() {
    override val name = "Simulate draw"

    override fun isAllowed(timeline: Timeline) = DrawInfectionCard.isAllowed(timeline)

    override fun print(timeline: Timeline) {
        val selection = TERMINAL.interactiveSelectList(
            listOf("1", "2", "3", "4", "5"),
            title = "Number of cards to draw",
        )
        val cards = selection?.toInt()
        if (cards != null) {
            printSection("RUNNING SIMULATION") {
                warning("Remember that this assumes no epidemics will be drawn!")
                printDrawProbabilities(timeline.currentGame.partitionedDeck, cards)
            }
        }
    }

    private fun Terminal.printDrawProbabilities(partitions: List<Set<InfectionCard>>, n: Int) {
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