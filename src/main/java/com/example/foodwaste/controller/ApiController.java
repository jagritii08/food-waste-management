package com.example.foodwaste.controller;

import com.example.foodwaste.entity.*;
import com.example.foodwaste.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    // All available food
    @GetMapping("/food/available")
    public ResponseEntity<?> getAvailableFood() {
        try {
            var food = foodItemService.getAvailableFood();
            List<Map<String, Object>> result = new ArrayList<>();
            for (FoodItem f : food) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", f.getId());
                map.put("name", f.getName());
                map.put("category", f.getCategory());
                map.put("quantity", f.getQuantity());
                map.put("unit", f.getUnit());
                map.put("expiryDate", f.getExpiryDate());
                map.put("photoUrl", f.getPhotoUrl());
                map.put("status", f.getStatus());
                map.put("restaurant",
                    f.getRestaurant().getOrganizationName());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // All restaurants
    @GetMapping("/restaurants")
    public ResponseEntity<?> getRestaurants() {
        try {
            var restaurants = userService.getAllRestaurants();
            List<Map<String, Object>> result = new ArrayList<>();
            for (User r : restaurants) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", r.getId());
                map.put("name", r.getOrganizationName());
                map.put("email", r.getEmail());
                map.put("phone", r.getPhone());
                map.put("address", r.getAddress());
                map.put("avgRating",
                    ratingService.getAverageRating(r));
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // All NGOs
    @GetMapping("/ngos")
    public ResponseEntity<?> getNgos() {
        try {
            var ngos = userService.getAllNgos();
            List<Map<String, Object>> result = new ArrayList<>();
            for (User n : ngos) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", n.getId());
                map.put("name", n.getOrganizationName());
                map.put("email", n.getEmail());
                map.put("phone", n.getPhone());
                map.put("address", n.getAddress());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            var allFood = foodItemService.getAllFood();
            long available = allFood.stream()
                .filter(f -> "AVAILABLE".equals(f.getStatus())).count();
            long collected = allFood.stream()
                .filter(f -> "COLLECTED".equals(f.getStatus())).count();
            double totalQty = allFood.stream()
                .filter(f -> f.getQuantity() != null)
                .mapToDouble(FoodItem::getQuantity).sum();

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalFood", allFood.size());
            stats.put("availableFood", available);
            stats.put("collectedFood", collected);
            stats.put("totalRestaurants",
                userService.getAllRestaurants().size());
            stats.put("totalNgos",
                userService.getAllNgos().size());
            stats.put("totalQuantityKg", totalQty);
            stats.put("mealsServed", (long)(totalQty * 4));
            stats.put("co2Saved", (long)(totalQty * 2.5));
            stats.put("peopleImpacted", (long)(totalQty * 4 / 3));
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
