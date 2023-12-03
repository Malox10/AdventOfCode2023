package day03

import readResourceLines

fun main() {
    val input = readResourceLines("Day03.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val range = (-1..1).toList()
    val offsets = range.flatMap { row -> range.map { column -> row to column }}.filter { it != 0 to 0 }

    val gears = mutableListOf<Int>()
    input.forEachIndexed { row, line ->
        line.forEachIndexed inner@ { column, char ->
            if(char != '*') return@inner
            val gearNumbers = mutableSetOf<Int>()
            val numberLocations = offsets.map { it + (row to column) }

            numberLocations.forEach { location ->
                if(input.get(location)?.isDigit() == false) return@forEach
                val number = input.getNumber(location)
                gearNumbers.add(number)
            }

            if(gearNumbers.size == 2) {
                val gearRatio = gearNumbers.reduce(Int::times)
                gears.add(gearRatio)
            }
        }
    }

    return gears.sum()
}

fun List<String>.getNumber(location: Pair<Int, Int>): Int {
    val builder = StringBuilder(this.get(location).toString())
    var afterOffset = 1
    do {
        val newLocation = (0 to afterOffset) + location
        val newChar = this.get(newLocation)
        val isDigit = newChar?.isDigit() ?: false
        if(isDigit) builder.append(newChar)
        afterOffset++
    } while(isDigit)

    var beforeOffset = -1
    do {
        val newLocation = (0 to beforeOffset) + location
        val newChar = this.get(newLocation)
        val isDigit = newChar?.isDigit() ?: false
        if(isDigit) builder.insert(0, newChar)
        beforeOffset--
    } while(isDigit)

    return builder.toString().toInt()
}
