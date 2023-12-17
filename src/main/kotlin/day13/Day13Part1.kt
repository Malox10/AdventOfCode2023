package day13

import readResourceLines

fun main() {
    val input = readResourceLines("Day13.txt")
    val solution = solve(input)
    println(solution)
}

fun solve(input: List<String>): Int {
    val patterns = parse(input)
    val axis = patterns.map { pattern ->
        var axis = MirrorAxis.Horizontal
        var axisIndex = pattern.findRowAxis()
        if(axisIndex != null) return@map axis to axisIndex

        axis = MirrorAxis.Vertical
        val transposedPattern = pattern.transpose()
        axisIndex = transposedPattern.findRowAxis() ?: error("every pattern has an axis")
        axis to axisIndex
    }

    return axis.sumOf { it.first.multiplier * it.second }
}

fun Pattern.findRowAxis(oldMirrorIndex: Int? = null): Int? {
    for (mirrorIndex in (1 until this.size)) {
        var isAxis = true
        var distance = 0
        while(true) {
            val up = this.getOrNull(mirrorIndex - 1 - distance) ?: break
            val down = this.getOrNull(mirrorIndex + distance) ?: break
            if(up != down) {
                isAxis = false
                break
            }
            distance++
        }
        if(isAxis) {
            if(mirrorIndex != oldMirrorIndex) return mirrorIndex
        }
    }

    return null
}

fun parse(input: List<String>): List<Pattern> {
    val patternList = mutableListOf<Pattern>()
    val patternBuilder = mutableListOf<List<Boolean>>()

    for (line in input) {
        if(line.isEmpty()) {
            patternList.add(patternBuilder.toList())
            patternBuilder.clear()
            continue
        }
        patternBuilder.add(line.map { char -> char == '#' }.toList())
    }

    patternList.add(patternBuilder.toList())
    return patternList
}

typealias Pattern = List<List<Boolean>>

enum class MirrorAxis(val multiplier: Int) {
    Horizontal(100),
    Vertical(1)
}

fun<T> List<List<T>>.transpose(): List<List<T>> {
    return List(this.first().size) { columnIndex ->
        this.map { row -> row[columnIndex] }
    }
}
