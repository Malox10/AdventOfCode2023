@file:Suppress("DuplicatedCode")

package day07

import readResourceLines

fun main() {
    val input = readResourceLines("Day07.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Long {
    val rounds = parsePart2(input)
    val sortedRounds = rounds.sorted()
    return sortedRounds.mapIndexed { index, round ->
        round.bet * (index + 1)
    }.sum()
}

fun parsePart2(input: List<String>): List<RoundP2> {
    val labelMap = LabelP2.values().associateBy { it.identifier }
    val nonJacks = LabelP2.values().filter { it != LabelP2.Jack }

    val rounds = input.map { line ->
        val (handString, betString) = line.split(" ").map { it.trim() }
        val bet = betString.toLong()

        val labels = handString.map { char -> labelMap[char]!! }
        val type = if(labels.contains(LabelP2.Jack)) {
            nonJacks.map { nonJack ->
                val newLabels = labels.map { label -> if (label == LabelP2.Jack) nonJack else label }
                getType(newLabels)
            }.minOf { it }
        } else {
            getType(labels)
        }

        val hand = HandP2(labels, type)
        RoundP2(hand, bet)
    }
    return rounds
}

//really lazy solution for the different ordering, just make everything a new class
data class RoundP2(val hand: HandP2, val bet: Long): Comparable<RoundP2> {
    override fun compareTo(other: RoundP2): Int {
        return this.hand compareTo other.hand
    }
}

data class HandP2(val labels: List<LabelP2>, val type: Type): Comparable<HandP2> {
    override fun compareTo(other: HandP2): Int {
        if(this.type < other.type) return 1
        if(this.type > other.type) return -1

        for(i in labels.indices) {
            if(this.labels[i] < other.labels[i]) return 1
            if(this.labels[i] > other.labels[i]) return -1
        }

        return 0
    }

    override fun toString(): String {
        return "$type: ${labels[0].identifier}${labels[1].identifier}${labels[2].identifier}${labels[3].identifier}${labels[4].identifier}"
    }
}

enum class LabelP2(val identifier: Char) {
    Ace('A'),
    King('K'),
    Queen('Q'),
    Ten('T'),
    Nine('9'),
    Eight('8'),
    Seven('7'),
    Six('6'),
    Five('5'),
    Four('4'),
    Three('3'),
    Two('2'),
    Jack('J')
}

fun getType(labels: List<LabelP2>): Type {
    val duplicates = labels
        .asSequence()
        .distinct()
        .map { char -> labels.count { it == char } }.toList().sortedDescending()
    return when(duplicates) {
        listOf(5) -> Type.FiveOfAKind
        listOf(4, 1) -> Type.FourOfAKind
        listOf(3, 2) -> Type.FullHouse
        listOf(3, 1, 1) -> Type.ThreeOfAKind
        listOf(2, 2, 1) -> Type.TwoPair
        listOf(2, 1, 1, 1) -> Type.OnePair
        else -> Type.HighCard
    }
}
