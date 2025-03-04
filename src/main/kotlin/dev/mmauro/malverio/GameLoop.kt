package dev.mmauro.malverio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors.cyan
import dev.mmauro.malverio.moves.*
import dev.mmauro.malverio.ui.GameComposable
import java.nio.file.Path
import kotlin.concurrent.thread
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
        val currentGame = mutableStateOf(timeline.currentGame)

        launchUI(currentGame)

        while (true) {
            currentGame.value = timeline.currentGame
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

    private fun launchUI(currentGame: MutableState<Game>) {
        thread {
            singleWindowApplication(state = WindowState(width = 1280.dp, height = 960.dp)) {
                MaterialTheme(darkColorScheme()) {
                    Surface(Modifier.fillMaxSize()) {
                        GameComposable(currentGame.value)
                    }
                }
            }
        }
    }

    private fun exit(): Nothing {
        TERMINAL.println("Goodbye!")
        exitProcess(0)
    }
}