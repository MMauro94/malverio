package dev.mmauro.malverio

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val infectionDeck: Deck<InfectionCard>,
    val isDuringEpidemic: Boolean,
) {

    constructor(infectionDeck: Deck<InfectionCard>) : this(
        infectionDeck = infectionDeck,
        isDuringEpidemic = false,
    )

    fun drawInfectionCard(card: InfectionCard): Game {
        requireNotInEpidemic()
        return copy(infectionDeck = infectionDeck.drawCardFromTop(card))
    }

    fun infect(card: InfectionCard): Game {
        requireNotInEpidemic()
        return copy(infectionDeck = infectionDeck.drawCardFromBottom(card), isDuringEpidemic = true)
    }

    fun intensify(): Game {
        require(isDuringEpidemic) { "Intensify can only be performed during epidemics" }
        return copy(infectionDeck = infectionDeck.shuffleDrawnAndPlaceOnTop(), isDuringEpidemic = false)
    }

    fun removeCard(card: InfectionCard): Game {
        return copy(infectionDeck = infectionDeck.removeCardFromDrawn(card))
    }

    fun moveToTopOfDeck(card: InfectionCard): Game {
        requireNotInEpidemic()
        return copy(infectionDeck = infectionDeck.moveFromDrawnToTopOfDeck(card))
    }
}

private fun Game.requireNotInEpidemic() {
    require(!isDuringEpidemic) { "Cannot perform this move during epidemic" }
}