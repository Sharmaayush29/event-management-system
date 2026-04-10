package com.events.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String category;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String agenda;

    @Column(nullable = false)
    private Integer maxCapacity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventMedia> media = new ArrayList<>();

    public enum EventStatus {
        UPCOMING, COMPLETED
    }

    // Constructors
    public Event() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAgenda() { return agenda; }
    public void setAgenda(String agenda) { this.agenda = agenda; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }

    public List<EventMedia> getMedia() { return media; }
    public void setMedia(List<EventMedia> media) { this.media = media; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private LocalDate date;
        private String location;
        private String category;
        private String agenda;
        private Integer maxCapacity;
        private EventStatus status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String t) { this.title = t; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder date(LocalDate d) { this.date = d; return this; }
        public Builder location(String l) { this.location = l; return this; }
        public Builder category(String c) { this.category = c; return this; }
        public Builder agenda(String a) { this.agenda = a; return this; }
        public Builder maxCapacity(Integer m) { this.maxCapacity = m; return this; }
        public Builder status(EventStatus s) { this.status = s; return this; }

        public Event build() {
            Event e = new Event();
            e.id = id; e.title = title; e.description = description;
            e.date = date; e.location = location; 
            e.category = category; e.agenda = agenda; e.maxCapacity = maxCapacity;
            e.status = status;
            return e;
        }
    }
}
