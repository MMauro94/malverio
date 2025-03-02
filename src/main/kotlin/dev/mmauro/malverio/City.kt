package dev.mmauro.malverio

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle
import kotlinx.serialization.Serializable

@Serializable
enum class City(val color: CityColor) : Textable {
    ATLANTA(CityColor.BLUE),
    CHICAGO(CityColor.BLUE),
    DENVER(CityColor.BLUE),
    FRANKFURT(CityColor.BLUE),
    JOHANNESBURG(CityColor.BLUE),
    LONDON(CityColor.BLUE),
    NEW_YORK(CityColor.BLUE),
    PARIS(CityColor.BLUE),
    SAN_FRANCISCO(CityColor.BLUE),
    ST_PETERSBURG(CityColor.BLUE),
    WASHINGTON(CityColor.BLUE),

    BOGOTA(CityColor.YELLOW),
    BUENOS_AIRES(CityColor.YELLOW),
    JACKSONVILLE(CityColor.YELLOW),
    KINSHASA(CityColor.YELLOW),
    LAGOS(CityColor.YELLOW),
    LIMA(CityColor.YELLOW),
    LOS_ANGELES(CityColor.YELLOW),
    MEXICO_CITY(CityColor.YELLOW),
    SANTIAGO(CityColor.YELLOW),
    SAO_PAULO(CityColor.YELLOW),

    BAGHDAD(CityColor.BLACK),
    CAIRO(CityColor.BLACK),
    ISTANBUL(CityColor.BLACK),
    MOSCOW(CityColor.BLACK),
    TEHRAN(CityColor.BLACK),
    TRIPOLI(CityColor.BLACK),

    LAKE_BAIKAL(CityColor.RED),
    ;

    override fun text() = color.textStyle(name)
}

@Serializable
enum class CityColor(val textStyle: TextStyle) : Textable {
    YELLOW(TextColors.black on TextColors.yellow),
    BLUE(TextColors.white on TextColors.blue),
    BLACK(TextColors.black on TextColors.white),
    RED(TextColors.black on TextColors.red),
    ;

    override fun text() = textStyle(name)
}
