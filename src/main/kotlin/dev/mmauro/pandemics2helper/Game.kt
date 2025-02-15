package dev.mmauro.pandemics2helper

import dev.mmauro.pandemics2helper.events.DrawCardEvent
import dev.mmauro.pandemics2helper.events.Event
import dev.mmauro.pandemics2helper.events.IntensifyEvent

data class Game(
    val timeline: List<Event>,
    val deck: Set<InfectionCard>,
) {

    fun addEvent(event: Event) = copy(timeline = timeline + event)

    fun addEvents(events: List<Event>) = events.fold(this) { prev, it -> prev.addEvent(it) }

    fun discardedSequence(): Sequence<List<InfectionCard>> {
        return sequence {
            buildList {
                for (event in timeline) {
                    when (event) {
                        is DrawCardEvent -> add(event.card)
                        IntensifyEvent -> {
                            yield(toList())
                            clear()
                        }
                    }
                }
                yield(toList())
            }
        }
    }

    fun discards() = discardedSequence().last()

    fun deckPartitions(): Sequence<List<Set<InfectionCard>>> {
        return sequenceOf(listOf(deck)) + discardedSequence()
            .map { it.toSet() }
            .runningFold(listOf(deck)) { previous, discarded ->
                buildList {
                    add(discarded)
                    for (prev in previous) {
                        val toAdd = prev - discarded
                        if (toAdd.isNotEmpty()) {
                            add(prev - discarded)
                        }
                    }
                }
            }
            .map { it.drop(1) }
    }

    fun deckPartition() = deckPartitions().last()
}