package day16

import readResourceLines

fun main() {
    val input = readResourceLines("Day16.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val grid = input.map { line -> line.map { Symbol.map[it]!! } }
    val startingPositions = grid.indices.map { State(0 to it, Direction.South) } +
            grid.first().indices.map { State(it to 0, Direction.East) } +
            grid.first().indices.map { State(it to grid.first().size - 1, Direction.West) } +
            grid.indices.map { State(grid.size - 1 to it, Direction.North) }
    return startingPositions.maxOf { grid.travel(it) }
}