package day03

import readResourceLines

fun main() {
    val input = readResourceLines("Day03.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val gears = parse(input)
    val activeGears = gears.filter { gear ->
        val startOffsets = listOf(-1 to -1, 0 to -1, 1 to -1)
        val hasStartSymbol = startOffsets.map { it + (gear.row to gear.start) }.any { !input.isEmptyAt(it) }
        if (hasStartSymbol) return@filter true

        val endOffsets = listOf(-1 to 1, 0 to 1, 1 to 1)
        val hasEndSymbol = endOffsets.map { it + (gear.row to gear.end) }.any { !input.isEmptyAt(it) }
        if (hasEndSymbol) return@filter true

        val hasMiddleSymbol =
            (gear.start..gear.end).flatMap { column -> listOf(gear.row + 1 to column, gear.row - 1 to column) }
                .any { !input.isEmptyAt(it) }
        hasMiddleSymbol
    }

    return activeGears.sumOf { input.getGear(it) }
}

fun List<String>.getGear(gear: Gear) = this[gear.row].substring(gear.start..gear.end).toInt()
fun List<String>.isEmptyAt(index: Pair<Int, Int>): Boolean {
    val char = this.get(index) ?: return true
    return char == '.' || char.isDigit()
}
fun List<String>.get(index: Pair<Int, Int>) = this.getOrNull(index.first)?.getOrNull(index.second)

fun parse(input: List<String>): List<Gear> {
    val gears = mutableListOf<Gear>()
    input.forEachIndexed { rowIndex, line ->
        var start: Int? = null
        line.forEachIndexed { columnIndex, char ->
            if(char.isDigit() && start == null) {
                 start = columnIndex
            } else if(!char.isDigit()) {
                start?.let { gears.add(Gear(rowIndex, it, columnIndex - 1)) }
                start = null
            }
        }
        start?.let { gears.add(Gear(rowIndex, it, line.length - 1)) }
    }

    return gears
}

data class Gear(val row: Int, val start: Int, val end: Int)
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = first + other.first to second + other.second
