package be.lennehendrickx.jmh;

import be.lennehendrickx.reflection_handler.AggregateRootWithCache;
import be.lennehendrickx.reflection_handler.AggregateRootWithoutCache;
import be.lennehendrickx.reflection_handler.Event;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;

public class ReflectionHandlerBenchmark {

    @State(Scope.Benchmark)
    public static class ReflectionHandlerState {
        List<Event> events = List.of(new Event.Cleared(), new Event.Deleted());
    }

    @Benchmark
    public void aggregateRootWithoutCache(Blackhole bh, ReflectionHandlerState state) {
        bh.consume(new AggregateRootWithoutCache(state.events));
    }

    @Benchmark
    public void aggregateRootWithCache(Blackhole bh, ReflectionHandlerState state) {
        bh.consume(new AggregateRootWithCache(state.events));
    }
}