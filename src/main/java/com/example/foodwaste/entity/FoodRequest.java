package com.example.foodwaste.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_request")
public class FoodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private User ngo;

    private String status;
    private LocalDateTime requestedAt = LocalDateTime.now();
    private String message;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public User getNgo() { return ngo; }
    public void setNgo(User ngo) { this.ngo = ngo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime r) { this.requestedAt = r; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}


