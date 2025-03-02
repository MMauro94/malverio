package dev.mmauro.malverio

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface Card : Textable {

    @OptIn(ExperimentalUuidApi::class)
    val id: Uuid
}