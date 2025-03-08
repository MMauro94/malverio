package dev.mmauro.malverio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
 import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.simulation.GameSimulatron1000
import dev.mmauro.malverio.simulation.INFECTION_CARD_DIMENSIONS
import dev.mmauro.malverio.simulation.MultiDimensionSimulationBuilder
import dev.mmauro.malverio.simulation.PLAYER_CARD_DIMENSIONS
import dev.mmauro.malverio.simulation.ProbabilityTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

val SIMULATION_LIMIT = 10_000
val FULL_REFRESH_FREQUENCY = 5_000L

private sealed interface SimulationState {
    data object Simulating : SimulationState
    data object DuringEpidemic : SimulationState
    data class Simulated(
        val playerCardSimulation: List<ProbabilityTree>,
        val infectionCardSimulation: List<ProbabilityTree>,
    ) : SimulationState
}

@Composable
fun GameComposable(game: Game) {
    var simulationState by remember {
        mutableStateOf<SimulationState>(SimulationState.Simulating)
    }
    LaunchedEffect(game) {
        if (game.isDuringEpidemic()) {
            simulationState = SimulationState.DuringEpidemic
        } else {
            simulationState = SimulationState.Simulating
            coroutineScope {
                val parallel = Runtime.getRuntime().availableProcessors().minus(1).coerceAtLeast(1)
                val simulations = Channel<GameSimulatron1000.SimulationResult>(parallel * 16)
                List(parallel) {
                    launch(Dispatchers.Default) {
                        repeat(SIMULATION_LIMIT) {
                            simulations.send(GameSimulatron1000.simulateRandomMove(game))
                        }
                    }
                }
                val results = Channel<SimulationState.Simulated>(capacity = Channel.CONFLATED)
                launch(Dispatchers.Default) {
                    val playerCardSimulatedDraws = MultiDimensionSimulationBuilder(PLAYER_CARD_DIMENSIONS)
                    val infectionCardSimulatedDraws = MultiDimensionSimulationBuilder(INFECTION_CARD_DIMENSIONS)
                    suspend fun send() {
                        results.send(
                            SimulationState.Simulated(
                                playerCardSimulatedDraws.toProbabilityTree(),
                                infectionCardSimulatedDraws.toProbabilityTree(),
                            )
                        )
                    }
                    for (simulation in simulations) {
                        playerCardSimulatedDraws.addSimulation(simulation.drawnPlayerCards)
                        infectionCardSimulatedDraws.addSimulation(simulation.drawnInfectionCards)
                        if (playerCardSimulatedDraws.totalSimulations % FULL_REFRESH_FREQUENCY == 0L) {
                            send()
                        }
                    }
                    send()
                }
                for (result in results) {
                    simulationState = result
                }
            }
        }
    }
    val simulation = simulationState
    Column {
        Text(
            "Current turn is: ${game.turn.currentPlayer}",
            Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            "Turns left (this one included): ${game.turnsLeft()}\n" +
                "Player cards drawn: ${game.turn.drawnPlayerCards}\n" +
                "Infection cards drawn: ${game.turn.drawnInfectionCards}",
            Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(Modifier.padding(8.dp).padding(top = 8.dp)) {
            Text(
                "Player draw simulation",
                Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Infection draw simulation",
                Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(Modifier.weight(1f).padding(8.dp)) {
            when (simulation) {
                SimulationState.DuringEpidemic -> Text(
                    "Please resolve Epidemic",
                    style = MaterialTheme.typography.titleLarge
                )

                SimulationState.Simulating -> Text(
                    "Running a simulation...",
                    style = MaterialTheme.typography.titleLarge
                )

                is SimulationState.Simulated -> {
                    ProbabilityTreesComposable(Modifier.weight(1f), simulation.playerCardSimulation)
                    ProbabilityTreesComposable(Modifier.weight(1f), simulation.infectionCardSimulation)
                }
            }
        }
    }
}
