@file:Suppress("DuplicatedCode")

package day10

import readResourceLines
import kotlin.math.sign

fun main() {
    val input = readResourceLines("Day10.txt")
    repeat(1000) {

        val solution = solvePart2(input)
        println(solution)
    }
}

fun solvePart2(input: List<String>): Int {
    val (start, grid) = parse(input)
    val tiles = Tile.values().filter { it.directions.size >= 2 }.mapNotNull { startingTileGuess ->
        grid[start.first][start.second] = startingTileGuess
        var currentLocation = start
        var travellingDirection = startingTileGuess.directions.first()
        val turns = mutableListOf<Turn>()
        do {
            val newLocation = travellingDirection.location + currentLocation
            val newTile = grid.getOrNull(newLocation.first)?.getOrNull(newLocation.second) ?: return@mapNotNull null
            val newDirection = newTile.traverse(travellingDirection) ?: return@mapNotNull null

            val turn = travellingDirection.turn(newDirection)
            currentLocation = newLocation
            travellingDirection = newDirection

            turns.add(turn)
        } while (newLocation != start)

        val turning = Turn.map[turns.sumOf { it.turn }.sign]!!
        Loop(startingTileGuess, turning, startingTileGuess.directions.first())
    }

    val (startingTile, turning, _) = tiles.first()
    grid[start.first][start.second] = startingTile
    var currentLocation = start
    var travellingDirection = startingTile.directions.first()

    val directionMap = Direction.values().associateBy { it.ordinal }
    val onLoopTiles = mutableSetOf(start)
    val insideTiles = mutableSetOf<Location>()
    do {
        val newLocation = travellingDirection.location + currentLocation
        val newTile = grid.getOrNull(newLocation.first)?.getOrNull(newLocation.second) ?: error("should find")
        val newDirection = newTile.traverse(travellingDirection) ?: error("should be able to traverse")

        onLoopTiles.add(newLocation)
        val directionIndex = ((travellingDirection.ordinal - turning.turn) + 4) % 4
        val insideLookingDirection =
            directionMap[directionIndex] ?: error("couldn't find direction with $directionIndex")

        val potentialInsideTiles =
            listOf(newLocation + insideLookingDirection.location, currentLocation + insideLookingDirection.location)
        potentialInsideTiles.filter { !onLoopTiles.contains(it) }.forEach { insideTiles.add(it) }

        currentLocation = newLocation
        travellingDirection = newDirection
    } while (newLocation != start)
    insideTiles.removeIf { onLoopTiles.contains(it) }
    insideTiles.removeIf { location -> grid.getTile(location) == null }

    return expandInnerTiles(onLoopTiles, insideTiles, grid)
}

fun expandInnerTiles(onLoop: Set<Location>, inside: Set<Location>, grid: Grid<Tile>): Int {
    val exploredTiles = (onLoop + inside).toSortedSet { x, y ->
        val first = x.first.compareTo(y.first)
        if(first == 0) + x.second.compareTo(y.second) else first
    }

    val pendingTiles = inside.toMutableSet()
    val innerTiles = inside.toMutableList()
    while(pendingTiles.isNotEmpty()) {
        val exploringTile = pendingTiles.first()
        pendingTiles.remove(exploringTile)
        Direction.values().forEach { direction ->
            val neighbour = exploringTile + direction.location
            if (exploredTiles.contains(neighbour)
                || pendingTiles.contains(neighbour)
            ) return@forEach

            grid.getTile(neighbour) ?: return@forEach
            pendingTiles.add(neighbour)
            innerTiles.add(neighbour)
        }
        exploredTiles.add(exploringTile)
    }

    return innerTiles.toSet().size
}

fun Grid<Tile>.getTile(location: Location) = this.getOrNull(location.first)?.getOrNull(location.second)

data class Loop(val startingTile: Tile, val direction: Turn, val startingDirection: Direction)

enum class Turn(val turn: Int) {
    Right(-1),
    Straight(0),
    Left(1);

    companion object {
        val map = Turn.values().associateBy { it.turn }
    }
}

fun Direction.turn(other: Direction): Turn {
    val sign = if(this.ordinal % 2 == 0) -1 else 1
    val absolute = (((this.ordinal + other.ordinal) + 5) % 4) - 1
    val turnNumber = (sign * absolute)
    return Turn.map[turnNumber % 2] ?: error("")
}
