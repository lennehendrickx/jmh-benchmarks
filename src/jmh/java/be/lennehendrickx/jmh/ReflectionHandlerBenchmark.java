package be.lennehendrickx.jmh;

import be.lennehendrickx.reflection_handler.AggregateRootWithCache;
import be.lennehendrickx.reflection_handler.AggregateRootWithoutCache;
import be.lennehendrickx.reflection_handler.Event;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.stream.IntStream;

public class ReflectionHandlerBenchmark {

    @State(Scope.Benchmark)
    public static class ReflectionHandlerState {

        @Param({ "1", "10", "100", "1000" })
        public int numberOfEvents;
        List<Event> events;

        @Setup
        public void setup() {
             events = IntStream
                    .range(0, numberOfEvents)
                    .mapToObj(iteration -> (Event) new Event.Deleted())
                    .toList();

        }

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