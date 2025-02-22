package dev.mmauro.malverio

import kotlinx.serialization.Serializable

const val DRAWN_PLAYER_CARDS_PER_TURN = 2

@Serializable
data class Game(
    val playerDeck: Deck<PlayerCard>,
    val infectionDeck: Deck<InfectionCard>,
    val turn: Turn,
) {

    constructor(players: List<String>, playerDeck: Deck<PlayerCard>, infectionDeck: Deck<InfectionCard>) : this(
        playerDeck = playerDeck,
        infectionDeck = infectionDeck,
        turn = Turn(
            players = players,
            turnNumber = 0,
            drawnPlayerCards = 0,
            drawnEpidemicCards = 0,
            epidemicStage = null,
        )
    )

    fun drawPlayerCard(card: PlayerCard): Game {
        requireNotInEpidemic()
        require(turn.drawnPlayerCards < DRAWN_PLAYER_CARDS_PER_TURN) {
            "you have already drawn ${turn.drawnPlayerCards} player cards"
        }
        return copy(
            playerDeck = playerDeck.drawCardFromTop(card),
            turn = turn.copy(
                drawnPlayerCards = turn.drawnPlayerCards + 1,
                epidemicStage = when (card) {
                    is PlayerCard.EpidemicCard -> Turn.EpidemicStage.INFECT
                    else -> null
                },
            )
        )
    }

    fun drawInfectionCard(card: InfectionCard): Game {
        requireNotInEpidemic()
        return copy(
            infectionDeck = infectionDeck.drawCardFromTop(card),
            turn = turn.copy(
                drawnEpidemicCards = turn.drawnEpidemicCards + 1,
            )
        )
    }

    fun infect(card: InfectionCard): Game {
        require(turn.epidemicStage == Turn.EpidemicStage.INFECT) {
            "Infect cannot be performed before drawing an epidemic card"
        }
        return copy(
            infectionDeck = infectionDeck.drawCardFromBottom(card),
            turn = turn.copy(epidemicStage = Turn.EpidemicStage.INTENSIFY),
        )
    }

    fun intensify(): Game {
        require(turn.epidemicStage == Turn.EpidemicStage.INTENSIFY) {
            "Intensify can only be performed after infect step"
        }
        return copy(
            infectionDeck = infectionDeck.shuffleDrawnAndPlaceOnTop(),
            turn = turn.copy(epidemicStage = null),
        )
    }

    fun removeCard(card: InfectionCard): Game {
        return copy(infectionDeck = infectionDeck.removeCardFromDrawn(card))
    }

    fun moveToTopOfDeck(card: InfectionCard): Game {
        requireNotInEpidemic()
        return copy(infectionDeck = infectionDeck.moveFromDrawnToTopOfDeck(card))
    }

    fun nextTurn(): Game {
        requireNotInEpidemic()
        require(turn.drawnPlayerCards == DRAWN_PLAYER_CARDS_PER_TURN) {
            "cannot go to next turn until you've drawn player cards"
        }
        return copy(
            turn = turn.copy(
                turnNumber = turn.turnNumber + 1,
                drawnPlayerCards = 0,
                drawnEpidemicCards = 0,
            )
        )
    }

    fun turnsLeft() : Int {
        return (playerDeck.undrawn.size + turn.drawnPlayerCards) / DRAWN_PLAYER_CARDS_PER_TURN
    }

    fun isDuringEpidemic() = turn.epidemicStage != null
}

private fun Game.requireNotInEpidemic() {
    require(!isDuringEpidemic()) { "Cannot perform this move during epidemic" }
}