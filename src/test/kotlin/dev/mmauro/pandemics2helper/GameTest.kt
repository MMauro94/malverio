package dev.mmauro.pandemics2helper

import dev.mmauro.pandemics2helper.events.DrawCardEvent
import dev.mmauro.pandemics2helper.events.EpidemicEvent
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class GameTest : FunSpec({
    val a = InfectionCard(City.WASHINGTON)
    val b = InfectionCard(City.ATLANTA)
    val c = InfectionCard(City.SANTIAGO)
    val d = InfectionCard(City.NEW_YORK)
    val e = InfectionCard(City.CHICAGO)
    val f = InfectionCard(City.ISTANBUL)
    val g = InfectionCard(City.CAIRO)
    val h = InfectionCard(City.LAGOS)
    val i = InfectionCard(City.DENVER)
    val deck = setOf(a, b, c, d, e, f, g, h, i)

    context("discarded") {
        withData(
            tuple(
                emptyList(),
                emptyList(),
            ),
            tuple(
                listOf(EpidemicEvent(a)),
                emptyList(),
            ),
            tuple(
                listOf(DrawCardEvent(a), DrawCardEvent(b)),
                listOf(a, b),
            ),
            tuple(
                listOf(DrawCardEvent(a), DrawCardEvent(b), EpidemicEvent(c)),
                emptyList(),
            ),
            tuple(
                listOf(DrawCardEvent(a), DrawCardEvent(b), EpidemicEvent(c), DrawCardEvent(b)),
                listOf(b),
            ),
        ) { (timeline, expected) ->
            val game = Game(deck)

            game.addEvents(timeline).discards shouldBe expected
        }
    }

    context("partitionedDeck") {
        withData(
            tuple(
                emptyList(),
                listOf(deck),
            ),
            tuple(
                listOf(DrawCardEvent(c), DrawCardEvent(g), DrawCardEvent(e)),
                listOf(setOf(a, b, d, f, h, i)),
            ),
            tuple(
                listOf(DrawCardEvent(c), DrawCardEvent(g), EpidemicEvent(e)),
                listOf(setOf(c, e, g), setOf(a, b, d, f, h, i)),
            ),
            tuple(
                listOf(DrawCardEvent(c), DrawCardEvent(g), EpidemicEvent(e), DrawCardEvent(e)),
                listOf(setOf(c, g), setOf(a, b, d, f, h, i)),
            ),
            tuple(
                listOf(
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    EpidemicEvent(e),
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    DrawCardEvent(e),
                    DrawCardEvent(a),
                    EpidemicEvent(b),
                ),
                listOf(setOf(c, g, e, a, b), setOf(d, f, h, i)),
            ),
            tuple(
                listOf(
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    EpidemicEvent(e),
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    DrawCardEvent(e),
                    DrawCardEvent(a),
                    EpidemicEvent(b),
                    DrawCardEvent(a),
                    EpidemicEvent(i),
                ),
                listOf(setOf(a, i), setOf(c, g, e, b), setOf(d, f, h)),
            ),
        ) { (timeline, expected) ->
            val game = Game(deck = deck)

            game.addEvents(timeline).partitionedDeck shouldBe expected
        }
    }
})