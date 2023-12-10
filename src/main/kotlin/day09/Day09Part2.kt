@file:Suppress("DuplicatedCode")

package day09

import readResourceLines

fun main() {
    val input = readResourceLines("Day09.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val lines = parse(input)
    val results = lines.map { line ->
        val rows = mutableListOf(line)
        do {
            val newRow = rows.last().windowed(2).map { (a, b) -> b - a }
            rows.add(newRow)
            val newSet = newRow.toSet()
        } while (!(newSet.size == 1 && newSet.contains(0)))

        rows.reverse()
        rows.fold(0) { acc, element -> element.first() - acc }
    }

    return results.sum()
}
