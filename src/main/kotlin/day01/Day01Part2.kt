package day01

import readResourceLines

fun main() {
    val input = readResourceLines("Day01.txt")
    val solution = solvePart2Version2(input)
    println(solution)
}

//fun solvePart2(input: List<String>) = input
//    .map { line ->
//        line.mapIndexedNotNull { index, _ ->
//            numberMap.mapNotNull { (key, value) ->
//                line[index].digitToIntOrNull()?.let { return@mapIndexedNotNull listOf(it) }
//                val substring = (index + key.length).let { upperbound -> if(upperbound <= line.length) line.substring(index until index + key.length) else return@mapNotNull null  }
//                if(substring.contains(key)) value else null
//            }
//        }.flatten().let { (it.first().toString() + it.last()).toInt() }
//    }.sum()

fun solvePart2Version2(input: List<String>): Int {
    val numbersAndDigits = numberMap + (1..9).toList().associateBy { it.toString() }
    return input.sumOf { line ->
        val (_, firstValue) = numbersAndDigits
            .map { (key, value) -> line.indexOf(key) to value }
            .filter { (index, _) -> index != -1 }
            .minByOrNull { (index, _) -> index }!!

        val (_, lastValue) = numbersAndDigits
            .map { (key, value) -> line.lastIndexOf(key) to value }
            .filter { (index, _) -> index != -1 }
            .maxByOrNull { (index, _) -> index }!!

        firstValue * 10 + lastValue
    }
}

val numberMap = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)
