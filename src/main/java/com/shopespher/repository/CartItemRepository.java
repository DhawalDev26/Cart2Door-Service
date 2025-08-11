package com.shopespher.repository;

import com.shopespher.entity.Cart;
import com.shopespher.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteAllByCart(Cart cart);
}
