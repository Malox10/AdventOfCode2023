package day05

import readResource

fun main() {
    val input = readResource("Day05.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: String): Long {
    val info = parse(input)
    return info.seeds.chunked(2).map { (seed, amount) ->
        info.lookup.fold(listOf(Range(seed, seed + amount - 1))) { acc, lookup ->
            lookup.transform(acc)
        }
    }.minOf { it -> it.minOf { it.start } }
}
