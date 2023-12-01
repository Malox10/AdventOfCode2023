package day01

import readResourceLines

fun main() {
    val input = readResourceLines("Day01.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>) = input
    .map { line ->
        line.mapIndexedNotNull { index, _ ->
            numberMap.mapNotNull { (key, value) ->
                line[index].digitToIntOrNull()?.let { return@mapIndexedNotNull listOf(it) }
                val substring = (index + key.length).let { upperbound -> if(upperbound <= line.length) line.substring(index until index + key.length) else return@mapNotNull null  }
                if(substring.contains(key)) value.toInt() else null
            }
        }.flatten().let { (it.first().toString() + it.last()).toInt() }
    }.sum()


val numberMap = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9",
)