package be.lennehendrickx.reflection_handler;

public interface Event {

    record Deleted() implements Event {}

    record Cleared() implements Event {}

}
