package com.example.foodwaste.controller;

import com.example.foodwaste.entity.*;
import com.example.foodwaste.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pickup")
public class Pickupcontroller {

    @Autowired
    private PickupService pickupService;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodItemService foodItemService;

    @GetMapping("/schedule/{requestId}")
    public String showScheduleForm(@PathVariable Long requestId,
                                    Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            model.addAttribute("schedule", new pickupSchedule());
            model.addAttribute("requestId", requestId);
            return "pickup/schedule-form";
        } catch (Exception e) {
            return "redirect:/ngo/dashboard";
        }
    }

    @PostMapping("/schedule/{requestId}")
    public String saveSchedule(@PathVariable Long requestId,
                                @ModelAttribute pickupSchedule schedule) {
        try {
            pickupService.schedulePickup(schedule, requestId);
        } catch (Exception e) {
            return "redirect:/ngo/dashboard";
        }
        return "redirect:/ngo/dashboard";
    }

    @GetMapping("/ngo")
    public String ngoSchedules(@AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        try {
            User ngo = userService.findByUsername(
                userDetails.getUsername()).orElseThrow();

            var schedules = pickupService.getNgoSchedules(ngo.getId());

            long scheduled = schedules.stream()
                .filter(s -> "SCHEDULED".equals(s.getStatus())).count();
            long completed = schedules.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus())).count();

            model.addAttribute("schedules", schedules);
            model.addAttribute("scheduledCount", scheduled);
            model.addAttribute("completedCount", completed);
            model.addAttribute("user", ngo);
            return "pickup/ngo-schedules";
        } catch (Exception e) {
            return "redirect:/ngo/dashboard";
        }
    }

    @GetMapping("/restaurant")
    public String restaurantSchedules(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            User restaurant = userService.findByUsername(
                userDetails.getUsername()).orElseThrow();

            var schedules = pickupService.getRestaurantSchedules(restaurant.getId());

            long scheduled = schedules.stream()
                .filter(s -> "SCHEDULED".equals(s.getStatus())).count();
            long completed = schedules.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus())).count();

            model.addAttribute("schedules", schedules);
            model.addAttribute("scheduledCount", scheduled);
            model.addAttribute("completedCount", completed);
            model.addAttribute("user", restaurant);
            return "pickup/restaurant-schedules";
        } catch (Exception e) {
            return "redirect:/restaurant/dashboard";
        }
    }

    @GetMapping("/complete/{id}")
    public String markCompleted(@PathVariable Long id) {
        try {
            pickupService.markCompleted(id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "redirect:/pickup/restaurant";
    }

    @GetMapping("/cancel/{id}")
    public String cancelSchedule(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(
                userDetails.getUsername()).orElseThrow();
            pickupService.cancelSchedule(id);
            if (user.getRole().equals("NGO")) {
                return "redirect:/pickup/ngo";
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "redirect:/pickup/restaurant";
    }

    @GetMapping("/admin")
    public String adminSchedules(Model model) {
        model.addAttribute("schedules", pickupService.getAllSchedules());
        return "pickup/admin-schedules";
    }
}

