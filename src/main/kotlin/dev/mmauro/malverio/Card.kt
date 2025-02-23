package dev.mmauro.malverio

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Card {

    @OptIn(ExperimentalUuidApi::class)
    val id: Uuid

    fun text(): String
}