package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.CloudinaryService;
import com.ecommerce.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * All product catalog business logic lives here — controllers stay
 * thin and only delegate to this layer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Product already exists with name: " + request.getName());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .active(request.isActive())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        log.info("Created product: id={}, name={}", savedProduct.getId(), savedProduct.getName());

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        log.info("Updated product: id={}, name={}", updatedProduct.getId(), updatedProduct.getName());

        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        // Hard delete for now, per current spec. Note: Product has no
        // cascade configured on its cartItems/orderItems associations,
        // so if this product is still referenced by an existing
        // CartItem or OrderItem row, this delete will fail on the
        // foreign key constraint (fk_cart_item_product /
        // fk_order_item_product) rather than silently orphaning data.
        // A soft delete (product.setActive(false)) avoids this entirely
        // and is the recommended approach once historical order
        // integrity matters — flagged here per the task's own note.


         // Delete the associated Cloudinary image first (if any)
        cloudinaryService.deleteImage(product.getImageUrl());
        // Then delete the product from the database
        productRepository.delete(product);

        log.info("Deleted product: id={}, name={}", product.getId(), product.getName());
    }
    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {

        // Validate the category actually exists before querying products,
        // so a bad/nonexistent categoryId fails fast with a clear 404
        // instead of silently returning an empty page.
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    "Category not found with id: " + categoryId);
        }

        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ProductResponse uploadProductImage(Long productId, MultipartFile image) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        // Per project decision: upload always overwrites regardless of
        // whether the product already has an image set (whether that
        // existing value came from a prior Cloudinary upload or was
        // entered manually as a plain URL via create/update). Any
        // existing image is still cleaned up first via the shared
        // replaceImage() helper, the same as updateProductImage().
        Product updatedProduct = replaceImage(product, image);

        log.info("Uploaded image for product id={}, url={}",
                updatedProduct.getId(), updatedProduct.getImageUrl());

        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        Product updatedProduct = replaceImage(product, image);

        log.info("Replaced image for product id={}, url={}",
                updatedProduct.getId(), updatedProduct.getImageUrl());

        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        cloudinaryService.deleteImage(product.getImageUrl());

        product.setImageUrl(null);
        productRepository.save(product);

        log.info("Deleted image for product id={}", productId);
    }

    /**
     * Shared workflow for both upload and replace: delete whatever
     * image currently exists (a no-op inside CloudinaryService if
     * imageUrl is null/blank or not a recognizable Cloudinary URL),
     * upload the new file, persist the new URL, and return the saved
     * product. Centralized here so uploadProductImage and
     * updateProductImage can't silently drift apart in behavior.
     */
    private Product replaceImage(Product product, MultipartFile image) {
        cloudinaryService.deleteImage(product.getImageUrl());

        String newImageUrl = cloudinaryService.uploadImage(image);

        product.setImageUrl(newImageUrl);
        return productRepository.save(product);
    }

    /**
     * Converts a Product entity into its outbound DTO, flattening the
     * Category association into categoryId/categoryName.
     */
    private ProductResponse mapToResponse(Product product) {
        Category category = product.getCategory();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}