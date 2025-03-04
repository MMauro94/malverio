package dev.mmauro.malverio.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.mmauro.malverio.Card
import dev.mmauro.malverio.Deck
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard

private val CARD_HEIGHT = 200.dp
private val MAX_CARDS = 100
private val CARDS_OFFSET = 0.5.dp

@Composable
fun PlayerDeckComposable(
    modifier: Modifier = Modifier,
    deck: Deck<PlayerCard>,
) {
    DeckComposable(
        modifier = modifier,
        deck = deck,
        cardBack = CardBack.PLAYER,
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
        cardBack = CardBack.INFECTION,
    )
}

@Composable
private fun <C : Card> DeckComposable(
    modifier: Modifier = Modifier,
    deck: Deck<C>,
    cardBack: CardBack,
) {
    Row(modifier = modifier.padding(8.dp)) {
        FaceUpCardsComposable(deck.removed, "Removed")
        Spacer(Modifier.width(16.dp))
        FaceDownCardsComposable(deck.undrawn.toList(), "Deck", cardBack)
        Spacer(Modifier.width(16.dp))
        FaceUpCardsComposable(deck.drawn, "Discards")
    }
}

@Composable
private fun <C : Card> FaceUpCardsComposable(
    deck: List<C>,
    name: String,
) {
    PileOfCardsComposable(
        deck = deck,
        baseContent = {
            Text(name, color = Color.White, style = MaterialTheme.typography.titleMedium)
        },
        cardContent = { m, card ->
            FaceUpCard(m, card)
        }
    )
}

@Composable
private fun <C : Card> FaceDownCardsComposable(
    deck: List<C>,
    name: String,
    cardBack: CardBack,
) {
    PileOfCardsComposable(
        deck = deck,
        baseContent = {
            Text(name, color = Color.White, style = MaterialTheme.typography.titleMedium)
        },
        cardContent = { m, _ ->
            FaceDownCard(m, cardBack)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <C : Card> PileOfCardsComposable(
    deck: List<C>,
    baseContent: @Composable () -> Unit,
    cardContent: @Composable (Modifier, Card) -> Unit,
) {
    TooltipArea(
        tooltip = {
            DeckComposition(deck)
        },
        delayMillis = 0,
    ) {
        Box {
            val baseOffset = MAX_CARDS * CARDS_OFFSET
            val offset = minOf(0.5.dp, baseOffset / deck.size)
            Column(Modifier.padding(top = baseOffset)) {
                PileOfCardsBase(
                    modifier = Modifier.height(CARD_HEIGHT),
                ) {
                    baseContent()
                }
            }
            for ((index, card) in deck.withIndex()) {
                Column(Modifier.padding(top = baseOffset - index * offset)) {
                    cardContent(
                        Modifier.height(CARD_HEIGHT),
                        card,
                    )
                }
            }
        }
    }
}

@Composable
private fun <C : Card> DeckComposition(deck: List<C>) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(Modifier.padding(8.dp)) {
            Text("${deck.size} cards")
        }
    }
}


@Composable
private fun PileOfCardsBase(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val dp1 = LocalDensity.current.run { 1.dp.toPx() }
    val stroke = Stroke(
        width = 4 * dp1,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * dp1, 10 * dp1), 0f)
    )
    val bg = MaterialTheme.colorScheme.onBackground
    Box(
        modifier = modifier
            .aspectRatio(5f / 7f)
            .drawBehind {
                drawRoundRect(
                    color = bg,
                    style = stroke,
                    cornerRadius = CornerRadius(CARD_BORDER_RADIUS.toPx())
                )
            }
            .clip(RoundedCornerShape(CARD_BORDER_RADIUS)),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}
