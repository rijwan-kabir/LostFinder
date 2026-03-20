package com.diu.lostfinder.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;
    private String location;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.PENDING;

    // Remove @DateTimeFormat - we'll handle date manually
    private LocalDateTime date;

    @ElementCollection
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    private String mainImageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User postedBy;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private String rejectionReason;

    public enum ItemType {
        LOST, FOUND
    }

    public enum ItemStatus {
        PENDING, APPROVED, REJECTED
    }
}