package com.example.foodwaste.repository;



import com.example.foodwaste.entity.FoodRequest;
import com.example.foodwaste.entity.User;
import com.example.foodwaste.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRequestRepository extends JpaRepository<FoodRequest, Long> {
    List<FoodRequest> findByNgo(User ngo);
    List<FoodRequest> findByFoodItemRestaurant(User restaurant);
    List<FoodRequest> findByStatus(String status);
    List<FoodRequest> findByFoodItem(FoodItem foodItem);
}
