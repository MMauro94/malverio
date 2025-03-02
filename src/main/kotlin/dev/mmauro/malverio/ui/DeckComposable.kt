package dev.mmauro.malverio.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.mmauro.malverio.Card
import dev.mmauro.malverio.Deck
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard

@Composable
fun PlayerDeckComposable(
    modifier: Modifier = Modifier,
    deck: Deck<PlayerCard>,
) {
    DeckComposable(
        modifier = modifier,
        deck = deck,
        backColor = Color(0.2f, 0.2f, 0.8f),
        backLetter = 'P',
    )
}

@Composable
fun InfectionDeckComposable(
    modifier: Modifier = Modifier,
    deck: Deck<InfectionCard>,
) {
    DeckComposable(
        modifier = modifier,
        deck = deck,
        backColor = Color(0.8f, 0.2f, 0.2f),
        backLetter = 'I',
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <C : Card> DeckComposable(
    modifier: Modifier = Modifier,
    deck: Deck<C>,
    backColor: Color,
    backLetter: Char,
) {
    Surface(
        modifier.aspectRatio(5f / 7f),
        shape = MaterialTheme.shapes.large,
        color = backColor,
    ) {
        TooltipArea(
            tooltip = {
                DeckComposition(deck)
            },
            delayMillis = 0,
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(backLetter.toString(), color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun <C : Card> DeckComposition(deck: Deck<C>) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(Modifier.padding(8.dp)) {
            Text("${deck.size} cards")
        }
    }
}
