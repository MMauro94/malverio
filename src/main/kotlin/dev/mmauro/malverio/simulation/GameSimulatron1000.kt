package dev.mmauro.malverio.simulation

import dev.mmauro.malverio.Game
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.PlayerCard
import dev.mmauro.malverio.PlayerCard.EpidemicCard

object GameSimulatron1000 {

    private class SimulationBuilder(
        var game: Game,
        val drawnPlayerCards: MutableSet<PlayerCard> = HashSet(),
        val drawnInfectionCards: MutableSet<ZombieOrInfect> = HashSet(),

        ) {
        fun addPlayerCard(game: Game, card: PlayerCard) {
            this.game = game
            drawnPlayerCards.add(card)
        }

        fun infectCity(card: InfectionCard.CityCard) {
            drawnInfectionCards.add(ZombieOrInfect.Infect(card))
        }

        fun zombieCity(card: InfectionCard.CityCard) {
            drawnInfectionCards.add(ZombieOrInfect.Zombie(card))
        }
    }

    data class SimulationResult(
        val game: Game,
        val drawnPlayerCards: Set<PlayerCard>,
        val drawnInfectionCards: Set<ZombieOrInfect>,
    )

    fun simulateRandomMove(game: Game): SimulationResult {
        val simulation = SimulationBuilder(game)
        do {
            val hasMoreMoves = simulateRandomMove(simulation)
        } while (hasMoreMoves)
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
            simulation.game = g1.increase()
            infect(simulation) {
                if (it.game.notInGame.isNotEmpty()) {
                    // TODO: supprt early games
                    val c = it.game.notInGame.random() // it.game.infectionDeck.randomCardFromBottom()
                    it.game = it.game.infect(c)
                    c
                } else {
                    null
                }
            }
            simulation.game = simulation.game.intensify()
        }
        return true
    }

    private fun simulateInfectionDraw(simulation: SimulationBuilder): Boolean {
        infect(simulation) {
            val c = it.game.infectionDeck.randomCardFromTop()
            it.game = it.game.drawInfectionCard(c)
            c
        }
        return true
    }

    private fun infect(simulation: SimulationBuilder, extract: (SimulationBuilder) -> InfectionCard?) {
        var isInfecting = true
        while (true) {
            simulation.game = simulation.game.ensureHasInfectionCardsToDraw()
            val card = extract(simulation)
            simulation.game = simulation.game.ensureHasInfectionCardsToDraw()
            when (card) {
                is InfectionCard.HollowMenGather -> {
                    isInfecting = false
                }

                is InfectionCard.CityCard -> {
                    if (card.city in simulation.game.forsakenCities) {
                        simulation.game.removeCard(card)
                    } else if (isInfecting) {
                        simulation.infectCity(card)
                        break
                    } else {
                        simulation.zombieCity(card)
                        break
                    }
                }

                null -> break
            }
        }
    }
}