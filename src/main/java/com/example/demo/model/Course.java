package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;
    
    @Column(name = "course_code", length = 20, nullable = false, unique = true)
    private String courseCode;
    
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @Column(name = "title", nullable = true)
    private String title;
    
    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.courseName != null) {
            this.title = this.courseName;
        }
    }
    
    @Column(name = "course_description", length = 1000)
    private String courseDescription;
    
    @Column(name = "credit_hours", nullable = false)
    private Integer creditHours;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private User faculty;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "capacity", nullable = false)
    private Integer capacity = 30;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseMaterial> materials = new ArrayList<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timetable> timetables = new ArrayList<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();
    
    public int getCurrentEnrollmentCount() {
        return (int) enrollments.stream()
                .filter(Enrollment::isActive)
                .count();
    }
    
    public boolean hasAvailableSeats() {
        return getCurrentEnrollmentCount() < capacity;
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setCourse(this);
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
        schedule.setCourse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return id != null && id.equals(course.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Course{" +
               "id=" + id +
               ", courseCode='" + courseCode + '\'' +
               ", courseName='" + courseName + '\'' +
               '}';
    }

    // Alias getter/setter for id to maintain compatibility
    public Long getCourseId() {
        return id;
    }

    public void setCourseId(Long courseId) {
        this.id = courseId;
    }
} 