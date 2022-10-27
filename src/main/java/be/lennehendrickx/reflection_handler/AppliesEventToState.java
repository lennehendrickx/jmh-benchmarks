package be.lennehendrickx.reflection_handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods in your aggregate root that will take an event and changes state of your
 * aggregate. The AggregateRoot superclass will call these methods automatically when loading an
 * aggregate from database, or when adding a new event via ApplyEvent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppliesEventToState {}
