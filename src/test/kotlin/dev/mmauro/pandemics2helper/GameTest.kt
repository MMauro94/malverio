package dev.mmauro.pandemics2helper

import dev.mmauro.pandemics2helper.events.DrawCardEvent
import dev.mmauro.pandemics2helper.events.IntensifyEvent
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class GameTest : FunSpec({
    val a = InfectionCard(City.WASHINGTON)
    val b = InfectionCard(City.WASHINGTON)
    val c = InfectionCard(City.WASHINGTON)
    val d = InfectionCard(City.NEW_YORK)
    val e = InfectionCard(City.NEW_YORK)
    val f = InfectionCard(City.NEW_YORK)
    val g = InfectionCard(City.ATLANTA)
    val h = InfectionCard(City.CHICAGO)
    val i = InfectionCard(City.CHICAGO)
    val deck = setOf(a, b, c, d, e, f, g, h, i)

    context("discardedSequence()") {
        withData(
            tuple(
                emptyList(),
                listOf(emptyList()),
            ),
            tuple(
                listOf(IntensifyEvent),
                listOf(emptyList(), emptyList()),
            ),
            tuple(
                listOf(DrawCardEvent(a), DrawCardEvent(b)),
                listOf(listOf(a, b)),
            ),
            tuple(
                listOf(DrawCardEvent(a), DrawCardEvent(b), IntensifyEvent),
                listOf(listOf(a, b), emptyList()),
            ),
            tuple(
                listOf(DrawCardEvent(a), DrawCardEvent(b), IntensifyEvent, DrawCardEvent(b)),
                listOf(listOf(a, b), listOf(b)),
            ),
        ) { (timeline, expected) ->
            val game = Game(
                timeline = timeline,
                deck = deck,
            )

            game.discardedSequence().toList() shouldBe expected
        }
    }

    context("deckPartitions()") {
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
                listOf(DrawCardEvent(c), DrawCardEvent(g), DrawCardEvent(e), IntensifyEvent),
                listOf(setOf(c, e, g), setOf(a, b, d, f, h, i)),
            ),
            tuple(
                listOf(DrawCardEvent(c), DrawCardEvent(g), DrawCardEvent(e), IntensifyEvent, DrawCardEvent(e)),
                listOf(setOf(c, g), setOf(a, b, d, f, h, i)),
            ),
            tuple(
                listOf(DrawCardEvent(c), DrawCardEvent(g), DrawCardEvent(e), IntensifyEvent, DrawCardEvent(e), IntensifyEvent),
                listOf(setOf(e), setOf(c, g), setOf(a, b, d, f, h, i)),
            ),
            tuple(
                listOf(
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    DrawCardEvent(e),
                    IntensifyEvent,
                    DrawCardEvent(e),
                    IntensifyEvent,
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    DrawCardEvent(e),
                    DrawCardEvent(a),
                    DrawCardEvent(b),
                    IntensifyEvent,
                ),
                listOf(setOf(c, g, e, a, b), setOf(d, f, h, i)),
            ),
            tuple(
                listOf(
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    DrawCardEvent(e),
                    IntensifyEvent,
                    DrawCardEvent(e),
                    IntensifyEvent,
                    DrawCardEvent(c),
                    DrawCardEvent(g),
                    DrawCardEvent(e),
                    DrawCardEvent(a),
                    DrawCardEvent(b),
                    IntensifyEvent,
                    DrawCardEvent(a),
                    DrawCardEvent(g),
                    IntensifyEvent,
                ),
                listOf(setOf(a, g), setOf(c, e, b), setOf(d, f, h, i)),
            ),
        ) { (timeline, expected) ->
            val game = Game(
                timeline = timeline,
                deck = deck,
            )

            game.deckPartitions().last() shouldBe expected
        }
    }
})