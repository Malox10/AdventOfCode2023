package day02

import readResourceLines

fun main() {
    val input = readResourceLines("Day02.txt")
    val solution = solvePart2(input)
    println(solution)
}

fun solvePart2(input: List<String>): Int {
    val games = parse(input)
    return games.sumOf { game ->
        Color.values().map { color ->
            game.pulls.filter { it.color == color }.maxBy { it.amount }
        }.map { it.amount }.reduce(Int::times)
    }
}

//Game 82: 12 blue, 4 red, 4 green; 7 red, 4 blue; 3 green, 10 red, 3 blue; 6 blue, 13 red; 4 blue, 5 red, 1 green
fun parse(input: List<String>): List<Game> {
    val colorMap = Color.values().associateBy { it.identifier }

    val games = input.map { line ->
        val (gameString, roundString) = line.split(":").map { it.trim() }
        val (_, idString) = gameString.split(" ").map { it.trim() }
        val id = idString.toInt()

        val rounds = roundString.split(";").map { it.trim() }
        val pulls = rounds.flatMap { round ->
            val pullStrings = round.split(",").map { it.trim() }
            pullStrings.map { pull ->
                val (amountString, colorString) = pull.split(" ")
                Pull(amountString.toInt(), colorMap[colorString]!!)
            }
        }
        Game(id, pulls)
    }
    return games
}

data class Pull(val amount: Int, val color: Color)
data class Game(val id: Int, val pulls: List<Pull>)

enum class Color(val identifier: String) {
    Red("red"),
    Green("green"),
    Blue("blue"),
}
