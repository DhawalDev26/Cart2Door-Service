package com.shopespher.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopespher.dto.APIResponse;
import com.shopespher.dto.CartDTO;
import com.shopespher.dto.UserResponseDTO;
import com.shopespher.entity.Cart;
import com.shopespher.entity.CartItem;
import com.shopespher.entity.Product;
import com.shopespher.feignservice.ProductFeignClient;
import com.shopespher.feignservice.UserFeignClient;
import com.shopespher.repository.CartItemRepository;
import com.shopespher.repository.CartRepository;
import com.shopespher.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public CartDTO createCart(Long userId) {
        try {
            Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

            if (optionalCart.isPresent()) {
                CartDTO existingCart = objectMapper.convertValue(optionalCart.get(), CartDTO.class);
                return existingCart;
            }

            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart.setTotalPrice(0.0);
            cart = cartRepository.save(cart);

            return mapToDTO(cart);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create cart for userId: " + userId, e);
        }
    }

    @Override
    public CartDTO getCartById(Long cartId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            return objectMapper.convertValue(cart, CartDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch cart by ID: " + cartId, e);
        }
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            return objectMapper.convertValue(cart, CartDTO.class);
//            List<Product> productDTOs = cart.getItems().stream()
//                    .map(product -> objectMapper.convertValue(product, CartDTO.class))
//                    .toList();
//            cartDTO.setProducts(productDTOs);
        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch cart for userId: " + userId, e);
        }
    }

    @Override
    public CartDTO addItemToCart(String email, String productId, Integer quantity) {
        Long userId = 0L;
        try {
            APIResponse response = productFeignClient.getProductById(productId);
            Product product = objectMapper.convertValue(response.getBody(), Product.class);

            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }

            APIResponse userByEmail = userFeignClient.getUserByEmail(email);
            UserResponseDTO user = objectMapper.convertValue(userByEmail.getBody(), UserResponseDTO.class);

            Cart cart = cartRepository.findByUserId(userId).orElse(null);

            if (cart == null) {
                // Create new cart
                cart = new Cart();
                cart.setUserId(userId);
                cart.setCreatedAt(LocalDateTime.now());
                cart.setUpdatedAt(LocalDateTime.now());
                cart.setTotalPrice(0.0);
                cart = cartRepository.save(cart);
            }

            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                // Product already in cart → update qty
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem.setSubTotal(existingItem.getPrice() * existingItem.getQuantity());
                cartItemRepository.save(existingItem);
            } else {
                // New product in cart
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                BigDecimal price = product.getPrice();
                newItem.setPrice(price.doubleValue());
                newItem.setSubTotal(price.multiply(BigDecimal.valueOf(quantity)).doubleValue());
                newItem.setAddedAt(LocalDateTime.now());

                cart.getItems().add(newItem);
                cartItemRepository.save(newItem);
            }

            updateCartTotalPrice(cart);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            return mapToDTO(cart, product);
        } catch (Exception e) {
            throw new RuntimeException("Unable to add item to cart for userId: " + userId, e);
        }
    }

    @Override
    public CartDTO updateCartItem(Long cartId, Long itemId, Integer quantity) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            CartItem item = cart.getItems().stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Item not found in cart"));

            item.setQuantity(quantity);
            item.setSubTotal(item.getPrice() * quantity);
            cartItemRepository.save(item);

            updateCartTotalPrice(cart);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            return objectMapper.convertValue(cart, CartDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to update cart item", e);
        }
    }

    @Override
    public CartDTO removeCartItem(Long cartId, Long itemId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            CartItem item = cart.getItems().stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Item not found in cart"));

            cart.getItems().remove(item);
            cartItemRepository.delete(item);

            updateCartTotalPrice(cart);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            return objectMapper.convertValue(cart, CartDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to remove item from cart", e);
        }
    }

    @Override
    public CartDTO clearCart(Long cartId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            cart.getItems().clear();
            cartItemRepository.deleteAllByCart(cart);
            cart.setTotalPrice(0.0);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
            return objectMapper.convertValue(cart, CartDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to clear cart", e);
        }
    }

    private void updateCartTotalPrice(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }

    private CartDTO mapToDTO(Cart cart, Product product) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        cartDTO.setUserId(cart.getUserId());
        cartDTO.setProducts(List.of(product));

        return cartDTO;
    }

    private CartDTO mapToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        cartDTO.setUserId(cart.getUserId());

        return cartDTO;
    }
}

