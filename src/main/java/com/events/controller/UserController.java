package com.events.controller;

import com.events.model.Event;
import com.events.model.Registration;
import com.events.model.User;
import com.events.service.EventService;
import com.events.service.MediaService;
import com.events.service.RegistrationService;
import com.events.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final MediaService mediaService;

    public UserController(UserService userService, EventService eventService,
                          RegistrationService registrationService, MediaService mediaService) {
        this.userService = userService;
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.mediaService = mediaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/user/dashboard";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Registration> myRegistrations = registrationService.getRegistrationsByUser(user);
        List<Event> upcomingEvents = eventService.getUpcomingEvents();

        model.addAttribute("user", user);
        model.addAttribute("myRegistrations", myRegistrations);
        model.addAttribute("upcomingCount", upcomingEvents.size());
        model.addAttribute("registeredCount", myRegistrations.size());
        return "user/dashboard";
    }

    @GetMapping("/events/upcoming")
    public String upcomingEvents(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Event> events = eventService.getUpcomingEvents();

        List<Long> registeredEventIds = registrationService.getRegistrationsByUser(user)
                .stream()
                .map(r -> r.getEvent().getId())
                .collect(Collectors.toList());

        model.addAttribute("events", events);
        model.addAttribute("registeredEventIds", registeredEventIds);
        model.addAttribute("user", user);
        return "user/upcoming-events";
    }

    @PostMapping("/events/{eventId}/register")
    public String registerForEvent(@PathVariable("eventId") Long eventId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            Event event = eventService.getEventById(eventId);
            registrationService.registerForEvent(user, event);
            redirectAttributes.addFlashAttribute("successMsg", "Successfully registered for: " + event.getTitle());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/events/upcoming";
    }

    @PostMapping("/events/{eventId}/cancel")
    public String cancelRegistration(@PathVariable("eventId") Long eventId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            Event event = eventService.getEventById(eventId);
            registrationService.cancelRegistration(user, event);
            redirectAttributes.addFlashAttribute("successMsg", "Registration cancelled for: " + event.getTitle());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/events/upcoming";
    }

    @GetMapping("/events/past")
    public String pastEvents(Model model) {
        List<Event> completedEvents = eventService.getCompletedEvents();
        model.addAttribute("events", completedEvents);
        return "user/past-events";
    }

    @GetMapping("/events/{eventId}/gallery")
    public String eventGallery(@PathVariable("eventId") Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        model.addAttribute("photos", mediaService.getMediaByEvent(event));
        return "user/gallery";
    }
}
