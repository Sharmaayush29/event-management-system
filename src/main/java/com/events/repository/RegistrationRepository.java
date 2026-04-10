package com.events.repository;

import com.events.model.Registration;
import com.events.model.User;
import com.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUser(User user);
    List<Registration> findByEvent(Event event);
    boolean existsByUserAndEvent(User user, Event event);
    Optional<Registration> findByUserAndEvent(User user, Event event);
    long countByEvent(Event event);
}
