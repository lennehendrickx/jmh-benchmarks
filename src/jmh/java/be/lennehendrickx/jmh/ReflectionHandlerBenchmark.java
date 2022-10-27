package be.lennehendrickx.jmh;

import be.lennehendrickx.reflection_handler.AggregateRootWithCache;
import be.lennehendrickx.reflection_handler.AggregateRootWithoutCache;
import be.lennehendrickx.reflection_handler.Event;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;

@State(Scope.Benchmark)
public class ReflectionHandlerBenchmark {
    @Benchmark
    public void aggregateRootWithoutCache(Blackhole bh) {
        bh.consume(new AggregateRootWithoutCache(List.of(new Event.Cleared(), new Event.Deleted())));
    }

    @Benchmark
    public void aggregateRootWithCache(Blackhole bh) {
        bh.consume(new AggregateRootWithCache(List.of(new Event.Cleared(), new Event.Deleted())));
    }

}