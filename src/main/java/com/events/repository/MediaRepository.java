package com.events.repository;

import com.events.model.EventMedia;
import com.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<EventMedia, Long> {
    List<EventMedia> findByEventOrderByUploadedAtDesc(Event event);
}
