import java.math.BigInteger

data class Place(val id: String, val left: String, val right: String) {
    fun isStart() = this.id.endsWith("A")
    fun isEnd() = this.id.endsWith("Z")
}

data class Cycle(val startAtStep: Int, val length: Int)

fun BigInteger.lcm(another: BigInteger): BigInteger {
    return this.divide(this.gcd(another)).multiply(another)
}

data class WastelandMap(val places: Map<String, Place>) {

    class Traversal(
        start: Place,
        private val instructions: String,
        private val places: Map<String, Place>
    ) {
        private var steps = 0
        private var current: String = start.id
        fun getSteps() = this.steps

        fun stepToNext(): Place {
            val idx = steps % instructions.length
            val place = places[current] ?: throw RuntimeException("Invalid place $current")
            steps += 1
            current = if (instructions[idx] == 'L') {
                place.left
            } else {
                place.right
            }
            return places[current]!!
        }
    }

    fun traverse(start: Place, instructions: String): Int {
        val traversal = Traversal(start, instructions, this.places)
        var current = start.id
        while (current != "ZZZ") {
            current = traversal.stepToNext().id
        }
        return traversal.getSteps()
    }

    fun collectCycleInfo(start: Place, instructions: String): Cycle {
        val visits = mutableMapOf<String, Int>()
        val traversal = Traversal(start, instructions, this.places)
        var curr = start
        while (true) {
            if (curr.isEnd()) {
                if (visits.containsKey(curr.id)) {
                    return Cycle(
                        startAtStep = visits[curr.id]!!,
                        length = traversal.getSteps() - visits[curr.id]!!
                    )
                }
            }
            visits[curr.id] = traversal.getSteps()
            curr = traversal.stepToNext()
        }
    }
}

fun main() {
    val lines = readInput("input")
    val wastelandMap = WastelandMap(readPlaces(lines.drop(1)))
    val instructions = lines[0].trim()

    val start = wastelandMap.places.values.first { it.id == "AAA" }
    val part1 = wastelandMap.traverse(start, instructions)
    println(part1)

    val lcm = BigInteger.ONE
    val allStartingPlaces = wastelandMap.places.values.filter { it.isStart() }
    val part2 = allStartingPlaces.map {
        val cycle = wastelandMap.collectCycleInfo(it, instructions)
        println(cycle)
        cycle
        // For all cycles, the initial offset is equal to the length. This way, we can reduce the problem
        // to calculating the LCM of all cycle lengths
    }.fold(lcm) { curr, cycle ->
        curr.lcm(BigInteger.valueOf(cycle.length.toLong()))
    }
    println("Part2: $part2")
}

fun readPlaces(lines: List<String>): Map<String, Place> {
    return lines.filterNot { it.isEmpty() }.map {
        val match = Regex("(\\w+)\\s+=\\s+\\((\\w+),\\s+(\\w+)\\)").find(it)
        Place(
            id = match?.groupValues?.get(1)
                ?: throw RuntimeException("Invalid input on line '$it'"),
            left = match.groupValues[2],
            right = match.groupValues[3],
        )
    }.fold(mutableMapOf()) { acc, place ->
        acc[place.id] = place
        acc
    }
}
