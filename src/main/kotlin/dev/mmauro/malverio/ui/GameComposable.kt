package dev.mmauro.malverio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mmauro.malverio.Game

@Composable
fun GameComposable(game: Game) {
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
        Spacer(Modifier.weight(1f))
        PlayerDeckComposable(Modifier.align(Alignment.End), game.playerDeck)
    }
}

