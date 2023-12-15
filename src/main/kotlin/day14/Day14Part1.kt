package day14

import readResourceLines

fun main() {
    val input = readResourceLines("Day14.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val platform = parse(input)
    do {
        val changed = platform.movePlatform()
    } while(changed)

    return platform.reversed().mapIndexed { index, row -> row.count { it == State.Rolling } * (index + 1) }.sum()
}

fun Platform.movePlatform(direction: Direction = Direction.North): Boolean {
    var madeMoves = false
    for (row in this.indices) {
        for (column in this.first().indices) {
            val location = row to column
            val state = this.getState(location) ?: continue
            if(state != State.Rolling) continue

            val newLocation = location + direction.offset
            val newSpace = this.getState(newLocation)
            if(newSpace != State.Empty) continue

            this[newLocation.first][newLocation.second] = State.Rolling
            this[location.first][location.second] = State.Empty
            madeMoves = true
        }
    }

    return madeMoves
}

fun Platform.getState(location: Pair<Int, Int>) = this.getOrNull(location.first)?.getOrNull(location.second)
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = first + other.first to second + other.second

fun parse(input: List<String>): Platform {
    return input.map { line ->
        line.map { char -> State.map[char]!! }.toMutableList()
    }
}

enum class State(val character: Char) {
    Rolling('O'),
    Stationary('#'),
    Empty('.');

    companion object {
        val map = State.values().associateBy { it.character }
    }
}

typealias Platform = List<MutableList<State>>
