package com.shopespher.service;

import com.shopespher.dto.CartDTO;

public interface CartService {

    CartDTO createCart(Long userId);

    CartDTO getCartById(Long cartId);

    CartDTO getCartByUserId(Long userId);

    CartDTO addItemToCart(Long userId, String productId, Integer quantity);

    CartDTO updateCartItem(Long cartId, Long itemId, Integer quantity);

    CartDTO removeCartItem(Long cartId, Long itemId);

    CartDTO clearCart(Long cartId);
}
