package day08

import readResourceLines

const val start = "AAA"
const val end = "ZZZ"
fun main() {
    val input = readResourceLines("Day08.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val network = parse(input)
    var currentNode = start
    var counter = 0
    do {
        val direction = network.directions.next()
        currentNode = when(direction) {
            Direction.Left -> network.nodes[currentNode]!!.left
            Direction.Right -> network.nodes[currentNode]!!.right
        }
        counter ++
    } while(currentNode != end)

    return counter
}

fun parse(input: List<String>): Network {
    val directionMap = Direction.values().associateBy { it.label }
    val directions = input.first().map { char -> directionMap[char]!! }

    val nodeInput = input.drop(2)
    val map = nodeInput.associate { line ->
        val (currentLocation, leftRightString) = line.split(" = (")
        val key = currentLocation.trim()

        val (left, right) = leftRightString.replace(')', ' ').split(", ").map { it.trim() }

        key to Node(left, right)
    }

    return Network(LoopedList(directions), map)
}

data class Network(val directions: LoopedList<Direction>, val nodes: Map<String, Node>)

data class Node(val left: String, val right: String)

enum class Direction(val label: Char) {
    Left('L'),
    Right('R')
}

class LoopedList<T>(private val inner: List<T>) {
    private var index = 0

    fun next(): T {
        val element = inner[index]
        index++
        if(index >= inner.size) index = 0
        return element
    }
}