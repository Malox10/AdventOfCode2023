package day12

import readResourceLines

//9759 too high
//9284 too high
fun main() {
    val input = readResourceLines("Day12.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val rows = parse(input)
    return rows.sumOf {
        val combinations = findCluster(it.springs, it.clusters)
        val combinationsBruteforce = bruteForceRow(it.springs, it.clusters)
        if(combinations != combinationsBruteforce) println("difference:")
        println("$combinations $combinationsBruteforce")
        combinationsBruteforce
    }
}

fun bruteForceRow(springs: List<Status>, clusters: List<Int>): Int {
    if(clusters.isEmpty()) return 1

    val firstUnknown = springs.indexOfFirst { it == Status.Unknown }
    if(firstUnknown == -1) {
        return if(springs.validate(clusters)) 1 else 0
    }
    val withBrokenSpring = springs.toMutableList().also { it[firstUnknown] = Status.Broken }
    val withWorkingSpring = springs.toMutableList().also { it[firstUnknown] = Status.Working }
    return bruteForceRow(withBrokenSpring, clusters) + bruteForceRow(withWorkingSpring, clusters)
}

fun List<Status>.validate(clusters: List<Int>): Boolean {
    if(this.count { it == Status.Broken } != clusters.sum()) return false

    var remainingSprings = this
    clusters.forEachIndexed { index, clusterLength ->
        val indexOfClusterStart = remainingSprings.indexOfFirst { it == Status.Broken }
        if(indexOfClusterStart == -1) return false

        val indexOfClusterEnd = indexOfClusterStart + clusterLength
        if(indexOfClusterEnd > remainingSprings.size) return false

        val separatorSpring = remainingSprings.getOrNull(indexOfClusterEnd) ?: Status.Working
        if(separatorSpring != Status.Working) return false
        val brokenSprings = remainingSprings.subList(indexOfClusterStart, indexOfClusterEnd)
        if(!brokenSprings.all { it == Status.Broken }) return false

        if(index >= clusters.size - 1) {
//            this.forEach { print(it.character) }
//            println()
            return true
        }

        val newStart = indexOfClusterEnd + 1
        val newEnd = remainingSprings.size
        if(newStart > newEnd) return false
        remainingSprings = remainingSprings.subList(newStart, newEnd)
    }
//    println(this)
    return true
}

typealias State = Pair<List<Status>, List<Int>>
val cache: MutableMap<State, Int> = mutableMapOf()
fun findCluster(springs: List<Status>, clusters: List<Int>): Int {
    val target = clusters.firstOrNull() ?: return if(springs.any { it == Status.Broken }) 0 else 1
    if(springs.size < clusters.sum() + (clusters.size - 1)) return 0


    val key: State = springs to clusters
    cache[key]?.let { return it }

    //0123456789
    //????.#.### 4
    val upperBound = springs.size - target
    val sum = (0..upperBound).sumOf { index ->
        val slice = springs.subList(index, index + target)
        if(slice.size < target) return@sumOf 0

        val allBroken = slice.none { spring -> spring == Status.Working }
        if(!allBroken) return@sumOf 0
        val nextSpringIsWorking = springs.getOrElse(index + target) { Status.Working } != Status.Broken
        if(!nextSpringIsWorking) return@sumOf 0
        val lastSpringIsWorking = springs.getOrElse(index - 1) { Status.Working } != Status.Broken
        if(!lastSpringIsWorking) return@sumOf 0

        //drop the amount of broken springs + the next intact one
        val newSprings = springs.drop(index + target + 1)
        val newClusters = clusters.drop(1)
        val combinations = findCluster(newSprings, newClusters)
        combinations
    }

    cache[key] = sum
    return sum
}

fun parse(input: List<String>): List<Row> {
    val rows = input.map { line ->
        val (springsString, clustersString) = line.split(" ").map { it.trim() }
        val springs = springsString.map { char -> Status.map[char]!! }
        val clusters = clustersString.split(",").map { it.trim().toInt() }
        Row(springs, clusters)
    }
    return rows
}

data class Row(val springs: List<Status>, val clusters: List<Int>) {
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
