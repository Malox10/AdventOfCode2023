package day14

import readResourceLines

fun main() {
    val input = readResourceLines("Day14.txt")
    val solution = solvePart2(input)
    println(solution)
}

val cache: MutableMap<Platform, Int> = mutableMapOf()
fun solvePart2(input: List<String>): Int {
    val platform = parse(input)

    var remainingCycles = 0
    var iteration = 0
    do {
        iteration++
        platform.doOneCycle()
//        platform.print()

        if(cache.contains(platform)) {
            val initial = cache[platform]!!
            val delta = iteration - initial

            val remaining = 1000000000L - iteration
            val actualRemaining = remaining % delta
            remainingCycles = actualRemaining.toInt()
        }

        cache[platform] = iteration
    } while(remainingCycles == 0)

    repeat(remainingCycles) {
        platform.doOneCycle()
    }

    return platform.calculateNorthLoad()
}

enum class Direction(val offset: Pair<Int, Int>) {
    North(-1 to 0),
    West(0 to -1),
    South(1 to 0),
    East(0 to 1)
}

@Suppress("unused")
fun Platform.print() {
    var output = "\n"
    this.map { row ->
        output += row.map { it.character }.joinToString("")
        output += "\n"
    }
    println(output)
}

fun Platform.moveCompletely(direction: Direction) {
    do {
        val changed = this.movePlatform(direction)
    } while(changed)
}

fun Platform.doOneCycle() {
    for (direction in Direction.values()) {
        this.moveCompletely(direction)
    }
}

fun Platform.calculateNorthLoad() = this.reversed().mapIndexed { index, row -> row.count { it == State.Rolling } * (index + 1) }.sum()
