package dev.mmauro.malverio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.simulation.GameSimulatron1000
import dev.mmauro.malverio.simulation.SimulationResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

private const val SIMULATION_COUNT = 10_000

private sealed interface SimulationState {
    data object Simulating : SimulationState
    data object DuringEpidemic : SimulationState
    data class Simulated(
        val playerCardSimulation: SimulationResults<PlayerCard>,
        val infectionCardSimulation: SimulationResults<InfectionCard>,
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
            val simulations = List(SIMULATION_COUNT) {
                async(Dispatchers.Default) {
                    GameSimulatron1000.simulateRandomMove(game)
                }
            }.awaitAll()
            simulationState = SimulationState.Simulated(
                playerCardSimulation = SimulationResults(simulations.map { it.drawnPlayerCards }),
                infectionCardSimulation = SimulationResults(simulations.map { it.drawnInfectionCards }),
            )
        }
    }
    Column(Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Current turn is ${game.turn.currentPlayer}",
                Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.weight(1f))
            InfectionDeckComposable(Modifier, game.infectionDeck)
        }
        when (val simulation = simulationState) {
            SimulationState.DuringEpidemic -> {
                Text(
                    "Please resolve Epidemic",
                    Modifier.weight(1f).align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            SimulationState.Simulating -> {
                Text(
                    "Running a simulation...",
                    Modifier.weight(1f).align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            is SimulationState.Simulated -> {
                Row(Modifier.weight(1f)) {
                    PlayerCardsSimulationComposable(Modifier.weight(1f), simulation.playerCardSimulation)
                    InfectionCardsSimulationComposable(Modifier.weight(1f), simulation.infectionCardSimulation)
                }
            }
        }
        PlayerDeckComposable(Modifier.align(Alignment.End), game.playerDeck)
    }
}
