package dev.mmauro.pandemics2helper.events

import dev.mmauro.pandemics2helper.InfectionCard

data class EpidemicEvent(val card: InfectionCard) : Event