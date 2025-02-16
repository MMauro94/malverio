package dev.mmauro.pandemics2helper.moves

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.warning
import dev.mmauro.pandemics2helper.Game
import dev.mmauro.pandemics2helper.InfectionCard
import dev.mmauro.pandemics2helper.TERMINAL
import dev.mmauro.pandemics2helper.printSection

object SimulateDraw : PrintMove() {
    override val name = "Simulate draw"

    override fun isAllowed(game: Game) = DrawInfectionCard.isAllowed(game)

    override fun print(game: Game) {
        val selection = TERMINAL.interactiveSelectList(
            listOf("1", "2", "3", "4", "5"),
            title = "Number of cards to draw",
        )
        val cards = selection?.toInt()
        if (cards != null) {
            printSection("RUNNING SIMULATION") {
                warning("Remember that this assumes no epidemics will be drawn!")
                printDrawProbabilities(game.partitionedDeck, cards)
            }
        }
    }

    private fun Terminal.printDrawProbabilities(partitions: List<Set<InfectionCard>>, n: Int) {
        if (partitions.isEmpty()) {
            println("RIP, mazzo finito")
        } else if (partitions.first().size <= n) {
            println("For sure, you'll draw these cards:")
            for (card in partitions.first()) {
                println(" - $card")
            }
            val remaining = n - partitions.first().size
            if (remaining > 0) {
                printDrawProbabilities(partitions.drop(1), remaining)
            }
        } else {
            println("You'll draw $n out of these ${partitions.first().size} cards:")
            for (card in partitions.first()) {
                println(" - $card")
            }
        }
    }
}