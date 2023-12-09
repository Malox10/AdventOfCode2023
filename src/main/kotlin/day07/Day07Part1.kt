package day07

import readResourceLines

fun main() {
    val input = readResourceLines("Day07.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Long {
    val rounds = parse(input)
    val sortedRounds = rounds.sorted()
    return sortedRounds.mapIndexed { index, round ->
        round.bet * (index + 1)
    }.sum()
}

fun parse(input: List<String>): List<Round> {
    val labelMap = Label.values().associateBy { it.identifier }
    val rounds = input.map { line ->
        val (handString, betString) = line.split(" ").map { it.trim() }
        val bet = betString.toLong()


        val labels = handString.map { char -> labelMap[char]!! }
        val duplicates = labels
            .asSequence()
            .distinct()
            .map { char -> labels.count { it == char } }.toList().sortedDescending()
        val type = when(duplicates) {
            listOf(5) -> Type.FiveOfAKind
            listOf(4, 1) -> Type.FourOfAKind
            listOf(3, 2) -> Type.FullHouse
            listOf(3, 1, 1) -> Type.ThreeOfAKind
            listOf(2, 2, 1) -> Type.TwoPair
            listOf(2, 1, 1, 1) -> Type.OnePair
            else -> Type.HighCard
        }

        val hand = Hand(labels, type)
        Round(hand, bet)
    }
    return rounds
}

data class Round(val hand: Hand, val bet: Long): Comparable<Round> {
    override fun compareTo(other: Round): Int {
        return this.hand compareTo other.hand
    }
}

data class Hand(val labels: List<Label>, val type: Type): Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
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

enum class Label(val identifier: Char) {
    Ace('A'),
    King('K'),
    Queen('Q'),
    Jack('J'),
    Ten('T'),
    Nine('9'),
    Eight('8'),
    Seven('7'),
    Six('6'),
    Five('5'),
    Four('4'),
    Three('3'),
    Two('2')
}
enum class Type {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard;
}
