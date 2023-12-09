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
        info.lookup.fold(listOf(Range(seed, seed))) { acc, lookup ->
            lookup.transform(acc)
        }
    }.minOf { it -> it.minOf { it.start } }
}

@Suppress("KotlinConstantConditions")
fun parse(input: String): Info {
    val allParts = input.split("map:")
    val seeds = allParts.first().lines().first().split("seeds: ")[1].split(" ").map { it.trim().toLong() }

    val maps = allParts.drop(1)
    val lookups = maps.map { map ->
        val rangeStrings = map.lines().filter { it.contains("\\d[\\d ]+".toRegex()) }
        val ranges = rangeStrings.map { rangeString ->
            val (destination, source, width) = rangeString.split(" ").map { it.trim().toLong() }
            val range = { inputRanges: List<Range> ->
                val checkRange = Range(source, source + width - 1)
                inputRanges.flatMap { inputRange ->
                    //check if range is outside of transform
                    if(inputRange.end < checkRange.start || checkRange.end < inputRange.start) return@flatMap listOf(inputRange to false)

                    val delta = destination - source
                    //check if range is inside range
                    if(checkRange.start <= inputRange.start && inputRange.end <= checkRange.end) return@flatMap listOf(inputRange.shift(delta) to true)

                    //check if range intersects range on the left
                    if(inputRange.start < checkRange.start && inputRange.end <= checkRange.end) {
                        val insideRange = Range(checkRange.start, inputRange.end).shift(delta)
                        val outsideRange = Range(inputRange.start, checkRange.start - 1)
                        return@flatMap listOf(insideRange to true, outsideRange to false)
                    }

                    //check if range intersects range on the right
                    if(checkRange.start <= inputRange.start && checkRange.end < inputRange.end) {
                        val insideRange = Range(inputRange.start, checkRange.end).shift(delta)
                        val outsideRange = Range(checkRange.end + 1, inputRange.end)
                        return@flatMap listOf(insideRange to true, outsideRange to false)
                    }

                    if(inputRange.start < checkRange.start && checkRange.end < inputRange.end) {
                        val insideRange = checkRange.shift(delta)
                        val leftOutsideRange = Range(inputRange.start, checkRange.start - 1)
                        val rightOutsideRange = Range(checkRange.end + 1, inputRange.end)
                        return@flatMap listOf(insideRange to true, leftOutsideRange to false, rightOutsideRange to false)
                    }

                    error("This shouldn't happen")
                }
            }
            range
        }
        Lookup(ranges)
    }
    return Info(seeds, lookups)
}

data class Info(val seeds: List<Long>, val lookup: List<Lookup>)

data class Lookup(val ranges: List<Transform>) {
    fun transform(input: List<Range>): List<Range> {
        val untransformed = input.toMutableList()
        val transformed = mutableListOf<Range>()
        for(range in ranges) {
            val output = range(untransformed)
            untransformed.clear()
            output.forEach { (range, isTransformed) ->
                if(isTransformed) transformed.add(range) else untransformed.add(range)
            }
        }
        return untransformed + transformed
    }
}

typealias Transform = (List<Range>) -> List<Pair<Range, Boolean>>
data class Range(val start: Long, val end: Long) {
    fun shift(offset: Long) = Range(start + offset, end + offset)
}
//data class Range(val start: Long, val end: Long)