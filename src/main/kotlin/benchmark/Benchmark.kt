package benchmark

import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import org.openjdk.jmh.annotations.*
import readResourceLines
import java.util.concurrent.*
import kotlin.math.*

@Fork(1)
@Warmup(iterations = 1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS)
open class TestBenchmark {
    private var input10: List<String> = readResourceLines("Day10.txt")
    private var input7: List<String> = readResourceLines("Day07.txt")

    @Setup
    open fun setUp() {
    }

//    @Benchmark
//    open fun day1Part2Benchmark(): Int {
//        return solvePart2(input)
//    }
//
//    @Benchmark
//    open fun day1Part2Version2Benchmark(): Int {
//        return solvePart2Version2(input)
//    }
//
//    @Benchmark
//    open fun day1Part2FreezyVersionBenchmark(): Int {
//        return solvePart2FreezyVersion(input)
//    }

    @Benchmark
    open fun day10Part2(): Int {
        return day10.solvePart2(input10)
    }

    @Benchmark
    open fun day7Part1(): Long {
        return day07.solve(input7)
    }
}