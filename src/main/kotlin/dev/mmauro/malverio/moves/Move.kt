package dev.mmauro.malverio.moves

import dev.mmauro.malverio.Timeline

interface Move {

    fun name(timeline: Timeline): String

    fun isAllowed(timeline: Timeline): Boolean

    fun perform(timeline: Timeline): Timeline
}

