package day11

import readResourceLines
import kotlin.math.abs

fun main() {
    val input = readResourceLines("Day11.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Long {
    val stars = parse(input)
    var sum = 0L
    stars.indices.forEach { firstStarIndex ->
        (firstStarIndex + 1 until stars.size).forEach { secondStarIndex ->
            val firstStar = stars[firstStarIndex]
            val secondStar = stars[secondStarIndex]
            sum += abs(secondStar.first - firstStar.first) + abs(secondStar.second - firstStar.second)
        }
    }

    return sum
}

const val star = '#'
fun parse(input: List<String>): List<Pair<Long, Long>> {
    var rowOffsetCounter = 0L
    val rowOffsets = input.map { line ->
        if(!line.contains(star)) rowOffsetCounter++
        rowOffsetCounter
    }

    val stars = mutableListOf<Pair<Long, Long>>()
    var columnOffset = 0L
    (0 until input.first().length).forEach { column ->
        var foundStar = false
        (input.indices).forEach { row ->
            val char = input[row][column]
            if(char == star) {
                val rowOffset = rowOffsets[row]
                stars.add(row + rowOffset to column + columnOffset)
                foundStar = true
            }
        }
        if(!foundStar) columnOffset++
    }

    return stars
}
