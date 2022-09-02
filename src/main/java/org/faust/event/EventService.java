package org.faust.event;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(String message) {
        Event event = new Event(message);
        eventRepository.save(event);
    }

    public Iterable<Event> getEvents() {
        return eventRepository.findAll();
    }
}
