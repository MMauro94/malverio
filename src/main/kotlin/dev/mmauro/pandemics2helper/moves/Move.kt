package dev.mmauro.pandemics2helper.moves

import dev.mmauro.pandemics2helper.Timeline

interface Move {

    val name: String

    fun isAllowed(timeline: Timeline): Boolean

    fun perform(timeline: Timeline): Timeline
}

