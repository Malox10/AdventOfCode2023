package day12

import readResourceLines

fun main() {
    val input = readResourceLines("Day12.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Long {
    val rows = parse(input)
    return rows.sumOf {
        bruteForceRow(it.springs, it.clusters)
//        findCluster(it.springs, it.clusters) //didn't work while solving part1
    }
}

fun bruteForceRow(springs: List<Status>, clusters: List<Long>): Long {
    if(clusters.isEmpty()) return 1

    val firstUnknown = springs.indexOfFirst { it == Status.Unknown }
    if(firstUnknown == -1) {
        return if(springs.validate(clusters)) 1 else 0
    }
    val withBrokenSpring = springs.toMutableList().also { it[firstUnknown] = Status.Broken }
    val withWorkingSpring = springs.toMutableList().also { it[firstUnknown] = Status.Working }
    return bruteForceRow(withBrokenSpring, clusters) + bruteForceRow(withWorkingSpring, clusters)
}

fun List<Status>.validate(clusters: List<Long>): Boolean {
    if(this.count { it == Status.Broken }.toLong() != clusters.sum()) return false

    var remainingSprings = this
    clusters.forEachIndexed { index, clusterLength ->
        val indexOfClusterStart = remainingSprings.indexOfFirst { it == Status.Broken }
        if(indexOfClusterStart == -1) return false

        val indexOfClusterEnd = indexOfClusterStart + clusterLength.toInt()
        if(indexOfClusterEnd > remainingSprings.size) return false

        val separatorSpring = remainingSprings.getOrNull(indexOfClusterEnd) ?: Status.Working
        if(separatorSpring != Status.Working) return false
        val brokenSprings = remainingSprings.subList(indexOfClusterStart, indexOfClusterEnd)
        if(!brokenSprings.all { it == Status.Broken }) return false

        if(index >= clusters.size - 1) {
            return true
        }

        val newStart = indexOfClusterEnd + 1
        val newEnd = remainingSprings.size
        if(newStart > newEnd) return false
        remainingSprings = remainingSprings.subList(newStart, newEnd)
    }

    return true
}

typealias State = Pair<List<Status>, List<Long>>
val cache: MutableMap<State, Long> = mutableMapOf()
fun findCluster(springs: List<Status>, clusters: List<Long>, previousPath: List<Status> = emptyList()): Long {
    val target = clusters.firstOrNull()?.toInt() ?: run {
        return if (springs.any { it == Status.Broken }) 0 else 1
    }
    if(springs.size < clusters.sum() + (clusters.size - 1)) return 0


    val key: State = springs to clusters
    cache[key]?.let {
         return it
    }

    var skipRemainingIterations = false
    val upperBound = springs.size - target
    val sum = (0..upperBound).sumOf { index ->
        if(skipRemainingIterations) {
            return@sumOf 0
        }
        val slice = springs.subList(index, index + target)
        if(slice.size < target) return@sumOf 0

        val allBroken = slice.none { spring -> spring == Status.Working }
        if(!allBroken) return@sumOf 0
        val nextSpringIsWorking = springs.getOrElse(index + target) { Status.Working } != Status.Broken
        if(!nextSpringIsWorking) return@sumOf 0
        val lastSpringIsWorking = springs.getOrElse(index - 1) { Status.Working } != Status.Broken
        if(!lastSpringIsWorking) return@sumOf 0
        val springsBefore = springs.take(index)
        if(springsBefore.any { it == Status.Broken }) return@sumOf 0

        if(slice.first() == Status.Broken) {
            skipRemainingIterations = true
        }
        //drop the amount of broken springs + the next intact one
        val newSprings = springs.drop(index + target + 1)
        val newClusters = clusters.drop(1)
        val combinations = findCluster(newSprings, newClusters, previousPath + springs.take(index + target + 1))
        combinations
    }

    cache[key] = sum
    return sum
}

fun parse(input: List<String>): List<Row> {
    val rows = input.map { line ->
        val (springsString, clustersString) = line.split(" ").map { it.trim() }
        val springs = springsString.map { char -> Status.map[char]!! }
        val clusters = clustersString.split(",").map { it.trim().toLong() }
        Row(springs, clusters)
    }
    return rows
}

data class Row(val springs: List<Status>, val clusters: List<Long>) {
    override fun toString(): String {
        var output = ""
        springs.forEach { spring -> output += spring.character }
        return "$output, $clusters"
    }
}
enum class Status(val character: Char) {
    Working('.'),
    Broken('#'),
    Unknown('?');

    companion object {
        val map = Status.values().associateBy { it.character }
    }
}
