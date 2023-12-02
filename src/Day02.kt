fun main() {
    val numberOfCubes = Cubes(12,13,14)
    val inputs = readInput("input").map { GameInput.fromString(it) }
    val result = inputs.sumOf { game ->
        if (game.isPossible(numberOfCubes)) game.id.toLong() else 0L
    }
    println("Part 1: $result")

    val result2 = inputs.sumOf { game ->
        game.minPossibleCube().power()
    }
    println("Part 2: $result2")
}

data class Cubes(val red: Int, val green: Int, val blue: Int) {
    fun power() = red * green * blue
}

data class GameInput(val id: Int, val plays: List<Cubes>) {

    fun isPossible(numberOfCubes: Cubes): Boolean {
        return plays.all { play ->
            play.red <= numberOfCubes.red &&
            play.green <= numberOfCubes.green &&
            play.blue <= numberOfCubes.blue
         }
    }

    fun minPossibleCube(): Cubes {
       return Cubes(
           plays.maxOfOrNull { it.red } ?: 0,
           plays.maxOfOrNull { it.green } ?: 0,
           plays.maxOfOrNull { it.blue } ?: 0
        )
    }

    companion object {
        fun fromString(input: String): GameInput {
            val gameId =  extractGameId(input)
            val plays = extractPlays(input)
            return GameInput(gameId, plays)
        }

        private fun extractPlays(input: String): List<Cubes> {
            return input.split(":")[1].split(";").map(::extractCube)
        }

        private fun extractCube(playStr: String): Cubes {
            var r = 0
            var g = 0
            var b = 0
            playStr.trim().split(",").map(String::trim).forEach{
                if (it.contains("red")) {
                    r = it.split(" ")[0].toInt()
                }
                else if (it.contains("green")) {
                    g = it.split(" ")[0].toInt()
                }
                else if (it.contains("blue")) {
                    b = it.split(" ")[0].toInt()
                }
            }
            return Cubes(r, g, b)
        }

        private fun extractGameId(input: String): Int {
            val match = Regex("Game (\\d+):.*").find(input)
            return match?.groupValues?.get(1)?.toInt() ?: throw Exception("No game id found in $input")
        }
    }
}