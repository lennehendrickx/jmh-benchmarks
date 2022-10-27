package be.lennehendrickx.reflection_handler;

import be.lennehendrickx.reflection_handler.Event.Cleared;
import be.lennehendrickx.reflection_handler.Event.Deleted;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

public class AggregateRootWithoutCache {

    public AggregateRootWithoutCache(List<Event> events) {
        events.forEach(this::setState);
    }

    private void setState(Event event) {
        Collection<Method> methods =
                concat(getAggregateMethods(getClass()), stream(AggregateRootWithoutCache.class.getDeclaredMethods()))
                        .filter(t -> t.isAnnotationPresent(AppliesEventToState.class))
                        .filter(t -> t.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                        .toList();

        methods.forEach(
                m -> {
                    if (m.getParameterCount() != 1) {
                        throw new IllegalStateException(
                                "Methods with @"
                                        + AppliesEventToState.class.getSimpleName()
                                        + " can have only one parameter: "
                                        + m);
                    }
                    if (!Modifier.isPrivate(m.getModifiers())) {
                        throw new IllegalStateException(
                                "@"
                                        + AppliesEventToState.class.getSimpleName()
                                        + " can only be used on private methods: "
                                        + m);
                    }
                });

        methods.forEach(
                t -> {
                    try {
                        t.setAccessible(true);
                        t.invoke(this, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static Stream<Method> getAggregateMethods(Class<?> aggregateClass) {
        if (aggregateClass.equals(AggregateRootWithoutCache.class)) {
            return stream(aggregateClass.getDeclaredMethods());
        }

        return concat(
                getAggregateMethods(aggregateClass.getSuperclass()),
                stream(aggregateClass.getDeclaredMethods()));
    }

    @AppliesEventToState
    private void handleDelete(Deleted event) {
    }

    @AppliesEventToState
    private void handleClear(Cleared event) {
    }

}
