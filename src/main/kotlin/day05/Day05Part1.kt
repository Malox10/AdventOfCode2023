package day05

import readResource

fun main() {
    val input = readResource("Day05.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: String): Long {
    val info = parse(input)
    return info.seeds.map { seed ->
        info.lookup.fold(seed) { acc, lookup ->
            lookup.transform(acc)
        }
    }.min()
}

fun parse(input: String): Info {
    val allParts = input.split("map:")
    val seeds = allParts.first().lines().first().split("seeds: ")[1].split(" ").map { it.trim().toLong() }

    val maps = allParts.drop(1)
    val lookups = maps.map { map ->
        val rangeStrings = map.lines().filter { it.contains("\\d[\\d ]+".toRegex()) }
        val ranges = rangeStrings.map { rangeString ->
            val (destination, source, width) = rangeString.split(" ").map { it.trim().toLong() }
            val range = { input: Long ->
                if ((source until (source + width)).contains(input)) {
                    val delta = destination - source
                    input + delta
                } else {
                    null
                }
            }
            range
        }
        Lookup(ranges)
    }
    return Info(seeds, lookups)
}

data class Info(val seeds: List<Long>, val lookup: List<Lookup>)

data class Lookup(val ranges: List<Range>) {
    fun transform(input: Long): Long {
        for(range in ranges) {
            val output = range(input)
            if(output != null) return output
        }

        return input
    }
}

typealias Range = (Long) -> Long?
//data class Range(val start: Long, val end: Long)