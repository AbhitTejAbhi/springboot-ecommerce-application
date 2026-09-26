package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Product catalog contract: admin-side create/update/delete, plus
 * customer-facing read operations (single lookup, paginated listing,
 * category filtering, and keyword search).
 *
 * Also defines image operations for products. This interface owns
 * the orchestration (find product, call CloudinaryService, persist
 * the resulting URL) — actual image storage mechanics live entirely
 * in CloudinaryService/CloudinaryServiceImpl, which know nothing
 * about Product.
 */
public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    void deleteProduct(Long productId);

    ProductResponse getProductById(Long productId);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    ProductResponse uploadProductImage(Long productId, MultipartFile image);

    ProductResponse updateProductImage(Long productId, MultipartFile image);

    void deleteProductImage(Long productId);
}