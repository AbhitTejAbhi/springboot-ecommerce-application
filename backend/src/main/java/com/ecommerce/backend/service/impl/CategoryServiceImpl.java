package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * All category catalog business logic lives here — controllers stay
 * thin and only delegate to this layer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Category already exists with name: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);

        log.info("Created category: id={}, name={}", savedCategory.getId(), savedCategory.getName());

        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        // Only re-check name uniqueness if the name is actually changing —
        // otherwise saving a category with its own unchanged name would
        // incorrectly be flagged as a duplicate.
        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException(
                    "Category already exists with name: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        log.info("Updated category: id={}, name={}", updatedCategory.getId(), updatedCategory.getName());

        return mapToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        // Block deletion while products still reference this category,
        // rather than letting it fail on the fk_product_category
        // foreign key constraint and surface as an opaque 500 error.
        // The admin gets a clear, actionable message instead.
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new BadRequestException(
                    "Cannot delete category with id: " + categoryId
                            + " — products are still assigned to it");
        }

        categoryRepository.delete(category);

        log.info("Deleted category: id={}, name={}", category.getId(), category.getName());
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        return mapToResponse(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}