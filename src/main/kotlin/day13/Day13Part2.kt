package day13

import readResourceLines

fun main() {
    val input = readResourceLines("Day13.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val patterns: List<MutablePattern> = parse(input).map { pattern -> pattern.map { it.toMutableList() } }
    val axis = patterns.mapIndexed { index, pattern ->
        val oldAxis = findAxis(pattern, null) ?: error("couldn't find old pattern")
        for (row in pattern.indices) {
            for (column in pattern.first().indices) {
                pattern[row][column] = !pattern[row][column]

                findAxis(pattern, oldAxis)?.let { newAxis ->
//                    pattern.print()
                    if(newAxis != oldAxis) return@mapIndexed newAxis
                }

                pattern[row][column] = !pattern[row][column]
            }
        }

        error("couldn't find flip: $index, oldAxis was: $oldAxis")
    }

    return axis.sumOf { it.first.multiplier * it.second }
}

fun findAxis(pattern: Pattern, oldAxis: Pair<MirrorAxis, Int>?): Pair<MirrorAxis, Int>? {
    var axis = MirrorAxis.Horizontal
    var axisIndex = pattern.findRowAxis(if(axis == oldAxis?.first) oldAxis.second else null)
    if(axisIndex != null) {
        val newAxis = axis to axisIndex
        if(newAxis != oldAxis) return newAxis
    }

    axis = MirrorAxis.Vertical
    val transposedPattern = pattern.transpose()
    axisIndex = transposedPattern.findRowAxis(if(axis == oldAxis?.first) oldAxis.second else null)
    axis to axisIndex
    if(axisIndex != null) {
        val newAxis = axis to axisIndex
        if(newAxis != oldAxis) return newAxis
    }

    return null
}

@Suppress("unused")
fun MutablePattern.print() {
    var output = "\n"
    this.map { row ->
        output += row.joinToString("") { if (it) "#" else "." }
        output += "\n"
    }
    println(output)
}

typealias MutablePattern = List<MutableList<Boolean>>