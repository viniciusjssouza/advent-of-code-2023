import org.junit.jupiter.api.Assertions.assertEquals

data class CamelCard(val value: Char, val withJoker: Boolean = false) : Comparable<CamelCard> {
    val strength: Int
        get() = when (value) {
            'A' -> 14
            'K' -> 13
            'Q' -> 12
            'J' -> if (withJoker) 1 else 11
            'T' -> 10
            else -> value.toString().toInt()
        }

    override fun compareTo(other: CamelCard): Int {
        return this.strength.compareTo(other.strength)
    }

    override fun toString(): String {
        return value.toString()
    }
}

data class Hand(val cards: List<CamelCard>, val bid: Long) : Comparable<Hand> {
    fun totalWinnings(rank: Int) = bid * rank

    fun calcStrength(): Long {
        val cardsCount = cardsCount()
        val jokersCount = cards.count { cards.first().withJoker && it.value == 'J' }
        when {
            jokersCount == 5 || cardsCount[5-jokersCount] == 1 -> { // five of a kind
                return 50
            }

            jokersCount == 4 || cardsCount[4-jokersCount] >= 1 -> { // four of a kind
                return 40
            }

            cardsCount[3] == 1 && cardsCount[2] == 1 -> { // full house
                return 30
            }

            cardsCount[2] == 2 && jokersCount == 1 -> { // full house
                return 30
            }

            jokersCount == 2 || cardsCount[3-jokersCount] == 1 -> { // three of a kind
                return 25
            }

            cardsCount[2] == 2 - jokersCount -> { // two pairs
                return 20
            }

            jokersCount == 1 || cardsCount[2] == 1 -> { // one pair
                return 10
            }

            cardsCount[1] == 5 -> { // high card
                return 5
            }

            else -> {
                return 0
            }
        }

    }

    private fun cardsCount(): Array<Int> {
        val withJoker = cards.first().withJoker
        val cardsCount = Array(6) { 0 }
        cards.toSet()
            .filterNot { withJoker && it.value == 'J' }
            .forEach { card ->
                val count = cards.count { it == card }
                cardsCount[count] += 1
            }
        return cardsCount
    }

    override fun compareTo(other: Hand): Int {
        val strengthCmp = this.calcStrength().compareTo(other.calcStrength())
        if (strengthCmp != 0) {
            return strengthCmp
        }
        this.cards.forEachIndexed { idx, thisCard ->
            val cardCmp = thisCard.compareTo(other.cards[idx])
            if (cardCmp != 0) {
                return cardCmp
            }
        }
        return 0
    }
}

fun main() {
    val lines = readInput("input")
    val hands = readHands(lines, withJoker = false)
    val part1 = calcTotalWinnings(hands)
    println("Part 1: $part1")

    val hands2 = readHands(lines, withJoker = true)
    val part2 = calcTotalWinnings(hands2)
    println("Part 2: $part2")

    runTests7()
}

private fun calcTotalWinnings(hands: List<Hand>) = hands.sorted().mapIndexed { idx, hand ->
    hand.totalWinnings(idx + 1)
}.sum()

fun readHands(lines: List<String>, withJoker: Boolean): List<Hand> {
    return lines.map {
        val parts = it.split(" ")
        Hand(cards = readCards(parts[0], withJoker), bid = parts[1].toLong())
    }
}

fun readCards(cards: String, withJoker: Boolean) = cards.map {
    CamelCard(it, withJoker)
}

// Just tests for the calcStrength function
fun runTests7() {
    assertHand(
        Hand(cards = readCards("22222", withJoker = true), bid = 1), expected = 50
    )
    assertHand(
        Hand(cards = readCards("2222J", withJoker = true), bid = 1), expected = 50
    )
    assertHand(
        Hand(cards = readCards("222JJ", withJoker = true), bid = 1), expected = 50
    )
    assertHand(
        Hand(cards = readCards("2JJJJ", withJoker = true), bid = 1), expected = 50
    )
    assertHand(
        Hand(cards = readCards("5555A", withJoker = true), bid = 1), expected = 40
    )
    assertHand(
        Hand(cards = readCards("5554J", withJoker = true), bid = 1), expected = 40
    )
    assertHand(
        Hand(cards = readCards("55JJ4", withJoker = true), bid = 1), expected = 40
    )
    assertHand(
        Hand(cards = readCards("A5JJJ", withJoker = true), bid = 1), expected = 40
    )
    assertHand(
        Hand(cards = readCards("AAKKK", withJoker = true), bid = 1), expected = 30
    )
    assertHand(
        Hand(cards = readCards("AAJKK", withJoker = true), bid = 1), expected = 30
    )
    assertHand(
        Hand(cards = readCards("KKKA1", withJoker = true), bid = 1), expected = 25
    )
    assertHand(
        Hand(cards = readCards("KKJA1", withJoker = true), bid = 1), expected = 25
    )
    assertHand(
        Hand(cards = readCards("KJJA1", withJoker = true), bid = 1), expected = 25
    )
    assertHand(
        Hand(cards = readCards("KK99A", withJoker = true), bid = 1), expected = 20
    )
    assertHand(
        Hand(cards = readCards("KK123", withJoker = true), bid = 1), expected = 10
    )
    assertHand(
        Hand(cards = readCards("KJ123", withJoker = true), bid = 1), expected = 10
    )
}

fun assertHand(h: Hand, expected: Long) {
    assertEquals(expected, h.calcStrength())
}