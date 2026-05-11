package com.example.foodwaste.controller;

import com.example.foodwaste.entity.Rating;
import com.example.foodwaste.entity.User;
import com.example.foodwaste.service.RatingService;
import com.example.foodwaste.service.UserService;
import com.example.foodwaste.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodItemService foodItemService;

    @GetMapping("/add/{foodItemId}")
    public String showRatingForm(@PathVariable Long foodItemId,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        User ngo = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();

        if (ratingService.alreadyRated(ngo, foodItemId)) {
            return "redirect:/ngo/dashboard?alreadyRated=true";
        }

        model.addAttribute("foodItem",
            foodItemService.getAllFood().stream()
                .filter(f -> f.getId().equals(foodItemId))
                .findFirst().orElseThrow());
        model.addAttribute("foodItemId", foodItemId);
        return "rating/rate-form";
    }

    @PostMapping("/submit/{foodItemId}")
    public String submitRating(@PathVariable Long foodItemId,
                                @RequestParam Integer stars,
                                @RequestParam(required = false) String review,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User ngo = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();
        try {
            ratingService.submitRating(foodItemId, ngo, stars, review);
        } catch (Exception e) {
            return "redirect:/ngo/dashboard?error=true";
        }
        return "redirect:/ngo/dashboard?rated=true";
    }

    @GetMapping("/restaurant")
    public String restaurantRatings(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User restaurant = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();

        List<Rating> ratings = ratingService.getRestaurantRatings(restaurant);

        long star5 = ratings.stream().filter(r -> r.getStars() == 5).count();
        long star4 = ratings.stream().filter(r -> r.getStars() == 4).count();
        long star3 = ratings.stream().filter(r -> r.getStars() == 3).count();
        long star2 = ratings.stream().filter(r -> r.getStars() == 2).count();
        long star1 = ratings.stream().filter(r -> r.getStars() == 1).count();

        model.addAttribute("ratings", ratings);
        model.addAttribute("avgRating",
            ratingService.getAverageRating(restaurant));
        model.addAttribute("user", restaurant);
        model.addAttribute("star5", star5);
        model.addAttribute("star4", star4);
        model.addAttribute("star3", star3);
        model.addAttribute("star2", star2);
        model.addAttribute("star1", star1);
        model.addAttribute("totalRatings", ratings.size());
        return "rating/restaurant-ratings";
    }
}