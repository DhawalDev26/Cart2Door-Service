package com.shopespher.controller;

import com.shopespher.dto.APIResponse;
import com.shopespher.dto.CartDTO;
import com.shopespher.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/createCart")
    public APIResponse<CartDTO> createCart(@RequestParam Long userId) {
        try {
            CartDTO cart = cartService.createCart(userId);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.CREATED)
                    .body(cart)
                    .message("Cart created successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/getCartById")
    public APIResponse<CartDTO> getCartById(@RequestParam Long cartId) {
        try {
            CartDTO cart = cartService.getCartById(cartId);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.OK)
                    .body(cart)
                    .message("Cart retrieved successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/getByUserId")
    public APIResponse<CartDTO> getCartByUserId(@RequestParam Long userId) {
        try {
            CartDTO cart = cartService.getCartByUserId(userId);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.OK)
                    .body(cart)
                    .message("User cart retrieved successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/addItem")
    public APIResponse<CartDTO> addItemToCart(
            @RequestParam String email,
            @RequestParam String productId,
            @RequestParam Integer quantity) {
        try {
            CartDTO updatedCart = cartService.addItemToCart(email, productId, quantity);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.OK)
                    .body(updatedCart)
                    .message("Item added to cart successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PutMapping("/updateItem")
    public APIResponse<CartDTO> updateCartItem(
            @RequestParam Long cartId,
            @RequestParam Long itemId,
            @RequestParam Integer quantity) {
        try {
            CartDTO updatedCart = cartService.updateCartItem(cartId, itemId, quantity);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.OK)
                    .body(updatedCart)
                    .message("Cart item updated successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/removeItem")
    public APIResponse<CartDTO> removeCartItem(
            @RequestParam Long cartId,
            @RequestParam Long itemId) {
        try {
            CartDTO updatedCart = cartService.removeCartItem(cartId, itemId);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.OK)
                    .body(updatedCart)
                    .message("Cart item removed successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/clearCart")
    public APIResponse<CartDTO> clearCart(@RequestParam Long cartId) {
        try {
            CartDTO updatedCart = cartService.clearCart(cartId);
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.OK)
                    .body(updatedCart)
                    .message("Cart cleared successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.<CartDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
