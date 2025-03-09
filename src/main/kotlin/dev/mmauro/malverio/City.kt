package dev.mmauro.malverio

import androidx.compose.ui.graphics.Color
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
    DAR_ES_SALAAM(CityColor.YELLOW),
    JACKSONVILLE(CityColor.YELLOW),
    KHARTOUM(CityColor.YELLOW),
    KINSHASA(CityColor.YELLOW),
    LAGOS(CityColor.YELLOW),
    LIMA(CityColor.YELLOW),
    LOS_ANGELES(CityColor.YELLOW),
    MEXICO_CITY(CityColor.YELLOW),
    SANTIAGO(CityColor.YELLOW),
    SAO_PAULO(CityColor.YELLOW),

    ANTANANARIVO(CityColor.BLACK),
    BAGHDAD(CityColor.BLACK),
    CAIRO(CityColor.BLACK),
    DELHI(CityColor.BLACK),
    ISTANBUL(CityColor.BLACK),
    KOLKATA(CityColor.BLACK),
    MOSCOW(CityColor.BLACK),
    TEHRAN(CityColor.BLACK),
    TRIPOLI(CityColor.BLACK),
    RIYADH(CityColor.BLACK),
    NEW_MUMBAI(CityColor.BLACK),

    JAKARTA(CityColor.RED),
    LAKE_BAIKAL(CityColor.RED),
    ;

    override fun text() = color.textStyle(name)
    override fun plainText() = name
    override fun color() = color.color
}

@Serializable
enum class CityColor(val textStyle: TextStyle, val color: Color) : Textable {
    YELLOW(TextColors.black on TextColors.yellow, Color(0.8f, 0.7f, 0.2f)),
    BLUE(TextColors.white on TextColors.blue, Color(0.2f, 0.2f, 0.8f)),
    BLACK(TextColors.black on TextColors.white, Color(0.2f, 0.2f, 0.2f)),
    RED(TextColors.black on TextColors.red, Color(0.8f, 0.2f, 0.2f)),
    ;

    override fun text() = textStyle(name)
    override fun plainText() = name
    override fun color() = color
}
