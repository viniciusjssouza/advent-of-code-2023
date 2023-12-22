import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

data class Tree(val maps: List<TreeSet<IntervalMap>>) {
    fun findLowestLocation(seeds: List<Interval>): Long {
        return seeds.minOf { seed -> findLowestLocation(seed, 0) }
    }

    private fun findLowestLocation(curr: Interval, level: Int): Long {
        if (curr.length <= 0) return Long.MAX_VALUE
        if (level == maps.size) return curr.start

        val currLevelMap = maps[level]
        val currAsMap = IntervalMap(level, curr.start, curr.start, curr.length)
        val prev = currLevelMap.floor(currAsMap)
        val next = currLevelMap.ceiling(currAsMap)

        val (overlap, left, right) = if (prev != null && curr.start < prev.sourceEnd) {
            val newEnd = minOf(prev.sourceEnd, curr.end)
            Triple(
                Interval(start = prev.dest + (curr.start-prev.source), length = newEnd - curr.start),
                null,
                Interval(start = newEnd, length = curr.end - newEnd),
            )
        } else if (next != null && curr.end > next.source) {
            val newEnd = minOf(next.sourceEnd, curr.end)
            Triple(
                Interval(start = next.dest, length = newEnd - next.source),
                Interval(start = curr.start, length = next.source - curr.start),
                Interval(start = next.sourceEnd, length = curr.end - next.sourceEnd)
            )
        } else {
            Triple(
                Interval(start = curr.start, length = curr.length), null, null
            )
        }

        return minOf(
            findLowestLocation(overlap, level+1),
            if (left != null) findLowestLocation(left, level) else Long.MAX_VALUE,
            if (right != null) findLowestLocation(right, level) else Long.MAX_VALUE
        )
    }
}

data class Interval(val start: Long, val length: Long) {
    val end: Long get() = start + length
}

data class IntervalMap(
    val level: Int, val source: Long, val dest: Long, val length: Long
) : Comparable<IntervalMap> {
    companion object {
        fun parse(level: Int, line: String): IntervalMap {
            val (dest, source, length) = line.split(Regex("\\s+"))
            return IntervalMap(level, source.toLong(), dest.toLong(), length.toLong())
        }
    }

    val sourceEnd: Long get() = source + length
    val sourceRange: LongRange get() = source..source + length
    val destEnd: Long get() = dest + length
    val destRange: LongRange = dest..dest + length

    override fun compareTo(other: IntervalMap): Int {
        val beginCmp = this.source.compareTo(other.source)
        if (beginCmp != 0) return beginCmp
        return this.length.compareTo(other.length)
    }
}

fun main() {
    val execTime = measureTime {
        val input = readInput("input")
        val linesIter = input.iterator()
        val seedsLine = readSeedLine(linesIter.next())

        val graph = readGraph(linesIter)
        val part1 = graph.findLowestLocation(seedsAsOneElementInterval(seedsLine))
        println(part1)

        val part2 = graph.findLowestLocation(seedsAsIntervals(seedsLine))
        println(part2)
    }
    println(execTime)
}

fun readGraph(linesIter: Iterator<String>): Tree {
    val maps = ArrayList<TreeSet<IntervalMap>>()
    var level = 0
    while (linesIter.hasNext()) {
        val line = linesIter.next()
        if (line.trim().endsWith("map:")) {
            val map = readMap(level, linesIter)
            maps.add(map)
            level++
        }
    }
    return Tree(maps)
}

fun readMap(level: Int, linesIter: Iterator<String>): TreeSet<IntervalMap> {
    val map = TreeSet<IntervalMap>()
    while (linesIter.hasNext()) {
        val line = linesIter.next()
        if (line.trim().isEmpty()) break
        map.add(IntervalMap.parse(level, line))
    }
    return map
}

fun seedsAsIntervals(seeds: List<Long>) = (seeds.indices step 2).map {
    Interval(
        start = seeds[it],
        length = seeds[it + 1],
    )
}

fun seedsAsOneElementInterval(seeds: List<Long>) = seeds.map {
    Interval(start = it, length = 1)
}

fun readSeedLine(firstLine: String): List<Long> {
    return Regex("seeds:\\s+(.*)").find(firstLine)?.groups?.get(1)?.value?.split(Regex("\\s+"))
        ?.map {
            it.toLong()
        } ?: throw RuntimeException("Invalid input")
}

