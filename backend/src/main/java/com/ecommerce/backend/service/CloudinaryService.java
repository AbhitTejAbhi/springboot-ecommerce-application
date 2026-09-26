package com.ecommerce.backend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Pure image-storage contract. Responsible only for getting bytes
 * into and out of Cloudinary — knows nothing about Product, has no
 * database operations, and has no awareness of any domain entity.
 * Any domain-specific orchestration (e.g. "upload this image for
 * this product") lives in the consuming service (ProductServiceImpl),
 * not here.
 */
public interface CloudinaryService {

    /**
     * Validates and uploads the given file to Cloudinary.
     *
     * @return the secure (https) URL of the uploaded image.
     */
    String uploadImage(MultipartFile file);

    /**
     * Deletes the image at the given Cloudinary URL. Extracts the
     * public id from the URL internally. Errors are handled
     * gracefully — a failed/missing remote delete should not crash
     * the calling workflow (e.g. a product update that's otherwise
     * about to succeed).
     */
    void deleteImage(String imageUrl);
}