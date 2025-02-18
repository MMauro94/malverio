package dev.mmauro.malverio

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val JSON = Json {
    prettyPrint = true
}

@Serializable
class Timeline(val games: List<Item>) {

    constructor(infectionDeck: Deck<InfectionCard>) : this(
        listOf(
            Item(Game(infectionDeck), "Game setup (${infectionDeck.size} infection cards)")
        )
    )

    val currentGame get() = games.last().game

    init {
        require(games.isNotEmpty()) { "must have at least a game" }
    }

    operator fun plus(item: Item) = Timeline(games + item)

    @Serializable
    data class Item(val game: Game, val description: String)

    fun save(path: Path) {
        path.writeText(JSON.encodeToString(this))
    }

    companion object {
        fun load(path: Path): Timeline {
            return JSON.decodeFromString<Timeline>(path.readText())
        }
    }
}