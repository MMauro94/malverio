package dev.mmauro.malverio

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import dev.mmauro.malverio.commands.LoadGameCommand
import dev.mmauro.malverio.commands.NewGameCommand

class Main : CliktCommand() {

    init {
        subcommands(NewGameCommand())
        subcommands(LoadGameCommand())
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = Main().main(args)
