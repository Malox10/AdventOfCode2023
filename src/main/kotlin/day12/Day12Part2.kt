package day12

import readResourceLines

//2121046676 too low
fun main() {
    val input = readResourceLines("Day12.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Long {
    val rows = parse(input)
    val extendedRows= rows.map { row ->
        val newSprings = (1..5).map { row.springs + listOf(Status.Unknown) }.flatten().dropLast(1)
        val newClusters = (1..5).map { row.clusters }.flatten()
        Row(newSprings, newClusters)
    }

    return extendedRows.sumOf { findCluster(it.springs, it.clusters) }
}