@file:Suppress("DuplicatedCode")

package day17

import readResourceLines
import java.util.*

fun main() {
    val input = readResourceLines("Day17.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val grid = input.map { line -> line.map { it.digitToInt() } }
    val pathfinder = PathFinderPart2(grid)
    return pathfinder.aStar(0 to 0).first().heat
}

class PathFinderPart2(private val grid: List<List<Int>>) {
    private val end = (grid.size - 1) to (grid.first().size - 1)
    data class Node(val key: Key, val heat: Int, var predecessor: Node?)
    data class Key(val location: Location, val direction: Direction, val sameDirectionMoves: Int)

    private val openList = PriorityQueue<Node> { a, b ->
        return@PriorityQueue a.heat compareTo b.heat
    }

    private val closedList: MutableSet<Key> = mutableSetOf()

    fun aStar(start: Location): List<Node> {
        openList.add(Node(Key(start, Direction.East, 0), 0, null))
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

    private val maxMoveLimit = 10
    private val minMoveLimit = 4
    private fun expandNode(currentNode: Node)  {
        val newDirections = Direction.values().toMutableList().let { directions ->
            directions.remove(currentNode.key.direction.opposite())
            if(currentNode.key.sameDirectionMoves >= maxMoveLimit) directions.remove(currentNode.key.direction)
            directions.toList()
        }

        outer@ for(newDirection in newDirections) {
            val moveCount = if(newDirection != currentNode.key.direction) minMoveLimit else 1
            //don't need to check if this in bound as the next loop will check every offset in between
            val successorLocation = currentNode.key.location + (newDirection.offset * moveCount)

            var newAdditionalHeat = 0
            for (factorOffset in 1..moveCount) {
                val newLocation = currentNode.key.location + (newDirection.offset * factorOffset)
                newAdditionalHeat += grid.getHeat(newLocation) ?: continue@outer
            }

            val newHeat = currentNode.heat + newAdditionalHeat
            val newSameDirectionMoves = if(moveCount == 1) currentNode.key.sameDirectionMoves + moveCount else moveCount

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

operator fun Location.times(factor: Int): Location = first * factor to second * factor