package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartResponse;

/**
 * Shopping cart contract for the logged-in customer. Every method is
 * scoped to a specific userId (resolved server-side from the
 * authenticated principal) — there is no "cartId" parameter anywhere
 * in this contract, since a customer only ever operates on their own
 * single cart.
 */
public interface CartService {

    CartResponse addToCart(Long userId, AddToCartRequest request);

    CartResponse getMyCart(Long userId);

    CartResponse updateQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}