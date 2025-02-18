package dev.mmauro.malverio

import com.github.ajalt.mordant.input.interactiveSelectList
import dev.mmauro.malverio.moves.DrawInfectionCard
import dev.mmauro.malverio.moves.EpidemicInfect
import dev.mmauro.malverio.moves.EpidemicIntensify
import dev.mmauro.malverio.moves.Forecast
import dev.mmauro.malverio.moves.PrintDiscards
import dev.mmauro.malverio.moves.PrintPartitions
import dev.mmauro.malverio.moves.PrintTimeline
import dev.mmauro.malverio.moves.RemoveInfectionCard
import dev.mmauro.malverio.moves.Rollback
import dev.mmauro.malverio.moves.SimulateDraw
import java.nio.file.Path
import kotlin.system.exitProcess

private val MOVES = listOf(
    DrawInfectionCard,
    EpidemicInfect,
    EpidemicIntensify,
    RemoveInfectionCard,
    Forecast,
    SimulateDraw,
    PrintPartitions,
    PrintTimeline,
    PrintDiscards,
    Rollback,
)

class GameLoop(
    private val startTimeline: Timeline,
    private val savegame: Path,
) {

    fun run() : Nothing {
        var timeline = startTimeline
        TERMINAL.println("Numbers of cards in the infection deck: ${timeline.currentGame.infectionDeck.size}")
        timeline.save(savegame)

        while (true) {
            val moves = MOVES.filter { it.isAllowed(timeline) }
            val selection = TERMINAL.interactiveSelectList(
                moves.map { it.name },
                title = "Select move",
            ) ?: exit()

            timeline = moves.single { it.name == selection }.perform(timeline)
            timeline.save(savegame)
        }
    }

    private fun exit(): Nothing {
        TERMINAL.println("Goodbye!")
        exitProcess(0)
    }
}