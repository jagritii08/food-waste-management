package com.example.foodwaste.service;



import com.example.foodwaste.entity.*;
import com.example.foodwaste.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PickupService {

    @Autowired
    private PickupScheduleRepository pickupRepository;

    @Autowired
    private FoodRequestRepository foodRequestRepository;

    // Schedule banao
    public pickupSchedule schedulePickup(pickupSchedule schedule, Long requestId) {
        FoodRequest request = foodRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request nahi mili"));
        schedule.setFoodRequest(request);
        schedule.setStatus("SCHEDULED");
        return pickupRepository.save(schedule);
    }

    // NGO ki schedules
    public List<pickupSchedule> getNgoSchedules(Long ngoId) {
        return pickupRepository.findByFoodRequestNgoId(ngoId);
    }

    // Restaurant ki schedules
    public List<pickupSchedule> getRestaurantSchedules(Long restaurantId) {
        return pickupRepository.findByFoodRequestFoodItemRestaurantId(restaurantId);
    }

    // Sabhi schedules
    public List<pickupSchedule> getAllSchedules() {
        return pickupRepository.findAll();
    }

    // Complete mark karo
    public pickupSchedule markCompleted(Long id) {
        pickupSchedule schedule = pickupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule nahi mili"));
        schedule.setStatus("COMPLETED");
        return pickupRepository.save(schedule);
    }

    // Cancel karo
    public pickupSchedule cancelSchedule(Long id) {
        pickupSchedule schedule = pickupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule nahi mili"));
        schedule.setStatus("CANCELLED");
        return pickupRepository.save(schedule);
    }

    // Schedule by ID
    public pickupSchedule getById(Long id) {
        return pickupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule nahi mili"));
    }
}