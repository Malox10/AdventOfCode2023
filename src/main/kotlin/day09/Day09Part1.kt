package day09

import readResourceLines

fun main() {
    val input = readResourceLines("Day09.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val lines = parse(input)
    val results = lines.map { line ->
        val rows = mutableListOf(line)
        do {
            val newRow = rows.last().windowed(2).map { (a, b) -> b - a }
            rows.add(newRow)
            val newSet = newRow.toSet()
        } while (!(newSet.size == 1 && newSet.contains(0)))

        rows.fold(0) { acc, element -> element.last() + acc }
    }

    return results.sum()
}

fun parse(input: List<String>) = input.map { line -> line.split(" ").filter { it != ""}.map { it.trim().toInt() } }