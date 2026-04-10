package com.events.service;

import com.events.model.Event;
import com.events.model.Registration;
import com.events.model.User;
import com.events.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration registerForEvent(User user, Event event) {
        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new IllegalStateException("User is already registered for this event.");
        }
        Registration registration = Registration.builder()
                .user(user)
                .event(event)
                .build();
        return registrationRepository.save(registration);
    }

    public void cancelRegistration(User user, Event event) {
        Registration reg = registrationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("Registration not found."));
        registrationRepository.delete(reg);
    }

    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsByEvent(Event event) {
        return registrationRepository.findByEvent(event);
    }

    @Transactional(readOnly = true)
    public boolean isRegistered(User user, Event event) {
        return registrationRepository.existsByUserAndEvent(user, event);
    }

    @Transactional(readOnly = true)
    public long countRegistrations(Event event) {
        return registrationRepository.countByEvent(event);
    }
}
