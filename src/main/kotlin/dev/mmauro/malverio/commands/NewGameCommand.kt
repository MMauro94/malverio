package dev.mmauro.malverio.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import dev.mmauro.malverio.Deck
import dev.mmauro.malverio.GameLoop
import dev.mmauro.malverio.InfectionCard
import dev.mmauro.malverio.Timeline
import kotlinx.serialization.json.Json
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText

private typealias InfectionDeck = Set<InfectionCard>

class NewGameCommand : CliktCommand(name = "new") {

    private val infectionDeck by option().path(mustExist = true)
        .convert { Json.decodeFromString<InfectionDeck>(it.readText()) }
        .required()
    private val savegame by option().path(canBeDir = false, canBeSymlink = false).required()

    override fun run() {
        savegame.createParentDirectories()
        GameLoop(
            startTimeline = Timeline(infectionDeck = Deck(infectionDeck)),
            savegame = savegame,
        ).run()
    }
}