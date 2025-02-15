package dev.mmauro.pandemics2helper

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
    SAINT_PETERSBURG(CityColor.BLUE),

    LOS_ANGELES(CityColor.YELLOW),
    JACKSONVILLE(CityColor.YELLOW),
    MEXICO_CITY(CityColor.YELLOW),
    BOGOTA(CityColor.YELLOW),
    LIMA(CityColor.YELLOW),
    SAO_PAULO(CityColor.YELLOW),
    SANTIAGO(CityColor.YELLOW),
    BUENO_AIRES(CityColor.YELLOW),
    LAGOS(CityColor.YELLOW),

    TRIPOLI(CityColor.BLACK),
    CAIRO(CityColor.BLACK),
    ISTANBUL(CityColor.BLACK),
    MOSCOW(CityColor.BLACK),

    LAKE_BAIKAL(CityColor.RED),
}

enum class CityColor {
    YELLOW,
    BLUE,
    BLACK,
    RED,
}