package dev.mmauro.malverio

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Turn @OptIn(ExperimentalSerializationApi::class) constructor(
    val players: List<Player>,
    val turnNumber: Int,
    val drawnPlayerCards: Int,
    @JsonNames("drawn-epidemic-cards")
    val drawnInfectionCards: Int,
    val epidemicStage: EpidemicStage?,
) {

    val currentPlayer get() = players[turnNumber % players.size]

    init {
        require(turnNumber >= 0)
        require(drawnPlayerCards in 0..DRAWN_PLAYER_CARDS_PER_TURN)
        require(drawnInfectionCards >= 0)
        if (epidemicStage != null) {
            require(drawnPlayerCards > 0) { "Cannot be in an epidemic stage if you haven't drawn any player card" }
            require(drawnInfectionCards == 0) { "Cannot be in an epidemic stage if you have started drawing infection cards" }
        }
    }

    fun hasDrawnAllPlayerCards() = drawnPlayerCards == DRAWN_PLAYER_CARDS_PER_TURN

    enum class EpidemicStage {
        INCREASE, INFECT, INTENSIFY
    }
}