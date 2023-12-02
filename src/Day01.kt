val spellings = hashMapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "zero" to 0
)

data class CalibrationValue(val firstDigit: Int, val lastDigit: Int)

fun firstAndLastDigit(line: String, spellings: Map<String, Int>): CalibrationValue {
    var firstDigit: String? = null
    var firstDigitIdx: Int? = null
    var lastDigit: String? = null
    var lastDigitIdx: Int? = null

    for ((spelling, value) in spellings) {
        val firstIndexOf = line.indexOf(spelling)
        if (firstIndexOf >= 0 && (firstDigitIdx == null || firstIndexOf < firstDigitIdx)) {
            firstDigitIdx = firstIndexOf
            firstDigit = value.toString()
        }

        val lastIndexOf = line.lastIndexOf(spelling)
        if (lastIndexOf >= 0 && (lastDigitIdx == null || lastIndexOf > lastDigitIdx)) {
            lastDigitIdx = lastIndexOf
            lastDigit = value.toString()
        }
    }

    line.forEachIndexed { index, char ->
        if (char.isDigit()) {
            if (firstDigitIdx == null || index < firstDigitIdx!!) {
                firstDigitIdx = index
                firstDigit = char.toString()
            }
            if (lastDigitIdx == null || index > lastDigitIdx!!) {
                lastDigitIdx = index
                lastDigit = char.toString()
            }
        }
    }
    if (firstDigit != null && lastDigit != null) {
        return CalibrationValue(firstDigit!!.toInt(), lastDigit!!.toInt())
    }
    throw RuntimeException("No digit found in $line")
}

fun firstAndLastDigit(line: String): CalibrationValue {
    var firstDigit: String? = null
    var lastDigit: String? = null
    for (char in line) {
        if (char.isDigit()) {
            firstDigit = firstDigit ?: char.toString()
            lastDigit = char.toString()
        }
    }
    if (firstDigit != null && lastDigit != null) {
        return CalibrationValue(firstDigit.toInt(), lastDigit.toInt())
    }
    throw RuntimeException("No digit found in $line")
}

fun main() {
    val input = readInput("input")

    val part1Result = part1(input)
    println("Part 1: $part1Result")

    val part2Result = part2(input)
    println("Part 2: $part2Result")
}

private fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        val calibrationValue = firstAndLastDigit(line)
        calibrationValue.lastDigit + 10 * calibrationValue.firstDigit
    }
}

private fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        val calibrationValue = firstAndLastDigit(line, spellings)
        calibrationValue.lastDigit + 10 * calibrationValue.firstDigit
    }
}
