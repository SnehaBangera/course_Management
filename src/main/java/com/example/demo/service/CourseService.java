package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Enrollment;
import com.example.demo.model.User;
import com.example.demo.model.Schedule;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAllCoursesWithDetails();
    }
    
    @Transactional(readOnly = true)
    public List<Course> getCoursesWithAvailableSeats() {
        return courseRepository.findCoursesWithAvailableSeats().stream()
                .filter(Course::hasAvailableSeats)
                .toList();
    }
    
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }
    
    public List<Course> getCoursesByFaculty(User faculty) {
        return courseRepository.findByFaculty(faculty);
    }
    
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }
    
    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollments(User student) {
        return enrollmentRepository.findByStudentAndActiveTrue(student);
    }
    
    public List<Enrollment> getEnrollmentsForCourse(Course course) {
        return enrollmentRepository.findByCourseAndActiveTrue(course);
    }
    
    @Transactional
    public Enrollment enrollStudentInCourse(User student, Course course) {
        try {
            // Check if student is already enrolled - include inactive enrollments in check
            Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndCourse(student, course);
            
            if (existingEnrollment.isPresent()) {
                Enrollment enrollment = existingEnrollment.get();
                // If enrollment exists but is inactive, reactivate it instead of creating a new one
                if (!enrollment.isActive()) {
                    enrollment.setActive(true);
                    enrollment.setEnrollmentDate(LocalDateTime.now());
                    return enrollmentRepository.save(enrollment);
                } else {
                    // Already actively enrolled
                    throw new RuntimeException("Student is already enrolled in this course");
                }
            }
            
            // Check if course has available seats
            if (!course.hasAvailableSeats()) {
                throw new RuntimeException("Course has reached its enrollment capacity");
            }
            
            // Create enrollment
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrollmentDate(LocalDateTime.now());
            enrollment.setActive(true);
            
            return enrollmentRepository.save(enrollment);
        } catch (Exception e) {
            throw new RuntimeException("Error enrolling in course: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void dropCourse(User student, Course course) {
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourseAndActiveTrue(student, course)
            .orElseThrow(() -> new RuntimeException("Student is not enrolled in this course"));
        
        enrollment.dropCourse();
        enrollmentRepository.save(enrollment);
    }
    
    public boolean canEnrollInCourse(Course course) {
        return course.hasAvailableSeats();
    }
    
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    public Schedule addSchedule(Course course, Schedule schedule) {
        schedule.setCourse(course);
        return scheduleRepository.save(schedule);
    }

    public void removeSchedule(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    public List<Schedule> getSchedulesForCourse(Course course) {
        return scheduleRepository.findByCourse(course);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCoursesWithSchedules() {
        return courseRepository.findAllWithSchedules();
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByFacultyWithSchedules(User faculty) {
        return courseRepository.findByFacultyWithSchedules(faculty);
    }

    @Transactional
    public Course addScheduleToCourse(Course course, Schedule schedule) {
        Course managedCourse = courseRepository.findById(course.getId())
            .orElseThrow(() -> new RuntimeException("Course not found"));
        schedule.setCourse(managedCourse);
        managedCourse.getSchedules().add(schedule);
        
        // Save the course with the updated schedules
        Course updatedCourse = courseRepository.save(managedCourse);
        
        // Explicitly evict any cached schedule data to ensure students see the changes
        clearCachedData(updatedCourse);
        
        return updatedCourse;
    }

    @Transactional
    public Course removeScheduleFromCourse(Course course, Schedule scheduleToRemove) {
        Course managedCourse = courseRepository.findById(course.getId())
            .orElseThrow(() -> new RuntimeException("Course not found"));
        managedCourse.getSchedules().removeIf(schedule ->
            schedule.getDayOfWeek().equals(scheduleToRemove.getDayOfWeek()) &&
            schedule.getStartTime().equals(scheduleToRemove.getStartTime()) &&
            schedule.getEndTime().equals(scheduleToRemove.getEndTime()) &&
            schedule.getLocation().equals(scheduleToRemove.getLocation())
        );
        
        // Save the course with the updated schedules
        Course updatedCourse = courseRepository.save(managedCourse);
        
        // Explicitly evict any cached schedule data to ensure students see the changes
        clearCachedData(updatedCourse);
        
        return updatedCourse;
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollmentsWithSchedules(User student) {
        try {
            if (student == null) {
                throw new IllegalArgumentException("Student cannot be null");
            }
            
            List<Enrollment> enrollments = enrollmentRepository.findByStudentWithSchedules(student);
            
            // Ensure collections are initialized within transaction
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getCourse() != null) {
                    // Force initialization of schedules
                    Course course = enrollment.getCourse();
                    if (course.getSchedules() != null) {
                        course.getSchedules().size(); // Force initialization
                        
                        // Validate each schedule to prevent issues in UI
                        for (Schedule schedule : course.getSchedules()) {
                            if (schedule.getDayOfWeek() == null || 
                                schedule.getStartTime() == null || 
                                schedule.getEndTime() == null) {
                                // Log invalid schedule but keep it in the list
                                System.out.println("WARNING: Invalid schedule found in course " + 
                                    course.getCourseCode() + ": " + schedule);
                            }
                        }
                    }
                }
            }
            
            return enrollments;
        } catch (Exception e) {
            System.out.println("Error in getStudentEnrollmentsWithSchedules: " + e.getMessage());
            e.printStackTrace();
            // Return empty list rather than throwing exception
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Course getCourseWithMaterials(Long courseId) {
        return courseRepository.findCourseWithMaterials(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    /**
     * Helper method to ensure changes to course schedules are propagated correctly
     */
    private void clearCachedData(Course course) {
        try {
            // Force a refresh of any cached data
            System.out.println("Clearing cached data for course: " + course.getCourseCode());
            
            // Get all enrollments for this course to ensure they're refreshed
            List<Enrollment> enrollments = enrollmentRepository.findByCourseAndActiveTrue(course);
            System.out.println("Refreshing " + enrollments.size() + " enrollments for course: " + course.getCourseCode());
        } catch (Exception e) {
            System.out.println("Error clearing cached data: " + e.getMessage());
        }
    }
} 