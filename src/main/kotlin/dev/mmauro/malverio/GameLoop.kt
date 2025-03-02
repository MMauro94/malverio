package dev.mmauro.malverio

import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors.cyan
import dev.mmauro.malverio.moves.*
import java.nio.file.Path
import kotlin.system.exitProcess

private val MOVES = listOf(
    DrawPlayerCard,
    DrawInfectionCard,
    EpidemicIncrease,
    EpidemicInfect,
    EpidemicIntensify,
    RemoveInfectionCard,
    Forecast,
    NextTurn,
    SimulateInfectionDraw,
    SimulatePlayerDraw,
    ShuffleInfectionDeck,
    AddForsakenCity,
    PrintInfectionPartitions,
    PrintTimeline,
    PrintDiscards,
    Rollback,
)

class GameLoop(
    private val startTimeline: Timeline,
    private val savegame: Path,
) {

    fun run(): Nothing {
        var timeline = startTimeline
        TERMINAL.println("Numbers of cards in the infection deck: ${timeline.currentGame.infectionDeck.size}")
        timeline.save(savegame)

        while (true) {
            TERMINAL.println("Current player turn: ${cyan(timeline.currentGame.turn.currentPlayer.text())}")
            TERMINAL.println("Turns left (this one included): ${cyan(timeline.currentGame.turnsLeft().toString())}")
            val moves = MOVES.filter { it.isAllowed(timeline) }
            val selection = TERMINAL.interactiveSelectList(
                moves.map { it.name },
                title = "Select move",
            ) ?: exit()

            val lastGame = timeline.currentGame
            timeline = moves.single { it.name == selection }.perform(timeline)
            timeline.save(savegame)
            if (lastGame !== timeline.currentGame) {
                TERMINAL.println(timeline.games.last().description)
            }
            TERMINAL.println()
        }
    }

    private fun exit(): Nothing {
        TERMINAL.println("Goodbye!")
        exitProcess(0)
    }
}