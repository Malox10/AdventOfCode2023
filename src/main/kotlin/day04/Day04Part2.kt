package day04

import readResourceLines

fun main() {
    val input = readResourceLines("Day04.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val cards = parse(input)
    val cardCounts = Array(cards.size) { 1 }
    var cardPointer = 0
    while(cardPointer < cards.size) {
        val currentCard = cards[cardPointer]
        val score = currentCard.score()

        (currentCard.id until currentCard.id + score).forEach { wonCardIndex ->
            if(wonCardIndex >= cardCounts.size) return@forEach
            cardCounts[wonCardIndex] += cardCounts[cardPointer]
        }
        cardPointer++
    }

    return cardCounts.sum()
}

//Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
fun parse(input: List<String>): List<Card> {
    val cards = input.map { line ->
        val (idString, numberString) = line.split(": ")
        val id = idString.split(" ").filter { it != "" }[1].toInt()

        val (targets, tries) = numberString.split(" | ").map { numbers ->
            numbers.split(" ").filter { it != "" }.map { it.toInt() }.toSet()
        }

        Card(id, targets, tries)
    }

    return cards
}

data class Card(val id: Int, val targets: Set<Int>, val tries: Set<Int>) {
    fun score() = targets.intersect(tries).size
}
