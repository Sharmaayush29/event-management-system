package com.events.service;

import com.events.model.Event;
import com.events.model.EventMedia;
import com.events.model.MediaType;
import com.events.repository.MediaRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class MediaService {

    private final MediaRepository mediaRepository;
    private final Cloudinary cloudinary;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public MediaService(MediaRepository mediaRepository, Cloudinary cloudinary) {
        this.mediaRepository = mediaRepository;
        this.cloudinary = cloudinary;
    }

    public EventMedia saveMedia(Event event, MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();
        MediaType mediaType = contentType != null && contentType.startsWith("video") ? MediaType.VIDEO : MediaType.IMAGE;
        String uniqueFileName = UUID.randomUUID().toString();

        String finalPath;
        if ("prod".equals(activeProfile)) {
            // Professional Cloud Storage (Cloudinary)
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap("public_id", "events/" + event.getId() + "/" + uniqueFileName));
            finalPath = (String) uploadResult.get("secure_url");
        } else {
            // Local Storage (Development)
            Path uploadPath = Paths.get(uploadDir, "events", event.getId().toString());
            Files.createDirectories(uploadPath);
            String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf('.'))
                    : (mediaType == MediaType.VIDEO ? ".mp4" : ".jpg");
            uniqueFileName += extension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            finalPath = "/uploads/events/" + event.getId() + "/" + uniqueFileName;
        }

        EventMedia media = EventMedia.builder()
                .event(event)
                .fileName(originalName)
                .filePath(finalPath)
                .mediaType(mediaType)
                .build();
        
        EventMedia savedMedia = mediaRepository.save(media);
        if (savedMedia == null) {
            throw new RuntimeException("Failed to save media record");
        }
        return savedMedia;
    }

    @Transactional(readOnly = true)
    public List<EventMedia> getMediaByEvent(Event event) {
        return mediaRepository.findByEventOrderByUploadedAtDesc(event);
    }

    public void deleteMedia(Long mediaId) throws IOException {
        EventMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found: " + mediaId));
        
        if (!"prod".equals(activeProfile)) {
            try {
                String relativePath = media.getFilePath().replace("/uploads", "");
                Path filePath = Paths.get(uploadDir).resolve(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }
        // In prod, deleting from Cloudinary would require the public_id. 
        // For simplicity, we just delete the DB record.
        mediaRepository.delete(media);
    }
}
