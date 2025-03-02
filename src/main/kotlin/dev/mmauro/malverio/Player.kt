package dev.mmauro.malverio

import kotlinx.serialization.Serializable

@Serializable
data class Player(val name: String) : Textable, Comparable<Player> {
    override fun toString() = name

    override fun text() = name

    override fun compareTo(other: Player) = name.compareTo(other.name)
}