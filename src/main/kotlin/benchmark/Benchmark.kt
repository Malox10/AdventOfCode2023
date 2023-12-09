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
    private var input: List<String> = readResourceLines("Day04.txt")

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
    open fun day4Part2(): Int {
        return day04.solvePart2(input)
    }
}