package dev.mmauro.malverio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import dev.mmauro.malverio.ui.FilePicker
import dev.mmauro.malverio.ui.InfectionDeckComposable
import dev.mmauro.malverio.ui.OutputFilePicker
import dev.mmauro.malverio.ui.PlayerDeckComposable
import dev.mmauro.malverio.ui.RowWithButton

private sealed interface UiScreen {
    data object Main : UiScreen
    data object NewGame : UiScreen
}


fun main() = singleWindowApplication(state = WindowState(width = 1280.dp, height = 960.dp)) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize()) {
            var screen by remember { mutableStateOf<UiScreen>(UiScreen.Main) }
            when (screen) {
                UiScreen.Main -> MainScreen { screen = it }
                UiScreen.NewGame -> NewGameScreen()
            }
        }
    }
}

@Composable
private fun MainScreen(
    changeUiScreen: (UiScreen) -> Unit,
) {
    Column {
        Button(onClick = { changeUiScreen(UiScreen.NewGame) }) { Text("New game") }
        Button(onClick = { TODO() }) { Text("Load game") }
    }
}

@Composable
private fun NewGameScreen() {
    Column {
        val playerDeck = FilePicker("player deck") {
            val cards = JSON.decodeFromString<Set<PlayerCard>>(it.readText())
            Deck(cards)
        }
        val infectionDeck = FilePicker("infection deck") {
            val cards = JSON.decodeFromString<Set<InfectionCard>>(it.readText())
            Deck(cards)
        }
        val saveFile by OutputFilePicker("save file")
        val players by PlayersPicker()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (playerDeck != null) {
                PlayerDeckComposable(
                    Modifier.height(200.dp),
                    playerDeck,
                )
            }
            if (infectionDeck != null) {
                InfectionDeckComposable(
                    Modifier.height(200.dp),
                    infectionDeck,
                )
            }
        }

        val cs = rememberCoroutineScope()
        Button(
            onClick = {

            },
            enabled = playerDeck != null && infectionDeck != null && saveFile != null && players.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        ) {
            Text("Start")
        }
    }
}

@Composable
private fun PlayersPicker(): State<List<Player>> {
    val players = remember { mutableStateOf<List<Player>>(emptyList()) }

    Column(Modifier.padding(8.dp)) {
        Text("Pick players")
        for (player in players.value) {
            RowWithButton("Remove", onClick = {
                players.value = players.value.minus(player)
            }) {
                Text(player.name)
            }
        }
        if (players.value.size < 4) {
            var newPlayerName by remember { mutableStateOf("") }
            RowWithButton(
                "Add player",
                onClick = {
                    players.value = players.value.plus(Player(newPlayerName))
                    newPlayerName = ""
                },
                enabled = newPlayerName.isNotBlank() && players.value.none { it.name == newPlayerName },
            ) {
                TextField(
                    newPlayerName,
                    { newPlayerName = it },
                    placeholder = { Text("New player's name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            players.value = players.value.plus(Player(newPlayerName))
                            newPlayerName = ""
                        }
                    )
                )
            }
        }
    }

    return players
}

