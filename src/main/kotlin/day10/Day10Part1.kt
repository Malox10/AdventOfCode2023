@file:Suppress("DuplicatedCode")

package day10

import readResourceLines

fun main() {
    val input = readResourceLines("Day10.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val (start, grid) = parse(input)
    val steps = Tile.values().filter { it.directions.size >= 2 }.mapNotNull { startingTileGuess ->
        grid[start.first][start.second] = startingTileGuess
        var stepCounter = 0
        var currentLocation = start
        var travellingDirection = startingTileGuess.directions.first()
        do {
            val newLocation = travellingDirection.location + currentLocation
            val newTile = grid.getOrNull(newLocation.first)?.getOrNull(newLocation.second) ?: return@mapNotNull null
            val newDirection = newTile.traverse(travellingDirection) ?: return@mapNotNull null

            currentLocation = newLocation
            travellingDirection = newDirection
            stepCounter++
        } while (newLocation != start)
        stepCounter
    }

    return steps.max() / 2
}

typealias Grid<T> = Array<Array<T>>
typealias Location = Pair<Int, Int>

fun parse(input: List<String>): Pair<Location, Grid<Tile>> {
    var start: Location? = null
    val map = Tile.values().associateBy { it.char }
    val grid: Grid<Tile> = input.mapIndexed { rowIndex, line ->
        line.mapIndexed { columnIndex, char ->
            val newTile = map[char]!!
            if(newTile == Tile.Start) start = (rowIndex to columnIndex)
            newTile
        }.toTypedArray()
    }.toTypedArray()

    if(start == null) error("couldn't find start")
    return (start!! to grid)
}

enum class Tile(val char: Char, val directions: List<Direction>) {
    NS('|', listOf(Direction.North, Direction.South)),
    WE('-', listOf(Direction.West, Direction.East)),
    NE('L', listOf(Direction.North, Direction.East)),
    NW('J', listOf(Direction.North, Direction.West)),
    SW('7', listOf(Direction.South, Direction.West)),
    SE('F', listOf(Direction.South, Direction.East)),
    Nothing('.', listOf()),
    Start('S', listOf());

    fun traverse(from: Direction): Direction? {
        val opposite = when(from) {
            Direction.North -> Direction.South
            Direction.South -> Direction.North
            Direction.West -> Direction.East
            Direction.East -> Direction.West
        }

        if(!this.directions.contains(opposite)) return null
        return this.directions.first { it != opposite }
    }
}
enum class Direction(val location: Location) {
    North(-1 to 0),
    East(0 to 1),
    South(1 to 0),
    West(0 to -1)
}

operator fun Location.plus(other: Location) = this.first + other.first to this.second + other.second
