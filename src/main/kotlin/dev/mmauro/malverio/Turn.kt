package dev.mmauro.malverio

import kotlinx.serialization.Serializable

@Serializable
data class Turn(
    val players: List<Player>,
    val turnNumber: Int,
    val drawnPlayerCards: Int,
    val drawnEpidemicCards: Int,
    val epidemicStage: EpidemicStage?,
) {

    val currentPlayer get() = players[turnNumber % players.size]

    init {
        require(turnNumber >= 0)
        require(drawnPlayerCards in 0..DRAWN_PLAYER_CARDS_PER_TURN)
        require(drawnEpidemicCards >= 0)
        if (epidemicStage != null) {
            require(drawnPlayerCards > 0) { "Cannot be in an epidemic stage if you haven't drawn any player card" }
            require(drawnEpidemicCards == 0) { "Cannot be in an epidemic stage if you have started drawing epidemic cards" }
        }
    }

    fun hasDrawnAllPlayerCards() = drawnPlayerCards == DRAWN_PLAYER_CARDS_PER_TURN

    enum class EpidemicStage {
        INCREASE, INFECT, INTENSIFY
    }
}