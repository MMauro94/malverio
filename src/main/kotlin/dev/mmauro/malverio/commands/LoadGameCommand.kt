package dev.mmauro.malverio.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import dev.mmauro.malverio.GameLoop
import dev.mmauro.malverio.Timeline

class LoadGameCommand : CliktCommand(name = "load") {

    private val savegame by option()
        .path(canBeDir = false, canBeSymlink = false, mustExist = true, mustBeReadable = true)
        .required()

    override fun run() {
        val timeline = Timeline.load(savegame)
        GameLoop(
            startTimeline = timeline,
            savegame = savegame,
        ).run()
    }
}