package day15

import readResource

fun main() {
    val input = readResource("Day15.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: String): Int {
    val instructions = input.split(",")
    return instructions.sumOf { it.hash() }
}

fun String.hash(): Int {
    var register = 0
    for(char in this) {
        register += char.code
        register *= 17
        register %= 256
    }
    return register
}
