package com.example.foodwaste.service;

import com.example.foodwaste.entity.*;
import com.example.foodwaste.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    // Rating submit karo
    public Rating submitRating(Long foodItemId, User ngo,
                                Integer stars, String review) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
            .orElseThrow(() -> new RuntimeException("Food not found"));

        // Check already rated
        if (ratingRepository.existsByNgoAndFoodItem(ngo, foodItem)) {
            throw new RuntimeException("Already rated!");
        }

        Rating rating = new Rating();
        rating.setFoodItem(foodItem);
        rating.setRestaurant(foodItem.getRestaurant());
        rating.setNgo(ngo);
        rating.setStars(stars);
        rating.setReview(review);
        return ratingRepository.save(rating);
    }

    // Restaurant ki ratings
    public List<Rating> getRestaurantRatings(User restaurant) {
        return ratingRepository.findByRestaurant(restaurant);
    }

    // Restaurant ka average rating
    public Double getAverageRating(User restaurant) {
        Double avg = ratingRepository.findAverageRatingByRestaurant(restaurant);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // Sabhi ratings
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    // NGO ki ratings
    public List<Rating> getNgoRatings(User ngo) {
        return ratingRepository.findByNgo(ngo);
    }

    // Check already rated
    public boolean alreadyRated(User ngo, Long foodItemId) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
            .orElse(null);
        if (foodItem == null) return false;
        return ratingRepository.existsByNgoAndFoodItem(ngo, foodItem);
    }
}