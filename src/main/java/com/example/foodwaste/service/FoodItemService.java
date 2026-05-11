
package com.example.foodwaste.service;

import com.example.foodwaste.entity.*;
import com.example.foodwaste.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private FoodRequestRepository foodRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Restaurant food add karta hai
    public FoodItem addFood(FoodItem item, User restaurant, MultipartFile photo) {
        item.setRestaurant(restaurant);
        item.setStatus("AVAILABLE");

        // Photo save karo
        if (photo != null && !photo.isEmpty()) {
            try {
                String uploadDir = "uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis()
                    + "_" + photo.getOriginalFilename();
                Files.write(Paths.get(uploadDir + fileName), photo.getBytes());
                item.setPhotoUrl("/uploads/" + fileName);
            } catch (Exception e) {
                System.out.println("Photo upload failed: " + e.getMessage());
            }
        }

        FoodItem saved = foodItemRepository.save(item);

        // Sabhi NGOs ko email bhejo
        List<User> ngos = userRepository.findByRole("NGO");
        for (User ngo : ngos) {
            emailService.sendFoodAvailableEmail(
                ngo.getEmail(),
                item.getName(),
                restaurant.getOrganizationName(),
                item.getQuantity() + " " + item.getUnit()
            );
        }
        return saved;
    }

    // Sabhi available food items
    public List<FoodItem> getAvailableFood() {
        return foodItemRepository.findByStatus("AVAILABLE");
    }

    // Restaurant ke sabhi food items
    public List<FoodItem> getRestaurantFood(User restaurant) {
        return foodItemRepository.findByRestaurant(restaurant);
    }

    // NGO food claim karta hai
    public FoodRequest claimFood(Long foodItemId, User ngo) {
        FoodItem item = foodItemRepository.findById(foodItemId)
            .orElseThrow(() -> new RuntimeException("Food nahi mila"));

        FoodRequest request = new FoodRequest();
        request.setFoodItem(item);
        request.setNgo(ngo);
        request.setStatus("PENDING");

        item.setStatus("CLAIMED");
        foodItemRepository.save(item);

        emailService.sendRequestStatusEmail(
            item.getRestaurant().getEmail(),
            ngo.getOrganizationName(),
            item.getName(),
            "CLAIMED"
        );

        return foodRequestRepository.save(request);
    }

    // Restaurant request approve karta hai
    public FoodRequest approveRequest(Long requestId) {
        FoodRequest request = foodRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request nahi mili"));
        request.setStatus("APPROVED");
        request.getFoodItem().setStatus("COLLECTED");
        foodItemRepository.save(request.getFoodItem());

        emailService.sendRequestStatusEmail(
            request.getNgo().getEmail(),
            request.getFoodItem().getRestaurant().getOrganizationName(),
            request.getFoodItem().getName(),
            "APPROVED"
        );

        return foodRequestRepository.save(request);
    }

    // Search food by name
    public List<FoodItem> searchFood(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Food delete karo
    public void deleteFood(Long id) {
        foodItemRepository.deleteById(id);
    }

    // NGO ki requests
    public List<FoodRequest> getNgoRequests(User ngo) {
        return foodRequestRepository.findByNgo(ngo);
    }

    // Restaurant ki requests
    public List<FoodRequest> getRestaurantRequests(User restaurant) {
        return foodRequestRepository.findByFoodItemRestaurant(restaurant);
    }

    // Admin — sabhi requests
    public List<FoodRequest> getAllRequests() {
        return foodRequestRepository.findAll();
    }

    // Admin — sabhi food items
    public List<FoodItem> getAllFood() {
        return foodItemRepository.findAll();
    }
}