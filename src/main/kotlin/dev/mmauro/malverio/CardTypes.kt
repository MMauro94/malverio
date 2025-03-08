package dev.mmauro.malverio

import dev.mmauro.malverio.PlayerCard.CityCard
import dev.mmauro.malverio.PlayerCard.EpidemicCard
import dev.mmauro.malverio.PlayerCard.EventCard
import dev.mmauro.malverio.PlayerCard.ProduceSuppliesCard


sealed interface PlayerCardType : Textable {
    data object Epidemic : PlayerCardType {
        override fun plainText() = "EPIDEMIC ☢️"
    }

    data object Produce : PlayerCardType {
        override fun plainText() = "Produce"
    }

    data object Event : PlayerCardType {
        override fun plainText() = "Event"
    }

    data class City(val color: CityColor) : PlayerCardType {
        override fun text() = color.text()
        override fun plainText() = color.plainText()
    }

    data object PortableAntiviralLab : PlayerCardType {
        override fun plainText() = "Portable antiviral lab"
    }
}

fun PlayerCard.toType() = when (this) {
    is CityCard -> PlayerCardType.City(city.color)
    is EpidemicCard -> PlayerCardType.Epidemic
    is EventCard.RationedEventCard -> PlayerCardType.Event
    is EventCard.UnrationedEventCard -> PlayerCardType.Event
    is ProduceSuppliesCard -> PlayerCardType.Produce
    is PlayerCard.PortableAntiviralLabCard -> PlayerCardType.PortableAntiviralLab
}
