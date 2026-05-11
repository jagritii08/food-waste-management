package com.example.foodwaste.repository;

import com.example.foodwaste.entity.FoodRequest;
import com.example.foodwaste.entity.pickupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PickupScheduleRepository extends JpaRepository<pickupSchedule, Long> {

    List<pickupSchedule> findByFoodRequest(FoodRequest foodRequest);

    List<pickupSchedule> findByStatus(String status);

    List<pickupSchedule> findByFoodRequestNgoId(Long ngoId);

    List<pickupSchedule> findByFoodRequestFoodItemRestaurantId(Long restaurantId);
}