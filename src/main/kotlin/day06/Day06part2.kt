package day06

import readResourceLines

fun main() {
    val input = readResourceLines("Day06.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val (time, distance) = input.map { line -> line.filter { it.isDigit() }.toLong() }
    return (1 until time).filter { timeHeld ->
        val remainingTime = time - timeHeld
        val distanceTravelled = remainingTime * timeHeld
        distanceTravelled > distance
    }.size
}