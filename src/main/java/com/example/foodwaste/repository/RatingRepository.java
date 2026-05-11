package com.example.foodwaste.repository;


import com.example.foodwaste.entity.Rating;
import com.example.foodwaste.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRestaurant(User restaurant);
    List<Rating> findByNgo(User ngo);
    boolean existsByNgoAndFoodItem(User ngo,
        com.example.foodwaste.entity.FoodItem foodItem);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.restaurant = ?1")
    Double findAverageRatingByRestaurant(User restaurant);
}