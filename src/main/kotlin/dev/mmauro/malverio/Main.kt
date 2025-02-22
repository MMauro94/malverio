package dev.mmauro.malverio

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import dev.mmauro.malverio.commands.LoadGameCommand
import dev.mmauro.malverio.commands.NewGameCommand
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
val JSON = Json {
    prettyPrint = true
    namingStrategy = JsonNamingStrategy.KebabCase
}

class Main : CliktCommand() {

    init {
        subcommands(NewGameCommand())
        subcommands(LoadGameCommand())
    }

    override fun run() = Unit
}

fun main(args: Array<String>) = Main().main(args)
