package dev.mmauro.malverio.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import dev.mmauro.malverio.City
import dev.mmauro.malverio.GameLoop
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Timeline

private val INFECTION_DECK = setOf(
    InfectionCard(City.ATLANTA),
    InfectionCard(City.BUENO_AIRES),
    InfectionCard(City.BUENO_AIRES),
    InfectionCard(City.CAIRO),
    InfectionCard(City.CAIRO),
    InfectionCard(City.CAIRO),
    InfectionCard(City.FRANKFURT),
    InfectionCard(City.FRANKFURT),
    InfectionCard(City.ISTANBUL),
    InfectionCard(City.ISTANBUL),
    InfectionCard(City.ISTANBUL),
    InfectionCard(City.JACKSONVILLE),
    InfectionCard(City.JACKSONVILLE),
    InfectionCard(City.JACKSONVILLE),
    InfectionCard(City.LAGOS),
    InfectionCard(City.LAGOS),
    InfectionCard(City.LAGOS),
    InfectionCard(City.LIMA),
    InfectionCard(City.LONDON),
    InfectionCard(City.LONDON),
    InfectionCard(City.LONDON),
    InfectionCard(City.LOS_ANGELES),
    InfectionCard(City.NEW_YORK),
    InfectionCard(City.NEW_YORK),
    InfectionCard(City.NEW_YORK),
    InfectionCard(City.PARIS),
    InfectionCard(City.PARIS),
    InfectionCard(City.SANTIAGO),
    InfectionCard(City.SAO_PAULO),
    InfectionCard(City.SAO_PAULO),
    InfectionCard(City.SAO_PAULO),
    InfectionCard(City.TRIPOLI),
    InfectionCard(City.TRIPOLI),
    InfectionCard(City.TRIPOLI),
    InfectionCard(City.WASHINGTON),
    InfectionCard(City.WASHINGTON),
    InfectionCard(City.WASHINGTON, setOf(InfectionCard.Mutation.WELL_STOCKED)),
)

class NewGameCommand : CliktCommand(name = "new") {

    private val savegame by option().path(canBeDir = false, canBeSymlink = false).required()

    override fun run() {
        GameLoop(
            startTimeline = Timeline(infectionDeck = INFECTION_DECK),
            savegame = savegame,
        ).run()
    }
}