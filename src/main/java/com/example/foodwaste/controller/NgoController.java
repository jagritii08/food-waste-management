package com.example.foodwaste.controller;



import com.example.foodwaste.entity.User;
import com.example.foodwaste.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ngo")
public class NgoController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User ngo = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();
        model.addAttribute("availableFood",
            foodItemService.getAvailableFood());
        model.addAttribute("myRequests",
            foodItemService.getNgoRequests(ngo));
        model.addAttribute("user", ngo);
        return "ngo/dashboard";
    }

    @GetMapping("/claim/{foodId}")
    public String claimFood(@PathVariable Long foodId,
                            @AuthenticationPrincipal UserDetails userDetails) {
        User ngo = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();
        foodItemService.claimFood(foodId, ngo);
        return "redirect:/ngo/dashboard";
    }

    @GetMapping("/search")
    public String searchFood(@RequestParam String keyword, Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User ngo = userService.findByUsername(
            userDetails.getUsername()).orElseThrow();
        model.addAttribute("availableFood",
            foodItemService.searchFood(keyword));
        model.addAttribute("myRequests",
            foodItemService.getNgoRequests(ngo));
        model.addAttribute("user", ngo);
        model.addAttribute("keyword", keyword);
        return "ngo/dashboard";
    }
}
