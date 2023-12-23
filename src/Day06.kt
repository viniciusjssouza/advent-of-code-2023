import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

data class Race(val time:Long, val distance:Long) {
    fun combinationsToWin(): Long {
        val a = -1
        val b = time
        val c = -distance
        val root1 = (-b + sqrt(b * b - 4.0 * a * c)) / (2.0 * a)
        val root2 = (-b - sqrt(b * b - 4.0 * a * c)) / (2.0 * a)

        val start = maxOf(0.0, root1 + 0.01)
        val end = maxOf(0.0, root2 - 0.01)
        return (floor(end) - ceil(start)).toLong() + 1
    }
}

fun main() {
    val lines = readInput("input")

    val races = readRaces(lines)
    val part1 = races.map { it.combinationsToWin() }.reduce(Long::times)
    println("Part 1: $part1")

    val part2Race = readSingleRace(lines)
    println("Part 2: ${part2Race.combinationsToWin()}")
}

fun readRaces(lines: List<String>): List<Race> {
    val times = Regex("Time:\\s+(.+)").find(lines[0])?.groupValues?.get(1)
        ?.split(Regex("\\s+"))?.map { it.toLong() } ?: throw Exception("Invalid first line")
    val distances = Regex("Distance:\\s+(.+)").find(lines[1])?.groupValues?.get(1)
        ?.split(Regex("\\s+"))?.map { it.toLong() } ?: throw Exception("Invalid second line")

    return times.mapIndexed { i, t ->
        Race(t, distances[i])
    }
}

fun readSingleRace(lines: List<String>): Race {
    val time = Regex("Time:\\s+(.+)").find(lines[0])?.groupValues?.get(1)
        ?.replace(" ","")?.toLong() ?: throw Exception("Invalid first line")
    val distance = Regex("Distance:\\s+(.+)").find(lines[1])?.groupValues?.get(1)
        ?.replace(" ", "")?.toLong()  ?: throw Exception("Invalid second line")

    return Race(time, distance)
}

