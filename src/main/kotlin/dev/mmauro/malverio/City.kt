package dev.mmauro.malverio

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle
import kotlinx.serialization.Serializable

@Serializable
enum class City(val color: CityColor) {
    SAN_FRANCISCO(CityColor.BLUE),
    DENVER(CityColor.BLUE),
    CHICAGO(CityColor.BLUE),
    ATLANTA(CityColor.BLUE),
    NEW_YORK(CityColor.BLUE),
    WASHINGTON(CityColor.BLUE),
    LONDON(CityColor.BLUE),
    PARIS(CityColor.BLUE),
    FRANKFURT(CityColor.BLUE),
    ST_PETERSBURG(CityColor.BLUE),

    LOS_ANGELES(CityColor.YELLOW),
    JACKSONVILLE(CityColor.YELLOW),
    MEXICO_CITY(CityColor.YELLOW),
    BOGOTA(CityColor.YELLOW),
    LIMA(CityColor.YELLOW),
    SAO_PAULO(CityColor.YELLOW),
    SANTIAGO(CityColor.YELLOW),
    BUENOS_AIRES(CityColor.YELLOW),
    LAGOS(CityColor.YELLOW),

    TRIPOLI(CityColor.BLACK),
    CAIRO(CityColor.BLACK),
    ISTANBUL(CityColor.BLACK),
    MOSCOW(CityColor.BLACK),

    LAKE_BAIKAL(CityColor.RED),
}

@Serializable
enum class CityColor(val textStyle: TextStyle) {
    YELLOW(TextColors.black on TextColors.yellow),
    BLUE(TextColors.white on TextColors.blue),
    BLACK(TextColors.white on TextColors.black),
    RED(TextColors.black on TextColors.red),
    ;

    fun text() = textStyle(name)
}
