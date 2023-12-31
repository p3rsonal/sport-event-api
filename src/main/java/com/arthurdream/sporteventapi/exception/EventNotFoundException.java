package com.arthurdream.sporteventapi.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long id) {
        super("Event with ID " + id + " not found.");
    }
}
