package day15

import readResource
import kotlin.collections.ArrayDeque

fun main() {
    val input = readResource("Day15.txt")
    val solution = solvePart2(input)
    println(solution)
}

typealias Box = ArrayDeque<Lens>
data class Lens(val label: String, val focalLength: Int)

fun solvePart2(input: String): Int {
    val instructions = parse(input)
    val boxes = Array(256) { Box() }

    for(instruction in instructions) {
        val index = instruction.label.hash()
        when(instruction) {
            is Instruction.Remove -> {
                boxes[index].removeIf { lens -> lens.label == instruction.label }
            }
            is Instruction.Add -> {
                val newLensLabel = instruction.label
                val newLens = Lens(newLensLabel, instruction.focal)
                val lensExists = boxes[index].find { lens -> lens.label == newLensLabel } != null
                if(lensExists) {
                    boxes[index].replaceAll { lens -> if(lens.label == newLensLabel) newLens else lens }
                } else {
                    boxes[index].add(newLens)
                }
            }
        }
    }

    return boxes.mapIndexed { boxIndex, box -> box.mapIndexed { index, lens -> (boxIndex + 1) * (index + 1) * lens.focalLength }.sum() }.sum()
}

fun parse(input: String): List<Instruction> {
    return input.split(",").map { instruction ->
        if(instruction.last() == '-') {
            Instruction.Remove(instruction.dropLast(1))
        } else {
            Instruction.Add(instruction.dropLast(2), instruction.last().digitToInt())
        }
    }
}

sealed class Instruction {
    abstract val label: String

    data class Remove(override val label: String) : Instruction()
    data class Add(override val label: String, val focal: Int) : Instruction()
}
