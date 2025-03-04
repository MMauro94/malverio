package dev.mmauro.malverio

interface Textable {
    fun text(): String = plainText()
    fun plainText(): String
}