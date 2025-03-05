package dev.mmauro.malverio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mmauro.malverio.Card

val CARD_BORDER_RADIUS = 16.dp

@Composable
fun FaceUpCard(
    modifier: Modifier,
    card: Card,
) {
    CardContainer(
        modifier = modifier,
        backgroundColor = card.color(),
        border = BorderStroke(4.dp, Color.White),
    ) {
        Text(
            card.plainText(),
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
        )
    }
}

enum class CardBack(
    val color: Color,
    val letter: Char,
) {
    PLAYER(Color(0.1f, 0.3f, 0.1f), 'P'),
    INFECTION(Color(0.3f, 0.1f, 0.1f), 'I'),
}

@Composable
fun FaceDownCard(
    modifier: Modifier,
    cardBack: CardBack,
) {
    CardContainer(
        modifier = modifier,
        backgroundColor = cardBack.color,
        border = BorderStroke(8.dp, Color.Black),
    ) {
        Text(cardBack.letter.toString(), color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun CardContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    border: BorderStroke,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier.aspectRatio(5f / 7f),
        color = backgroundColor,
        shape = RoundedCornerShape(CARD_BORDER_RADIUS),
        border = border,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}