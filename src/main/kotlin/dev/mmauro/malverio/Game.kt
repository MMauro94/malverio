package dev.mmauro.malverio

import kotlinx.serialization.Serializable

const val DRAWN_PLAYER_CARDS_PER_TURN = 2

@Serializable
data class Game(
    val forsakenCities: Set<City>,
    val playerDeck: Deck<PlayerCard>,
    val infectionDeck: Deck<InfectionCard>,
    val notInGame: Set<InfectionCard.CityCard>,
    val infectionMarker: InfectionMarker,
    val turn: Turn,
) {

    constructor(
        forsakenCities: Set<City>,
        players: List<Player>,
        playerDeck: Deck<PlayerCard>,
        infectionDeck: Deck<InfectionCard>,
        notInGame: Set<InfectionCard.CityCard>,
    ) : this(
        forsakenCities = forsakenCities,
        playerDeck = playerDeck,
        infectionDeck = infectionDeck,
        notInGame = notInGame,
        infectionMarker = InfectionMarker(),
        turn = Turn(
            players = players,
            turnNumber = 0,
            drawnPlayerCards = 0,
            drawnInfectionCards = 0,
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
                    is PlayerCard.EpidemicCard -> Turn.EpidemicStage.INCREASE
                    else -> null
                },
            )
        )
    }

    fun monitorPlayerCard(card: PlayerCard): Game {
        requireNotInEpidemic()
        return copy(playerDeck = playerDeck.drawCardFromTop(card))
    }

    fun drawInfectionCard(card: InfectionCard): Game {
        requireNotInEpidemic()
        val doNotIncrement = card.cityOrNull() in forsakenCities || card is InfectionCard.HollowMenGather
        val game = copy(
            infectionDeck = infectionDeck.drawCardFromTop(card),
            turn = if (doNotIncrement) {
                turn
            } else {
                turn.copy(
                    drawnInfectionCards = turn.drawnInfectionCards + 1,
                )
            },
        )
        return when {
            card.cityOrNull() in forsakenCities -> game.removeCard(card)
            else -> game
        }
    }

    fun increase(): Game {
        require(turn.epidemicStage == Turn.EpidemicStage.INCREASE) {
            "Infect cannot be performed before drawing an epidemic card"
        }
        return copy(
            infectionMarker = infectionMarker.advance(),
            turn = turn.copy(epidemicStage = Turn.EpidemicStage.INFECT)
        )
    }

    fun infect(card: InfectionCard): Game {
        require(turn.epidemicStage == Turn.EpidemicStage.INFECT) {
            "Intensify can only be performed after increase step"
        }
        require(card in notInGame || card in infectionDeck.deck)

        var nextTurn = turn
        var id = infectionDeck
        var nig = notInGame

        if (card !is InfectionCard.HollowMenGather && card.cityOrNull() !in forsakenCities) {
            nextTurn = turn.copy(epidemicStage = Turn.EpidemicStage.INTENSIFY)
        }
        if (card in infectionDeck.deck) {
            id = infectionDeck.drawCardFromBottom(card)
        } else {
            id = id.copy(drawn = id.drawn + card)
            nig = notInGame.minus(card as InfectionCard.CityCard)
        }
        return copy(
            infectionDeck = id,
            notInGame = nig,
            turn = nextTurn,
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

    fun ensureHasInfectionCardsToDraw(): Game {
        return if (infectionDeck.undrawn.isEmpty()) {
            shuffleInfectionDeck()
        } else {
            this
        }
    }

    fun shuffleInfectionDeck(): Game {
        require(infectionDeck.undrawn.isEmpty()) {
            "Shuffle infection deck can only be performed when the infection deck is over"
        }
        return copy(
            infectionDeck = infectionDeck.shuffleDrawnAndPlaceOnTop(),
            infectionMarker = infectionMarker.advance(),
        )
    }

    fun removeCard(card: InfectionCard): Game {
        return copy(infectionDeck = infectionDeck.removeCard(card))
    }

    fun removeCardFromDrawn(card: InfectionCard): Game {
        return copy(infectionDeck = infectionDeck.removeCardFromDrawn(card))
    }

    fun removeInfectionCardFromPlay(card: InfectionCard.CityCard): Game {
        return copy(
            infectionDeck = infectionDeck.removeCardFromDrawn(card),
            notInGame = (notInGame + card).toSet(),
        )
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
                drawnInfectionCards = 0,
            )
        )
    }

    fun turnsLeft(): Int {
        return (playerDeck.undrawn.size + turn.drawnPlayerCards) / DRAWN_PLAYER_CARDS_PER_TURN
    }

    fun isDuringEpidemic() = turn.epidemicStage != null

    fun hasDrawnAllInfectionCards(): Boolean {
        return turn.drawnInfectionCards >= infectionMarker.cards
    }
}

private fun Game.requireNotInEpidemic() {
    require(!isDuringEpidemic()) { "Cannot perform this move during epidemic" }
}