package day16

import readResourceLines

fun main() {
    val input = readResourceLines("Day16.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val grid = input.map { line -> line.map { Symbol.map[it]!! } }
    return grid.travel(State(0 to 0, Direction.East))
}

typealias Grid = List<List<Symbol>>
fun Grid.travel(start: State): Int {
    val statesToExplore = mutableListOf(start)
    val exploredStates = mutableSetOf<State>()
    while (statesToExplore.isNotEmpty()) {
        val newState = statesToExplore.removeFirst()
        var (position, direction) = newState
        do {
            val symbol = this.getSymbol(position) ?: break

            val currentState = State(position, direction)
            if(exploredStates.contains(currentState)) break
            exploredStates.add(currentState)

            val newDirections = symbol.traverse(direction.opposite())
            when(newDirections.size) {
                2 -> {
                    newDirections.forEach { statesToExplore.add(State(position + it, it)) }
                    break
                }
                1 -> direction = newDirections.first()
                0 -> Unit
            }

            position += direction
        } while(true)
    }

    return exploredStates.map { it.location }.toSet().size
}

fun Grid.getSymbol(location: Location) = this.getOrNull(location.first)?.getOrNull(location.second)

typealias Location = Pair<Int, Int>

data class State(val location: Location, val direction: Direction)
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

enum class Symbol(val char: Char, private val crossings: List<Direction>) {
    LeftMirror('\\', listOf(Direction.East, Direction.North, Direction.West, Direction.South)),
    RightMirror('/', listOf(Direction.West, Direction.South, Direction.East, Direction.North)),
    Vertical('|', listOf(Direction.North, Direction.South)),
    Horizontal('-', listOf(Direction.West, Direction.East)),
    Empty('.', listOf());

    fun traverse(from: Direction): List<Direction> {
        return when(this.crossings.size) {
            4 -> listOf(this.crossings[from.ordinal])
            2 -> if(this.crossings.contains(from)) emptyList() else this.crossings
            0 -> emptyList()
            else -> error("can't happen")
        }
    }

    companion object {
        val map = Symbol.values().associateBy { it.char }
    }
}

operator fun Pair<Int, Int>.plus(other: Direction) = first + other.offset.first to second + other.offset.second