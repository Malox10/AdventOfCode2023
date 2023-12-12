package day11

import readResourceLines

fun main() {
    val input = readResourceLines("Day11.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>) = solve(input, 999999)
