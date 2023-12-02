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

data class Digit(val value: Int, val index: Int)

data class CalibrationValue(val firstDigit: Digit, val lastDigit: Digit)

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
        if (calibrationValue == null) {
            throw Exception("No digits found in $line")
        } else
        calibrationValue.lastDigit.value + 10 * calibrationValue.firstDigit.value
    }
}

private fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        val calibrationValueOnlyDigits = firstAndLastDigit(line)
        val calibrationValueOnlySpelling = firstAndLastDigit(line, spellings)

        var firstDigit = calibrationValueOnlyDigits?.firstDigit?.value
        calibrationValueOnlySpelling?.let {
            if (firstDigit == null || calibrationValueOnlyDigits!!.firstDigit.index > it.firstDigit.index) {
                firstDigit = calibrationValueOnlySpelling.firstDigit.value
            }
        }

        var lastDigit = calibrationValueOnlyDigits?.lastDigit?.value
        calibrationValueOnlySpelling?.let {
            if (lastDigit == null || calibrationValueOnlyDigits!!.lastDigit.index < it.lastDigit.index) {
                lastDigit = calibrationValueOnlySpelling.lastDigit.value
            }
        }
        if (firstDigit == null || lastDigit == null) {
            throw Exception("No digits found in $line")
        }
        lastDigit!! + 10 * firstDigit!!
    }
}

fun firstAndLastDigit(line: String, spellings: Map<String, Int>): CalibrationValue? {
    var firstDigit: Digit? = null
    var lastDigit: Digit? = null

    for ((spelling, value) in spellings) {
        var foundIndex = line.indexOf(spelling)
        if (foundIndex >= 0 && (firstDigit == null || firstDigit.index > foundIndex)) {
            firstDigit = Digit(value, foundIndex)
        }

        foundIndex = line.lastIndexOf(spelling)
        if (foundIndex >= 0 && (lastDigit == null || lastDigit.index < foundIndex)) {
            lastDigit = Digit(value, foundIndex)
        }
    }
    if (firstDigit != null) {
        return CalibrationValue(firstDigit, lastDigit!!)
    }
    return null
}

fun firstAndLastDigit(line: String): CalibrationValue? {
    var firstDigit: Digit? = null
    var lastDigit: Digit? = null
    line.forEachIndexed { index, c ->
        if (c.isDigit()) {
            firstDigit = firstDigit ?: Digit(c.digitToInt(), index)
            lastDigit = Digit(c.digitToInt(), index)
        }
    }
    if (firstDigit != null) {
        return CalibrationValue(firstDigit!!, lastDigit!!)
    }
    return null
}
