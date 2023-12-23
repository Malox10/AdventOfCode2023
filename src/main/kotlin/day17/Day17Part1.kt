@file:Suppress("DuplicatedCode", "unused")

package day17

import readResourceLines
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

fun main() {
    println(measureTimeMillis {
        val input = readResourceLines("Day17.txt")
        val solution = solve(input)
        println(solution)
    })
}

@Suppress("UNUSED_VARIABLE")
fun solve(input: List<String>): Int {
    val grid = input.map { line -> line.map { it.digitToInt() } }

    val newOptimization = NewOptimization(grid)
    val cache = File("day17Cache.txt")
    if(!cache.exists()) {
        var counter = 0
        val finishNodes = mutableMapOf<Location, Int>()
        for(row in grid.indices) {
            for(column in grid.first().indices) {
                counter++
                if(finishNodes.contains(row to column)) continue
                newOptimization.openList.clear()
                newOptimization.closedList.clear()

                val finishPath = newOptimization.aStar(row to column)
                val lastNode = finishPath.first()
                finishNodes += finishPath.map { it.location to lastNode.heat - it.heat }
//            grid.printPath(finishPath)
            }
        }

        var output = ""
        for(node in finishNodes) {
            output += "${node.key.first},${node.key.second},${node.value}\n"
        }
        cache.writeText(output)
    }
    val finishNodes: MutableMap<Location, Int> = cache.readLines().associate { line ->
        val (a,b,c) = line.split(",").map { it.trim().toInt() }
        (a to b) to c
    }.toMutableMap()


    val pathfinder = PathFinderPart2(grid)
    return pathfinder.aStar(0 to 0).first().heat
}

@Suppress("unused")
fun List<List<Int>>.printPath(finishPath: Iterable<NewOptimization.Node>) {
    println()
    for(row in this.indices) {
        for(column in this.first().indices) {
            if(finishPath.any { it.location == row to column }) print("█") else print(this[row][column])
        }
        print("\n")
    }
}

class Pathfinder(private val grid: List<List<Int>>) {
    private val end = (grid.size - 1) to (grid.first().size - 1)
    data class Node(val key: Key, val heat: Int, var predecessor: Node?)
    data class Key(val location: Location, val direction: Direction, val sameDirectionMoves: Int)

    //    private val factor = 1
    private val openList = PriorityQueue<Node> { a, b ->
//        return@PriorityQueue (a.heat / a.location.sum()) compareTo (b.heat / b.location.sum())
//        return@PriorityQueue (a.heat - a.location.sum() * factor) compareTo (b.heat - b.location.sum() * factor)
        return@PriorityQueue a.heat compareTo b.heat
    }

    private val closedList: MutableSet<Key> = mutableSetOf()

    fun aStar(start: Location): List<Node> {
        openList.add(Node(Key(start, Direction.East, 0), 0, null))

        do {
            val currentNode = openList.remove()
            if(currentNode.key.location == end) {
                val finishPath = mutableListOf<Node>(currentNode)
                var node = currentNode
                while(true) {
                    val predecessor = node.predecessor ?: break
                    finishPath.add(predecessor)
                    node = predecessor
                }
                return finishPath
            }
            closedList.add(currentNode.key)
            expandNode(currentNode)
        } while (openList.isNotEmpty())
        error("no path found")
    }

    private fun expandNode(currentNode: Node)  {
        val newDirections = Direction.values().toMutableList().let { directions ->
                directions.remove(currentNode.key.direction.opposite())
                if(currentNode.key.sameDirectionMoves >= 3) directions.remove(currentNode.key.direction)
                directions.toList()
            }

        for(newDirection in newDirections) {
            val successorLocation = currentNode.key.location + newDirection.offset
            val newAdditionalHeat = grid.getHeat(successorLocation) ?: continue
            val newHeat = currentNode.heat + newAdditionalHeat
            val newSameDirectionMoves = if(currentNode.key.direction == newDirection) currentNode.key.sameDirectionMoves + 1 else 1

            val newKey = Key(successorLocation, newDirection, newSameDirectionMoves)
            val successor = Node(newKey, newHeat, null)
            if(closedList.contains(newKey)) continue

            val cached = openList.find { it.key.location == successor.key.location && it.key.direction == successor.key.direction }
            if(cached != null && cached.heat <= successor.heat && cached.key.sameDirectionMoves <= successor.key.sameDirectionMoves) continue

            successor.predecessor = currentNode
            openList.add(successor)
        }
    }
}


class NewOptimization(private val grid: List<List<Int>>) {
    private val end = (grid.size - 1) to (grid.first().size - 1)
    data class Node(val location: Location, val heat: Int, var predecessor: Node?) {
        override fun equals(other: Any?): Boolean {
            return other is Node && this.location == other.location
        }

        override fun hashCode(): Int {
            var result = location.hashCode()
            result = 31 * result + heat
            result = 31 * result + (predecessor?.hashCode() ?: 0)
            return result
        }
    }

//    private val factor = 1
    val openList = PriorityQueue<Node> { a, b ->
//        return@PriorityQueue (a.heat / a.location.sum()) compareTo (b.heat / b.location.sum())
//        return@PriorityQueue (a.heat - a.location.sum() * factor) compareTo (b.heat - b.location.sum() * factor)
        return@PriorityQueue a.heat compareTo b.heat
    }
    val closedList: MutableSet<Location> = mutableSetOf()

    fun aStar(start: Location): List<Node> {
        openList.add(Node(start, 0, null))

        do {
            val currentNode = openList.remove()
            if(currentNode.location == end) {
                val finishPath = mutableListOf<Node>(currentNode)
                var node = currentNode
                while(true) {
                    val predecessor = node.predecessor ?: break
                    finishPath.add(predecessor)
                    node = predecessor
                }
                return finishPath
            }
            closedList.add(currentNode.location)
            expandNode(currentNode)
        } while (openList.isNotEmpty())
        error("no path found")
    }

    private fun expandNode(currentNode: Node)  {
        Direction.values().mapNotNull { direction ->
            val successorLocation = currentNode.location + direction.offset
            val newAdditionalHeat = grid.getHeat(successorLocation) ?: return@mapNotNull null
            val newHeat = currentNode.heat + newAdditionalHeat
            val successor = Node(successorLocation, newHeat, null)
            if(closedList.contains(successor.location)) return@mapNotNull null

            if(openList.contains(successor)) {
                val previousHeat = openList.find { it == successor }?.heat
                if(previousHeat != null && previousHeat <= successor.heat) return@mapNotNull null
            }

            successor.predecessor = currentNode
            openList.add(successor)
        }
    }
}

//class AStar(private val grid: List<List<Int>>) {
//    private val end = (grid.size - 1) to (grid.first().size - 1)
//    data class Node(val location: Location, val heat: Int, val direction: Direction, val sameDirectionMoves: Int)
//    private val openList = PriorityQueue<Node>()
//    private val closedList: MutableSet<Node> = mutableSetOf()
//
//    fun find(start: Location): Int {
//        openList.add(Node(start, 0, Direction.East, 0))
//
//        do {
//            val currentNode = openList.remove()
//            if(currentNode.location == end) return currentNode.heat
//            closedList.add(currentNode)
//            expandNode(currentNode)
//        } while (openList.isNotEmpty())
//        error("no path found")
//    }
//
//    private fun expandNode(currentNode: Node) {
//        val newDirections = Direction.values().toMutableList().let { directions ->
//            directions.remove(currentNode.direction.opposite())
//            if(currentNode.sameDirectionMoves >= 3) directions.remove(currentNode.direction)
//            directions.toList()
//        }
//
//        for (newDirection in newDirections) {
//            val newLocation = currentNode.location + newDirection.offset
//            val additionalHeat = grid.getHeat(newLocation) ?: continue
//            val newHeat = currentNode.heat + additionalHeat
//            val newSameDirectionMoves = if(currentNode.direction == newDirection) currentNode.sameDirectionMoves + 1 else 1
//
//            val successor = Node(
//                newLocation,
//                newHeat,
//                newDirection,
//                newSameDirectionMoves,
//            )
//            if(closedList.contains(successor)) continue
////            if(openList.contains(successor) && openList.find { })
//
//        }
//
//
//    }
//}
//class OwnPathFinder(private val grid: List<List<Int>>, start: State) {
//    private val end = (grid.size - 1) to (grid.first().size - 1)
//    private val priorityQueue =  PriorityQueue<State> { a, b ->
//        (a.heat * 3) + (end - a.location).sum() compareTo (b.heat * 3) + (end - b.location).sum()
////        if(heatCompare != 0) return@PriorityQueue heatCompare
////        (end - a.location).sum() compareTo (end - b.location).sum()
//    }
//
//    init {
//        priorityQueue.add(start)
//    }
//
//    private val cache: MutableMap<Pair<Location, Direction>, Pair<Int, Int>> = mutableMapOf()
//    fun findEnd(): State {
//        var counter = 0
//        while (priorityQueue.isNotEmpty()) {
//            val currentState = priorityQueue.remove()
//            val newDirections = Direction.values().toMutableList().let { directions ->
//                directions.remove(currentState.direction.opposite())
//                if(currentState.sameDirectionMoves >= 3) directions.remove(currentState.direction)
//                directions.toList()
//            }
//
//            for (newDirection in newDirections) {
//
//
//                val newLocation = currentState.location + newDirection.offset
//                val additionalHeat = grid.getHeat(newLocation) ?: continue
//                val newSameDirectionMoves = if(currentState.direction == newDirection) currentState.sameDirectionMoves +  1 else 1
//
//                val newState = State(
//                    newLocation,
//                    newDirection,
//                    currentState.heat + additionalHeat,
//                    newSameDirectionMoves
//                )
//                if(newLocation == end) {
//                    return newState
//                }
//
//                cache[currentState.location to currentState.direction] = currentState.heat to currentState.sameDirectionMoves
//                val value = cache[newState.location to newState.direction]
//                if(value != null && value.first < newState.heat && value.second <= newState.sameDirectionMoves) continue
//                priorityQueue.add(newState)
//            }
//        }
//
//        error("Did not find end")
//    }
//}
//
//class DirectPathFinder(private val grid: List<List<Int>>) {
//    private val end = (grid.size - 1) to (grid.first().size - 1)
//    private val cache: MutableMap<Location, Int> = mutableMapOf()
//    fun findAll() {
//        for(row in grid.indices) {
//            for(column in grid.first().indices) {
//                val x = findFrom(row to column)
//            }
//        }
//    }
//
//    //returns lowestHeat from that point
//    val endHeat = grid.getHeat(end)!!
//    private fun findFrom(start: Location): Int {
//        if(start == end) return endHeat
//        cache[start]?.let { return it }
//        return Direction.values().mapNotNull { direction ->
//            val newLocation = start + direction.offset
//            val newHeat = grid.getHeat(newLocation) ?: return@mapNotNull null
//            val cachedHeat = cache[newLocation]
//            if(cachedHeat != null && cachedHeat <= newHeat) return@mapNotNull cachedHeat
//
//            val totalHeat = newHeat + findFrom(newLocation)
//            cache[newLocation] = totalHeat
//            totalHeat
//        }.min()
//    }
//}

fun List<List<Int>>.getHeat(location: Location) = this.getOrNull(location.first)?.getOrNull(location.second)

typealias Location = Pair<Int, Int>
enum class Direction(val offset: Location) {
    North(-1 to 0),
    East(0 to 1),
    South(1 to 0),
    West(0 to -1);

    fun opposite() = when(this) {
        North -> South
        East -> West
        South -> North
        West -> East
    }
}

fun Location.sum() = first + second
operator fun Location.minus(other: Location) = first - other.first to second - other.second
operator fun Location.plus(other: Location) = first + other.first to second + other.second