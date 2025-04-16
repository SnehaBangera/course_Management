package com.example.demo.repository;

import com.example.demo.model.Course;
import com.example.demo.model.Enrollment;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("SELECT DISTINCT e FROM Enrollment e LEFT JOIN FETCH e.course c LEFT JOIN FETCH c.faculty WHERE e.student = ?1 AND e.active = true")
    List<Enrollment> findByStudentAndActiveTrue(User student);
    
    @Query("SELECT DISTINCT e FROM Enrollment e LEFT JOIN FETCH e.student LEFT JOIN FETCH e.course WHERE e.course = ?1 AND e.active = true")
    List<Enrollment> findByCourseAndActiveTrue(Course course);
    
    Optional<Enrollment> findByStudentAndCourseAndActiveTrue(User student, Course course);
    
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = ?1 AND e.active = true")
    int countActiveByCourse(Course course);

    @Query("SELECT DISTINCT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.course c " +
           "LEFT JOIN FETCH c.faculty " +
           "LEFT JOIN FETCH c.schedules " +
           "WHERE e.student = ?1 AND e.active = true")
    List<Enrollment> findByStudentWithSchedules(User student);
} 