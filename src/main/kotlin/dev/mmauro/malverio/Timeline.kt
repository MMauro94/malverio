package dev.mmauro.malverio

import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
class Timeline(val games: List<Item>) {

    constructor(game: Game) : this(
        listOf(
            Item(
                game = game,
                description = "Game setup (${game.playerDeck.size} player cards, ${game.infectionDeck.size} infection cards)"
            )
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