package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartItemResponse;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * All shopping cart business logic lives here — controllers stay
 * thin and only delegate to this layer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {

        User user = getUserOrThrow(userId);
        Product product = getActiveProductOrThrow(request.getProductId());

        Cart cart = getOrCreateCart(user);

        // Merge into the existing line item if this product is already
        // in the cart, rather than creating a duplicate row — enforced
        // both here and at the DB level via the uq_cart_product
        // composite unique constraint on (cart_id, product_id).
        CartItem existingItem = cartItemRepository
                .findByCartUserIdAndProductId(userId, product.getId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            validateStock(product, newQuantity);
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);

            log.info("Increased quantity for productId={} in cart of userId={} to {}",
                    product.getId(), userId, newQuantity);
        } else {
            validateStock(product, request.getQuantity());

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);

            log.info("Added new productId={} to cart of userId={} with quantity={}",
                    product.getId(), userId, request.getQuantity());
        }

        return buildCartResponse(cart, userId);
    }

    @Override
    public CartResponse getMyCart(Long userId) {
        Cart cart = getOrCreateCartByUserId(userId);
        return buildCartResponse(cart, userId);
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request) {

        CartItem cartItem = cartItemRepository.findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItemId));

        Product product = cartItem.getProduct();
        validateStock(product, request.getQuantity());

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Updated quantity of cartItemId={} for userId={} to {}",
                cartItemId, userId, request.getQuantity());

        return buildCartResponse(cartItem.getCart(), userId);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + cartItemId));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);

        log.info("Removed cartItemId={} from cart of userId={}", cartItemId, userId);

        return buildCartResponse(cart, userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByCartUserId(userId);
        log.info("Cleared cart for userId={}", userId);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }

    private Product getActiveProductOrThrow(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        if (!product.isActive()) {
            throw new BadRequestException(
                    "Product is not available: " + product.getName());
        }

        return product;
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new BadRequestException(
                    "Insufficient stock for product: " + product.getName()
                            + ". Available: " + product.getStock()
                            + ", requested: " + requestedQuantity);
        }
    }

    /**
     * Fetches the user's existing cart, or lazily creates one if this
     * is their first ever cart interaction. A Cart is created
     * implicitly on first add-to-cart rather than at registration time,
     * since not every registered user necessarily starts shopping
     * immediately.
     */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    Cart savedCart = cartRepository.save(newCart);
                    log.info("Created new cart for userId={}", user.getId());
                    return savedCart;
                });
    }

    /**
     * Used by read-only/ownership-scoped operations (getMyCart,
     * updateQuantity, removeItem) where we only have a userId, not
     * a hydrated User entity, and don't want an extra User lookup
     * just to satisfy getOrCreateCart's signature.
     */
    private Cart getOrCreateCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = getUserOrThrow(userId);
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    Cart savedCart = cartRepository.save(newCart);
                    log.info("Created new cart for userId={}", userId);
                    return savedCart;
                });
    }

    /**
     * Builds the full CartResponse — all line items plus the computed
     * grand total — by re-querying cart items fresh from the
     * repository rather than relying on the Cart entity's lazily
     * loaded "cartItems" collection, avoiding any stale-collection or
     * LazyInitializationException concerns after a save/delete inside
     * the same transaction.
     */
    private CartResponse buildCartResponse(Cart cart, Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByCartUserId(userId);

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(this::mapToItemResponse)
                .toList();

        BigDecimal grandTotal = itemResponses.stream()
                .map(CartItemResponse::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .grandTotal(grandTotal)
                .totalItems(itemResponses.size())
                .build();
    }

    private CartItemResponse mapToItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal itemTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .productPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .itemTotal(itemTotal)
                .build();
    }
}