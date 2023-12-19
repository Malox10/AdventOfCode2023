package day17

import readResourceLines
import java.util.PriorityQueue

fun main() {
    val input = readResourceLines("Day17.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val grid = input.map { line -> line.map { it.digitToInt() } }

    val start = 0 to 0
    val direction = Direction.East
    val pathfinder = AStar(grid, State(start, direction, 0, 0))

    return pathfinder.findEnd().heat
}

class AStar(private val grid: List<List<Int>>, start: State) {
    private val end = (grid.size - 1) to (grid.first().size - 1)
    private val priorityQueue =  PriorityQueue<State> { a, b ->
        (a.heat * 3) + (end - a.location).sum() compareTo (b.heat * 3) + (end - b.location).sum()
//        if(heatCompare != 0) return@PriorityQueue heatCompare
//        (end - a.location).sum() compareTo (end - b.location).sum()
    }

    init {
        priorityQueue.add(start)
    }

    private val cache: MutableMap<Pair<Location, Direction>, Pair<Int, Int>> = mutableMapOf()
    fun findEnd(): State {
        var counter = 0
        while (priorityQueue.isNotEmpty()) {
            val currentState = priorityQueue.remove()
            val newDirections = Direction.values().toMutableList().let { directions ->
                directions.remove(currentState.direction.opposite())
                if(currentState.sameDirectionMoves >= 3) directions.remove(currentState.direction)
                directions.toList()
            }

            for (newDirection in newDirections) {


                val newLocation = currentState.location + newDirection.offset
                val additionalHeat = grid.getHeat(newLocation) ?: continue
                val newSameDirectionMoves = if(currentState.direction == newDirection) currentState.sameDirectionMoves +  1 else 1

                val newState = State(
                    newLocation,
                    newDirection,
                    currentState.heat + additionalHeat,
                    newSameDirectionMoves
                )
                if(newLocation == end) {
                    return newState
                }

                cache[currentState.location to currentState.direction] = currentState.heat to currentState.sameDirectionMoves
                val value = cache[newState.location to newState.direction]
                if(value != null && value.first < newState.heat && value.second <= newState.sameDirectionMoves) continue
                priorityQueue.add(newState)
            }
        }

        error("Did not find end")
    }
}

fun List<List<Int>>.getHeat(location: Location) = this.getOrNull(location.first)?.getOrNull(location.second)
data class State(val location: Location, val direction: Direction, val heat: Int, val sameDirectionMoves: Int)

typealias Location = Pair<Int, Int>
enum class Direction(val offset: Location) {
    North(-1 to 0),
    East(0 to 1),
    South(1 to 0),
    West(0 to -1);

    fun opposite() = when(this) {
        North -> South
        East -> West
        South -> North
        West -> East
    }
}

fun Location.sum() = first + second
operator fun Location.minus(other: Location) = first - other.first to second - other.second
operator fun Location.plus(other: Location) = first + other.first to second + other.second