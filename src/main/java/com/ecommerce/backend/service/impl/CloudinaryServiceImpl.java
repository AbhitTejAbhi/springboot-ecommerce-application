package com.ecommerce.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements image storage against Cloudinary only. No Product
 * lookups, no repositories, no @Transactional — this class has zero
 * awareness of the domain model it happens to be used by.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/jpg",
            "image/webp"
    );

    /**
     * Matches the public id (including any folder path) out of a
     * standard Cloudinary delivery URL, e.g.:
     *   https://res.cloudinary.com/<cloud>/image/upload/v169.../folder/name.jpg
     * captures "folder/name" — i.e. everything after the optional
     * version segment (vNNNNNNNNNN/) and before the file extension.
     */
    private static final Pattern PUBLIC_ID_PATTERN =
            Pattern.compile("/upload/(?:v\\d+/)?(.+)\\.[a-zA-Z0-9]+$");

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) {
        validateFile(file);

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "products"
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");

            if (secureUrl == null) {
                // Defensive: Cloudinary's SDK throws on most failures
                // already, but guard against an unexpected response
                // shape rather than returning a null URL to the caller.
                throw new BadRequestException("Image upload failed — no URL returned");
            }

            log.info("Uploaded image to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (IOException ex) {
            log.error("Failed to upload image to Cloudinary", ex);
            throw new BadRequestException("Failed to upload image. Please try again.");
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String publicId = extractPublicId(imageUrl);

        if (publicId == null) {
            // Not a recognizable Cloudinary URL (e.g. a manually-set
            // external image URL) — nothing for Cloudinary to delete.
            // Handled gracefully rather than throwing, since this is
            // an expected possibility, not a system error.
            log.warn("Could not extract Cloudinary public id from URL, skipping delete: {}", imageUrl);
            return;
        }

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted Cloudinary image with publicId={}, result={}",
                    publicId, result.get("result"));
        } catch (IOException ex) {
            // Errors are handled gracefully per spec — a failed remote
            // delete (e.g. transient network issue, already-deleted
            // asset) should not block or crash the calling workflow
            // (e.g. a product image replace/delete that's otherwise
            // about to succeed locally).
            log.error("Failed to delete Cloudinary image with publicId={}", publicId, ex);
        }
    }

    // ----------------------------------------------------------------
    // Validation
    // ----------------------------------------------------------------

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                    "Unsupported file type: " + contentType
                            + ". Allowed types: " + ALLOWED_CONTENT_TYPES);
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException(
                    "File size exceeds the maximum allowed size of 5 MB");
        }
    }

    private String extractPublicId(String imageUrl) {
        Matcher matcher = PUBLIC_ID_PATTERN.matcher(imageUrl);
        return matcher.find() ? matcher.group(1) : null;
    }
}