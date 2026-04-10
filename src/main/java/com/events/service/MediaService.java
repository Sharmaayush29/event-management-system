package com.events.service;

import com.events.model.Event;
import com.events.model.EventMedia;
import com.events.model.MediaType;
import com.events.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MediaService {

    private final MediaRepository mediaRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public EventMedia saveMedia(Event event, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir, "events", event.getId().toString());
        Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();
        MediaType mediaType = MediaType.IMAGE;
        
        if (contentType != null && contentType.startsWith("video")) {
            mediaType = MediaType.VIDEO;
        }

        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : (mediaType == MediaType.VIDEO ? ".mp4" : ".jpg");
        
        String uniqueFileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        EventMedia media = EventMedia.builder()
                .event(event)
                .fileName(uniqueFileName)
                .filePath("/uploads/events/" + event.getId() + "/" + uniqueFileName)
                .mediaType(mediaType)
                .build();
        return mediaRepository.save(media);
    }

    @Transactional(readOnly = true)
    public List<EventMedia> getMediaByEvent(Event event) {
        return mediaRepository.findByEventOrderByUploadedAtDesc(event);
    }

    public void deleteMedia(Long mediaId) throws IOException {
        EventMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found: " + mediaId));
        try {
            String relativePath = media.getFilePath().replace("/uploads", "");
            Path filePath = Paths.get(uploadDir).resolve(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}
        mediaRepository.delete(media);
    }
}
