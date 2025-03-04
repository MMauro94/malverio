package dev.mmauro.malverio.simulation

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.EpidemicCard

object GameSimulatron1000 {

    private class SimulationBuilder(
        var game: Game,
        val drawnPlayerCards: MutableSet<PlayerCard> = HashSet(),
        val drawnInfectionCards: MutableSet<InfectionCard> = HashSet(),
    ) {
        fun addPlayerCard(game: Game, card: PlayerCard) {
            this.game = game
            drawnPlayerCards.add(card)
        }

        fun addInfectionCard(game: Game, card: InfectionCard) {
            this.game = game
            drawnInfectionCards.add(card)
        }
    }

    data class SimulationResult(
        val game: Game,
        val drawnPlayerCards: Set<PlayerCard>,
        val drawnInfectionCards: Set<InfectionCard>,
    )

    fun simulateRandomMove(game: Game): SimulationResult {
        val simulation = SimulationBuilder(game)
        do {
            val isDone = simulateRandomMove(simulation)
        } while (!isDone)
        return SimulationResult(
            simulation.game,
            simulation.drawnPlayerCards,
            simulation.drawnInfectionCards,
        )
    }

    private fun simulateRandomMove(
        simulation: SimulationBuilder,
    ): Boolean {
        return when {
            !simulation.game.turn.hasDrawnAllPlayerCards() -> simulatePlayerDraw(simulation)
            !simulation.game.hasDrawnAllInfectionCards() -> simulateInfectionDraw(simulation)
            else -> false
        }
    }

    private fun simulatePlayerDraw(simulation: SimulationBuilder): Boolean {
        if (simulation.game.playerDeck.undrawn.isEmpty()) return false

        val card = simulation.game.playerDeck.randomCardFromTop()
        val g1 = simulation.game.drawPlayerCard(card)
        simulation.addPlayerCard(g1, card)

        if (card is EpidemicCard) {
            val (g2, infectionCard) = g1.resolveEpidemicRandomly()
            simulation.addInfectionCard(g2, infectionCard)
        }
        return true
    }

    private fun simulateInfectionDraw(simulation: SimulationBuilder): Boolean {
        do {
            simulation.game = simulation.game.ensureHasInfectionCardsToDraw()
            val card = simulation.game.infectionDeck.randomCardFromTop()
            simulation.addInfectionCard(simulation.game.drawInfectionCard(card), card)
            simulation.game = simulation.game.ensureHasInfectionCardsToDraw()
        } while (card.shouldIgnore(simulation.game))
        return true
    }

    private fun InfectionCard.shouldIgnore(game: Game): Boolean {
        return this is InfectionCard.HollowMenGather || cityOrNull() in game.forsakenCities
    }
}