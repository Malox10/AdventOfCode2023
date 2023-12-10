package day08

import readResourceLines

fun main() {
    val input = readResourceLines("Day08.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Long {
    val network = parse(input)
    val ghosts = network.nodes.keys.filter { it.endsWith("A") }.map { Ghost(it, network) }
    val primeNumbers = ghosts.map { ghost ->
        ghost.simulate().getPrimeNumbers()
    }

    //we can flatten this since all numbers are prime number pairs, so we don't need to account for overlap
    return primeNumbers.flatten().toSet().reduce(Long::times)
}

class Ghost(start: String, private val network: Network) {
    private var currentNode = start
    private val directions = network.directions.copy()
    val pastNodes = mutableListOf<Pair<String, Long>>()

    fun simulate(): Long {
        var foundFinish = false
        var finishCounter = 0L
        var counter = 0L
        while(true) {
            counter++
            pastNodes.add(currentNode to directions.index.toLong())
            val direction = directions.next()
            currentNode = when(direction) {
                Direction.Left -> network.nodes[currentNode]!!.left
                Direction.Right -> network.nodes[currentNode]!!.right
            }

            if(foundFinish && currentNode.isFinish()) break
            if(currentNode.isFinish()) foundFinish = true
            if(foundFinish) finishCounter++
        }

        if((counter % finishCounter) == 0L) return finishCounter
        error("impossible")
    }
}

fun String.isFinish() = this.endsWith("Z")
fun Long.getPrimeNumbers(): List<Long> {
    var remaining = this
    val factors = mutableListOf<Long>()
    loop@ while(remaining >= 2) {
        for (factor in (2..this)) {
            val result = (remaining % factor) == 0L
            if(result) {
                remaining /= factor
                factors.add(factor)
                continue@loop //or break lol
            }
        }
    }

    return factors
}