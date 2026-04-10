package com.events.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_media")
public class EventMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

    // Constructors
    public EventMedia() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Event event;
        private String fileName;
        private String filePath;
        private MediaType mediaType;

        public Builder event(Event e) { this.event = e; return this; }
        public Builder fileName(String n) { this.fileName = n; return this; }
        public Builder filePath(String p) { this.filePath = p; return this; }
        public Builder mediaType(MediaType t) { this.mediaType = t; return this; }

        public EventMedia build() {
            EventMedia m = new EventMedia();
            m.event = event;
            m.fileName = fileName;
            m.filePath = filePath;
            m.mediaType = mediaType;
            return m;
        }
    }
}
