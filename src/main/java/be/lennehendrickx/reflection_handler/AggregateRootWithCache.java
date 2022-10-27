package be.lennehendrickx.reflection_handler;

import be.lennehendrickx.reflection_handler.AnnotatedHandlersPerType.AnnotatedHandlers;
import be.lennehendrickx.reflection_handler.Event.Cleared;
import be.lennehendrickx.reflection_handler.Event.Deleted;

import java.util.List;

public class AggregateRootWithCache {

    private static final AnnotatedHandlersPerType EVENT_HANDLERS_PER_TYPE =
            new AnnotatedHandlersPerType();

    private final AnnotatedHandlers eventHandlers = EVENT_HANDLERS_PER_TYPE.registerHandlersForType(getClass(), AppliesEventToState.class);

    public AggregateRootWithCache(List<Event> events) {
        events.forEach(this::setState);
    }

    private void setState(Event event) {
        eventHandlers.handle(this, event);
    }

    @AppliesEventToState
    private void handleDelete(Deleted event) {
    }

    @AppliesEventToState
    private void handleClear(Cleared event) {
    }

}
