package dev.mmauro.malverio

import kotlinx.serialization.Serializable

const val DRAWN_PLAYER_CARDS_PER_TURN = 2

@Serializable
data class Game(
    val forsakenCities: Set<City>,
    val playerDeck: Deck<PlayerCard>,
    val infectionDeck: Deck<InfectionCard>,
    val infectionMarker: InfectionMarker,
    val turn: Turn,
) {

    constructor(
        forsakenCities: Set<City>,
        players: List<Player>,
        playerDeck: Deck<PlayerCard>,
        infectionDeck: Deck<InfectionCard>,
    ) : this(
        forsakenCities = forsakenCities,
        playerDeck = playerDeck,
        infectionDeck = infectionDeck,
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

    fun drawInfectionCard(card: InfectionCard): Game {
        requireNotInEpidemic()
        val game = copy(
            infectionDeck = infectionDeck.drawCardFromTop(card),
            turn = turn.copy(
                drawnInfectionCards = turn.drawnInfectionCards + 1,
            ),
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

    fun ensureHasInfectionCardsToDraw() : Game {
        return if (infectionDeck.undrawn.isEmpty()) {
            shuffleInfectionDeck()
        } else {
            this
        }
    }

    fun resolveEpidemicRandomly(): Pair<Game, InfectionCard> {
        val withInfections = ensureHasInfectionCardsToDraw()
        val infectionCard = withInfections.infectionDeck.randomCardFromBottom()
        val infectedGame = withInfections.increase().infect(infectionCard).intensify()
        return infectedGame.ensureHasInfectionCardsToDraw() to infectionCard
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