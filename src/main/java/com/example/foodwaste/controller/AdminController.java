
package com.example.foodwaste.controller;

import com.example.foodwaste.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("allFood", foodItemService.getAllFood());
        model.addAttribute("allRequests", foodItemService.getAllRequests());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("totalRestaurants", userService.getAllRestaurants().size());
        model.addAttribute("totalNgos", userService.getAllNgos().size());
        return "admin/dashboard";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete-food/{id}")
    public String deleteFood(@PathVariable Long id) {
        foodItemService.deleteFood(id);
        return "redirect:/admin/dashboard";
    }
}