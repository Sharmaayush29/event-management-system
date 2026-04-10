package com.events.service;

import com.events.model.Event;
import com.events.model.Event.EventStatus;
import com.events.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(String title, String description, LocalDate date, String location, String category, String agenda, Integer maxCapacity) {
        Event event = Event.builder()
                .title(title)
                .description(description)
                .date(date)
                .location(location)
                .category(category)
                .agenda(agenda)
                .maxCapacity(maxCapacity)
                .status(EventStatus.UPCOMING)
                .build();
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByStatusOrderByDateAsc(EventStatus.UPCOMING);
    }

    @Transactional(readOnly = true)
    public List<Event> getCompletedEvents() {
        return eventRepository.findByStatusOrderByDateAsc(EventStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public Event markAsCompleted(Long eventId) {
        Event event = getEventById(eventId);
        event.setStatus(EventStatus.COMPLETED);
        return eventRepository.save(event);
    }

    public Event updateEvent(Long eventId, String title, String description, LocalDate date, String location, String category, String agenda, Integer maxCapacity) {
        Event event = getEventById(eventId);
        event.setTitle(title);
        event.setDescription(description);
        event.setDate(date);
        event.setLocation(location);
        event.setCategory(category);
        event.setAgenda(agenda);
        event.setMaxCapacity(maxCapacity);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
