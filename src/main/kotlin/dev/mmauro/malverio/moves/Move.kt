package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline

interface Move {

    val name: String

    fun isAllowed(timeline: Timeline): Boolean

    fun perform(timeline: Timeline): Timeline
}

