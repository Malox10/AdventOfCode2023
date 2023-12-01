package day01

import readResourceLines

fun main() {
    val input = readResourceLines("Day01.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>) =
    input.map { line -> line.mapNotNull { it.digitToIntOrNull() } }.sumOf { it.first() * 10 + it.last() }
