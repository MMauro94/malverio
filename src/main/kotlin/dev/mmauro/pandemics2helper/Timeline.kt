package dev.mmauro.pandemics2helper

class Timeline(val games: List<Item>) {
    constructor(game: Game) : this(listOf(Item(game, "Game setup")))

    val currentGame get() = games.last().game

    init {
        require(games.isNotEmpty()) { "must have at least a game" }
    }

    operator fun plus(item: Item) = Timeline(games + item)

    data class Item(val game: Game, val description: String)
}