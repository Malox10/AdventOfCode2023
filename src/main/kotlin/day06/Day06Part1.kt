package day06

import readResourceLines

fun main() {
    val input = readResourceLines("Day06.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val races = parse(input)
    return races.map { race ->
        (1 until race.length).filter { timeHeld ->
            val remainingTime = race.length - timeHeld
            val distanceTravelled = remainingTime * timeHeld
            distanceTravelled > race.distance
        }.size
    }.reduce(Int::times)
}

fun parse(input: List<String>): List<Race> {
    val (lengths, distances) = input.map { line -> line.split(" ").filter { it != "" }.drop(1).map { it.trim().toInt() } }
    return lengths.indices.map {
        Race(lengths[it], distances[it])
    }
}

data class Race(val length: Int, val distance: Int)