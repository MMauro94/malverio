package dev.mmauro.malverio

import androidx.compose.ui.graphics.Color
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface Card : Textable {

    @OptIn(ExperimentalUuidApi::class)
    val id: Uuid

    override fun color(): Color
}