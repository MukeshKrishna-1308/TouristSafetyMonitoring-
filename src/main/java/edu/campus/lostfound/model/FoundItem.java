package edu.campus.lostfound.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "found_items")
public class FoundItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(nullable = false)
    private Instant foundDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FoundStatus status;

    protected FoundItem() {
    }

    public FoundItem(User staff, Category category, String description, String location, Instant foundDate) {
        this.staff = staff;
        this.category = category;
        this.description = description;
        this.location = location;
        this.foundDate = foundDate;
        this.status = FoundStatus.AVAILABLE;
    }

    public Long getId() { return id; }
    public User getStaff() { return staff; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Instant getFoundDate() { return foundDate; }
    public FoundStatus getStatus() { return status; }

    @PrePersist
    void ensureDate() {
        if (foundDate == null) foundDate = Instant.now();
    }

    public void setStatus(FoundStatus status) { this.status = status; }
}