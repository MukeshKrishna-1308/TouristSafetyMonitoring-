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
@Table(name = "lost_reports")
public class LostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(nullable = false)
    private Instant reportDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LostStatus status;

    protected LostReport() {
    }

    public LostReport(User user, Category category, String description, String location, Instant reportDate) {
        this.user = user;
        this.category = category;
        this.description = description;
        this.location = location;
        this.reportDate = reportDate;
        this.status = LostStatus.OPEN;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Instant getReportDate() { return reportDate; }
    public LostStatus getStatus() { return status; }

    @PrePersist
    void ensureDate() {
        if (reportDate == null) reportDate = Instant.now();
    }

    public void setStatus(LostStatus status) { this.status = status; }
}