package be.lennehendrickx.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class SampleBenchmark {
    @Benchmark
    public void fibClassic(Blackhole bh) {
        bh.consume("test");
    }

}