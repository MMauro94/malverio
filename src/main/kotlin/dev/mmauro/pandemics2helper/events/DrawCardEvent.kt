package dev.mmauro.pandemics2helper.events

import dev.mmauro.pandemics2helper.InfectionCard

data class DrawCardEvent(val card: InfectionCard) : Event
