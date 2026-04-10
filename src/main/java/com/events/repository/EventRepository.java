package com.events.repository;

import com.events.model.Event;
import com.events.model.Event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatusOrderByDateAsc(EventStatus status);
    List<Event> findAllByOrderByDateDesc();
}
