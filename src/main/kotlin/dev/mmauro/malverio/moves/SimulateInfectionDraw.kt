package dev.mmauro.malverio.moves

import com.github.ajalt.mordant.terminal.Terminal
import dev.mmauro.malverio.Game
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Textable
import dev.mmauro.malverio.Timeline
import dev.mmauro.malverio.simulation.Group
import dev.mmauro.malverio.simulation.SimulationResults
import dev.mmauro.malverio.simulation.print

object SimulateInfectionDraw : AbstractSimulateDrawMove<InfectionCard>() {

    override fun isAllowed(timeline: Timeline) = !timeline.currentGame.isDuringEpidemic()

    override val cardTypeName = "infection"

    override fun getNumberOfCardsToDraw(timeline: Timeline): Int {
        return timeline.currentGame.infectionMarker.cards
    }

    override fun Game.doRandomDraw(): RandomDrawResult<InfectionCard> {
        var game = this
        val drawnCards = buildSet {
            do {
                if (game.infectionDeck.undrawn.isEmpty()) {
                    game = game.shuffleInfectionDeck()
                }
                val card = game.infectionDeck.randomCardFromTop()
                add(card)
                game = game.drawInfectionCard(card)
            } while (card.shouldIgnore(game))
        }

        return RandomDrawResult(
            drawnCards = drawnCards,
            game = game,
        )
    }

    private fun InfectionCard.shouldIgnore(game: Game): Boolean {
        return this is InfectionCard.HollowMenGather || cityOrNull() in game.forsakenCities
    }

    override fun Terminal.printSimulationResults(results: SimulationResults<InfectionCard>) {
        println("Probability of infection cards:")
        results.probabilityTree(
            { Group(it.toType(), isRelevant = true) },
            {
                when (it) {
                    is InfectionCard.CityCard -> Group(it.city, isRelevant = true)
                    is InfectionCard.HollowMenGather -> Group(it, isRelevant = false)
                }
            },
            {
                when (it) {
                    is InfectionCard.CityCard -> Group(it, isRelevant = it.mutations.isNotEmpty())
                    is InfectionCard.HollowMenGather -> null
                }
            },
        ).print(terminal = this)
    }

    private sealed interface CardType : Textable {
        data object City : CardType {
            override fun text() = "City 🏙️"
        }

        data object HollowMenGather : CardType {
            override fun text() = "Hollow men gather 🧟"
        }
    }

    private fun InfectionCard.toType() = when (this) {
        is InfectionCard.CityCard -> CardType.City
        is InfectionCard.HollowMenGather -> CardType.HollowMenGather
    }
}