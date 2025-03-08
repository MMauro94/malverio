package dev.mmauro.malverio.ui

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.simulation.GameSimulatron1000
import dev.mmauro.malverio.simulation.ProbabilityTree
import dev.mmauro.malverio.simulation.SimulationResults
import dev.mmauro.malverio.simulation.ZombieOrInfect
import dev.mmauro.malverio.simulation.infectionProbabilityTree
import dev.mmauro.malverio.simulation.playerProbabilityTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val SIMULATION_COUNT = 50_000

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
                        repeat(SIMULATION_COUNT / parallel) {
                            simulations.send(GameSimulatron1000.simulateRandomMove(game))
                        }
                    }
                }
                val partialResults = Channel<Pair<
                    List<Set<PlayerCard>>, List<Set<ZombieOrInfect>>
                    >>(capacity = Channel.CONFLATED)
                launch(Dispatchers.Default) {
                    val playerCardSimulatedDraws = mutableListOf<Set<PlayerCard>>()
                    val infectionCardSimulatedDraws = mutableListOf<Set<ZombieOrInfect>>()
                    suspend fun send() {
                        partialResults.send(
                            playerCardSimulatedDraws.toList() to infectionCardSimulatedDraws.toList()
                        )
                    }
                    for (simulation in simulations) {
                        playerCardSimulatedDraws.add(simulation.drawnPlayerCards)
                        infectionCardSimulatedDraws.add(simulation.drawnInfectionCards)
                        if (playerCardSimulatedDraws.size % 1000 == 0) {
                            send()
                        }
                    }
                    send()
                }
                val results = Channel<SimulationState.Simulated>(capacity = Channel.CONFLATED)
                launch(Dispatchers.Default) {
                    for ((player, infection) in partialResults) {
                        results.send(
                            SimulationState.Simulated(
                                playerCardSimulation = SimulationResults(player).playerProbabilityTree(),
                                infectionCardSimulation = SimulationResults(infection).infectionProbabilityTree(),
                            )
                        )
                    }
                }
                for (result in results) {
                    simulationState = result
                }
            }
        }
    }
    val simulation = simulationState
    Row(Modifier.padding(8.dp)) {
        Column(Modifier.weight(1f)) {
            Text(
                "Current turn is ${game.turn.currentPlayer}",
                Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge,
            )
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                when (simulation) {
                    SimulationState.DuringEpidemic -> Text(
                        "Please resolve Epidemic",
                        style = MaterialTheme.typography.titleLarge
                    )

                    SimulationState.Simulating -> Text(
                        "Running a simulation...",
                        style = MaterialTheme.typography.titleLarge
                    )

                    is SimulationState.Simulated -> ProbabilityTreesComposable(simulation.playerCardSimulation)
                }
            }
        }
        Column(Modifier.weight(1f)) {
            PlayerDeckComposable(Modifier, game.playerDeck)
            Box(Modifier.weight(1f)) {
                if (simulation is SimulationState.Simulated) {
                    ProbabilityTreesComposable(simulation.infectionCardSimulation)
                }
            }
            InfectionDeckComposable(Modifier, game.infectionDeck)
        }
    }
}
