import java.lang.StringBuilder

fun main() {
    val schematic = Schematic(readInput("input"))

    val part1 = schematic.allPartNumbers()
        .sumOf {
            it.number.toLong()
        }
    println("Part 1: $part1")

    val part2 = schematic.calcRatio()
    println("Part 2: $part2")
}

data class NumberLocation(val row: Int, val col: Int, val length: Int, val number: Int)

class Schematic(val grid: List<String>) {
    fun allPartNumbers(): List<NumberLocation> {
        return findNumbers()
            .filter{ loc ->
                for (row in loc.row - 1..loc.row + 1) {
                    for (col in loc.col - 1..loc.col + loc.length) {
                        if (row == loc.row && (loc.col..<loc.col + loc.length).contains(col)) continue
                        if (row < 0 || row >= grid.size) continue
                        if (col < 0 || col >= grid[row].length) continue
                        if (!grid[row][col].isDigit() && grid[row][col] != '.') {
                            return@filter true
                        }
                    }
                }
                false
            }
    }

    fun calcRatio(): Long {
        return this.findGears().map<Pair<Int,Int>, Long> { (row, col) ->
            val adjNumbers = mutableSetOf<Int>()
            for (i in row - 1..row + 1) {
                for (j in col - 1..col + 1) {
                    if (i == row && j == col) continue
                    if (i < 0 || i >= grid.size) continue
                    if (j < 0 || j >= grid[i].length) continue
                    if (grid[i][j].isDigit()) {
                        adjNumbers.add(assembleNumber(i, j))
                        if (adjNumbers.size > 2) {
                            return@map 0
                        }
                    }
                }
            }
            if (adjNumbers.size == 2) {
                return@map adjNumbers.first().toLong() * adjNumbers.last().toLong()
            }
            return@map 0L
        }.sum()
    }

    private fun findGears(): List<Pair<Int, Int>> {
        val gears = mutableListOf<Pair<Int, Int>>()
        grid.forEachIndexed { idx, line ->
            for (i in line.indices) {
                if (line[i] == '*') {
                    gears.add(Pair(idx, i))
                }
            }
        }
        return gears
    }

    private fun assembleNumber(row: Int, col: Int): Int {
        val number = StringBuilder()
        var start = col
        while (start >= 0 && grid[row][start].isDigit()) {
            start--
        }
        for (i in start+1 ..<grid[row].length) {
            if (!grid[row][i].isDigit()) {
                break
            }
            number.append(grid[row][i])
        }
        return number.toString().toInt()
    }

    private fun findNumbers(): List<NumberLocation> {
        val locations = mutableListOf<NumberLocation>()
        val addNumber = { number: StringBuilder, lineNumber: Int, col: Int ->
            if (number.isNotEmpty()) {
                locations.add(
                    NumberLocation(lineNumber, col, number.length, number.toString().toInt())
                )
                number.clear()
            }
        }
        grid.forEachIndexed { idx, line ->
            val number = StringBuilder()
            for (i in line.indices) {
                if (line[i].isDigit()) {
                    number.append(line[i])
                } else {
                    addNumber(number, idx, i - number.length)
                }
            }
            addNumber(number, idx, line.length - number.length)
        }
        return locations
    }
}
