
package com.example.foodwaste.controller;

import com.example.foodwaste.entity.FoodItem;
import com.example.foodwaste.entity.FoodRequest;
import com.example.foodwaste.service.FoodItemService;
import com.example.foodwaste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String reports(Model model) {

        List<FoodItem> allFood = foodItemService.getAllFood();
        List<FoodRequest> allRequests = foodItemService.getAllRequests();

        // Total stats
        long totalFood = allFood.size();
        long totalAvailable = allFood.stream()
            .filter(f -> "AVAILABLE".equals(f.getStatus())).count();
        long totalClaimed = allFood.stream()
            .filter(f -> "CLAIMED".equals(f.getStatus())).count();
        long totalCollected = allFood.stream()
            .filter(f -> "COLLECTED".equals(f.getStatus())).count();
        long totalApproved = allRequests.stream()
            .filter(r -> "APPROVED".equals(r.getStatus())).count();
        long totalPending = allRequests.stream()
            .filter(r -> "PENDING".equals(r.getStatus())).count();

        // Category wise — NULL safe
        Map<String, Long> categoryCount = new LinkedHashMap<>();
        for (FoodItem f : allFood) {
            String cat = (f.getCategory() != null) ? f.getCategory() : "Other";
            categoryCount.merge(cat, 1L, Long::sum);
        }
        List<String> categoryLabels = new ArrayList<>(categoryCount.keySet());
        List<Long> categoryValues = new ArrayList<>(categoryCount.values());

        // Restaurant wise — NULL safe
        Map<String, Long> restaurantFood = new LinkedHashMap<>();
        for (FoodItem f : allFood) {
            if (f.getRestaurant() != null) {
                String name = f.getRestaurant().getOrganizationName() != null
                    ? f.getRestaurant().getOrganizationName() : "Unknown";
                restaurantFood.merge(name, 1L, Long::sum);
            }
        }

        // NGO wise — NULL safe
        Map<String, Long> ngoRequests = new LinkedHashMap<>();
        for (FoodRequest r : allRequests) {
            if (r.getNgo() != null) {
                String name = r.getNgo().getOrganizationName() != null
                    ? r.getNgo().getOrganizationName() : "Unknown";
                ngoRequests.merge(name, 1L, Long::sum);
            }
        }

        // Impact calculation
        double totalQuantity = 0;
        for (FoodItem f : allFood) {
            if (f.getQuantity() != null) {
                totalQuantity += f.getQuantity();
            }
        }
        long mealsServed = (long)(totalQuantity * 4);
        long co2Saved = (long)(totalQuantity * 2.5);
        long peopleImpacted = mealsServed / 3;

        model.addAttribute("totalFood", totalFood);
        model.addAttribute("totalAvailable", totalAvailable);
        model.addAttribute("totalClaimed", totalClaimed);
        model.addAttribute("totalCollected", totalCollected);
        model.addAttribute("totalApproved", totalApproved);
        model.addAttribute("totalPending", totalPending);
        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categoryValues", categoryValues);
        model.addAttribute("restaurantFood", restaurantFood);
        model.addAttribute("ngoRequests", ngoRequests);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("mealsServed", mealsServed);
        model.addAttribute("co2Saved", co2Saved);
        model.addAttribute("peopleImpacted", peopleImpacted);
        model.addAttribute("totalRestaurants",
            userService.getAllRestaurants().size());
        model.addAttribute("totalNgos",
            userService.getAllNgos().size());

        return "admin/reports";
    }
}