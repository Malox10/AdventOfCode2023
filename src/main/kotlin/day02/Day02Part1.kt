package day02

import readResourceLines

fun main() {
    val input = readResourceLines("Day02.txt")
    val solution = solve(input)
    println(solution)
}

//challenge to write a oneliner
fun solve(input: List<String>) = input.mapIndexed { index, line -> line.contains("((1[3-9]|[2-9]\\d|\\d{3,}) red)|((1[4-9]|[2-9]\\d|\\d{3,}) green)|((1[5-9]|[2-9]\\d|\\d{3,}) blue)".toRegex()) to index }.filter { (filter, _) -> !filter }.sumOf { (_, game) -> game + 1 }