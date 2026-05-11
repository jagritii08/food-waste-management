package com.example.foodwaste.repository;



import com.example.foodwaste.entity.FoodItem;
import com.example.foodwaste.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByStatus(String status);
    List<FoodItem> findByRestaurant(User restaurant);
    List<FoodItem> findByRestaurantAndStatus(User restaurant, String status);
    List<FoodItem> findByNameContainingIgnoreCase(String name);
    List<FoodItem> findByCategoryAndStatus(String category, String status);
}


