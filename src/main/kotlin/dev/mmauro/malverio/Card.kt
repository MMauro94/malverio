package dev.mmauro.malverio

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Card : Textable {

    @OptIn(ExperimentalUuidApi::class)
    val id: Uuid
}