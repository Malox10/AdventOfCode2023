package day04

import readResourceLines
import kotlin.math.pow

fun main() {
    val input = readResourceLines("Day04.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>) = input
    .map { line ->
        line.split(": ")[1].split(" | ").map { numbers -> numbers.split(" ").filter { it != "" }.map { it.toInt() } }
    }.sumOf { (need, have) ->
        val intersect = need.intersect(have.toSet()).size
        if(intersect <= 0) return@sumOf 0.0
        2.0.pow(intersect - 1)
    }
