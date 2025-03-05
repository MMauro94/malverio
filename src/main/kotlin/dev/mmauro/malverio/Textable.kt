package dev.mmauro.malverio

import androidx.compose.ui.graphics.Color

interface Textable {
    fun text(): String = plainText()
    fun color(): Color? = null
    fun plainText(): String
}