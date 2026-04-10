package com.events.controller;

import com.events.model.Event;
import com.events.model.EventMedia;
import com.events.model.Registration;
import com.events.service.EventService;
import com.events.service.MediaService;
import com.events.service.RegistrationService;
import com.events.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EventService eventService;
    private final UserService userService;
    private final RegistrationService registrationService;
    private final MediaService mediaService;

    public AdminController(EventService eventService, UserService userService,
                           RegistrationService registrationService, MediaService mediaService) {
        this.eventService = eventService;
        this.userService = userService;
        this.registrationService = registrationService;
        this.mediaService = mediaService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Event> allEvents = eventService.getAllEvents();
        long upcomingCount = allEvents.stream().filter(e -> e.getStatus() == Event.EventStatus.UPCOMING).count();
        long completedCount = allEvents.stream().filter(e -> e.getStatus() == Event.EventStatus.COMPLETED).count();
        long userCount = userService.getAllUsers().size();

        model.addAttribute("allEvents", allEvents);
        model.addAttribute("upcomingCount", upcomingCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("userCount", userCount);
        return "admin/dashboard";
    }

    @GetMapping("/events/create")
    public String showCreateEventForm() {
        return "admin/create-event";
    }

    @PostMapping("/events/create")
    public String createEvent(@RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               @RequestParam("location") String location,
                               @RequestParam("category") String category,
                               @RequestParam("agenda") String agenda,
                               @RequestParam("maxCapacity") Integer maxCapacity,
                               RedirectAttributes redirectAttributes) {
        try {
            eventService.createEvent(title, description, date, location, category, agenda, maxCapacity);
            redirectAttributes.addFlashAttribute("successMsg", "Event \"" + title + "\" created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to create event: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/{eventId}/edit")
    public String editEventForm(@PathVariable("eventId") Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        return "admin/edit-event";
    }

    @PostMapping("/events/{eventId}/edit")
    public String updateEvent(@PathVariable("eventId") Long eventId,
                               @RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               @RequestParam("location") String location,
                               @RequestParam("category") String category,
                               @RequestParam("agenda") String agenda,
                               @RequestParam("maxCapacity") Integer maxCapacity,
                               RedirectAttributes redirectAttributes) {
        try {
            eventService.updateEvent(eventId, title, description, date, location, category, agenda, maxCapacity);
            redirectAttributes.addFlashAttribute("successMsg", "Event updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to update event: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/events/{eventId}/complete")
    public String markCompleted(@PathVariable("eventId") Long eventId, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.markAsCompleted(eventId);
            redirectAttributes.addFlashAttribute("successMsg", "\"" + event.getTitle() + "\" marked as completed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/events/{eventId}/delete")
    public String deleteEvent(@PathVariable("eventId") Long eventId, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(eventId);
            redirectAttributes.addFlashAttribute("successMsg", "Event deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/{eventId}/photos")
    public String showUploadPhotosForm(@PathVariable("eventId") Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        model.addAttribute("photos", mediaService.getMediaByEvent(event));
        return "admin/upload-photos";
    }

    @GetMapping("/events/{eventId}/attendees")
    public String showAttendees(@PathVariable("eventId") Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        List<Registration> registrations = registrationService.getRegistrationsByEvent(event);
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrations);
        return "admin/attendees";
    }

    @PostMapping("/events/{eventId}/photos")
    public String uploadPhotos(@PathVariable("eventId") Long eventId,
                                @RequestParam("photos") List<MultipartFile> files,
                                RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(eventId);
        int uploaded = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    mediaService.saveMedia(event, file);
                    uploaded++;
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("errorMsg",
                            "Failed to upload " + file.getOriginalFilename() + ": " + e.getMessage());
                }
            }
        }
        if (uploaded > 0) {
            redirectAttributes.addFlashAttribute("successMsg", uploaded + " file(s) uploaded successfully!");
        }
        return "redirect:/admin/events/" + eventId + "/photos";
    }

    @PostMapping("/photos/{mediaId}/delete")
    public String deletePhoto(@PathVariable("mediaId") Long mediaId,
                               @RequestParam("eventId") Long eventId,
                               RedirectAttributes redirectAttributes) {
        try {
            mediaService.deleteMedia(mediaId);
            redirectAttributes.addFlashAttribute("successMsg", "Photo deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete photo: " + e.getMessage());
        }
        return "redirect:/admin/events/" + eventId + "/photos";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
}
