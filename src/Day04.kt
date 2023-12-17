import java.lang.StringBuilder

data class Card(val id: Int, val win: Set<Long>, val ownedNumbers: Set<Long>) {

    fun totalPoints(): Long {
        val numberOfIntersections = totalMatches()
        if (numberOfIntersections == 0) return 0
        return 1L shl (numberOfIntersections - 1)
    }

    fun totalMatches(): Int {
        return  win.intersect(ownedNumbers).size
    }
}

fun main() {
    val input = readInput("input").map(::parseCard)
    val part1 = input.sumOf { card ->
        card.totalPoints()
    }
    println("Part 1: $part1")

    val cardsCount = Array(input.size+1) { 1 }
    cardsCount[1] = 1
    val total = input.sumOf {card ->
        val totalMatches = card.totalMatches()
        for (i in 1..totalMatches) {
            cardsCount[card.id+i] += cardsCount[card.id]
        }
        cardsCount[card.id]
    }
    println("Part 2: $total")
}

fun parseCard(line: String): Card {
    val groups = Regex("Card\\s+(\\d+): (.*)\\|(.*)").find(line)?.groups ?: throw RuntimeException("Invalid input on line $line")
    return Card(
        id = groups[1]?.value?.toInt() ?: throw RuntimeException("Invalid input"),

        win = groups[2]?.value?.trim()?.splitToSequence(Regex("\\s+"))?.map {
            it.trim().toLong()
        }?.toSet() ?: throw RuntimeException("Invalid winning set"),

        ownedNumbers = groups[3]?.value?.trim()?.split(Regex("\\s+"))?.map {
            it.trim().toLong()
        }?.toSet() ?: throw RuntimeException("Invalid owned numbers")
    )
}

