
package com.example.foodwaste.controller;

import com.example.foodwaste.entity.*;
import com.example.foodwaste.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User restaurant = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();

        var items = foodItemService.getRestaurantFood(restaurant);
        var requests = foodItemService.getRestaurantRequests(restaurant);

        long approvedCount = requests.stream()
            .filter(r -> "APPROVED".equals(r.getStatus()))
            .count();
        long collectedCount = items.stream()
            .filter(i -> "COLLECTED".equals(i.getStatus()))
            .count();

        model.addAttribute("items", items);
        model.addAttribute("requests", requests);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("collectedCount", collectedCount);
        model.addAttribute("user", restaurant);
        return "restaurant/dashboard";
    }

    @GetMapping("/add-food")
    public String addFoodPage(Model model) {
        model.addAttribute("foodItem", new FoodItem());
        return "restaurant/add-food";
    }

    @PostMapping("/add-food")
    public String addFood(@ModelAttribute FoodItem item,
                          @AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam(value = "photo", required = false)
                          MultipartFile photo) {
        User restaurant = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();
        foodItemService.addFood(item, restaurant, photo);
        return "redirect:/restaurant/dashboard";
    }

    @GetMapping("/approve/{requestId}")
    public String approveRequest(@PathVariable Long requestId) {
        foodItemService.approveRequest(requestId);
        return "redirect:/restaurant/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteFood(@PathVariable Long id) {
        foodItemService.deleteFood(id);
        return "redirect:/restaurant/dashboard";
    }
}