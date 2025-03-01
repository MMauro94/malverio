package dev.mmauro.malverio

import kotlinx.serialization.Serializable

private val CARDS_NUMBERS = listOf(2, 2, 2, 3, 3, 4, 4, 5)

@Serializable
data class InfectionMarker(val index: Int = 0) {

    val cards get() = CARDS_NUMBERS[index]

    init {
        require(index in CARDS_NUMBERS.indices)
    }

    fun advance(): InfectionMarker {
        return when (index) {
            CARDS_NUMBERS.indices.last -> this
            else -> InfectionMarker(index + 1)
        }
    }
}