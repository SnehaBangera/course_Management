package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    private final Random random = new Random();
    
    @PostConstruct
    @Transactional
    public void init() {
        // Skip initialization if data already exists
        if (userRepository.count() > 0) {
            return;
        }
        
        // Create sample users
        User admin = new User(null, "admin", "password", "Admin", "User", "admin@example.com", Role.ADMIN);
        User faculty1 = new User(null, "prof1", "password", "John", "Doe", "john.doe@example.com", Role.FACULTY);
        User faculty2 = new User(null, "prof2", "password", "Jane", "Smith", "jane.smith@example.com", Role.FACULTY);
        User faculty3 = new User(null, "prof3", "password", "Robert", "Johnson", "robert.johnson@example.com", Role.FACULTY);
        User faculty4 = new User(null, "prof4", "password", "Michelle", "Williams", "michelle.williams@example.com", Role.FACULTY);
        
        userRepository.save(admin);
        userRepository.save(faculty1);
        userRepository.save(faculty2);
        userRepository.save(faculty3);
        userRepository.save(faculty4);
        
        // Create sample students
        User student1 = new User(null, "student1", "password", "Alice", "Johnson", "alice@example.com", Role.STUDENT);
        User student2 = new User(null, "student2", "password", "Bob", "Brown", "bob@example.com", Role.STUDENT);
        User student3 = new User(null, "student3", "password", "Charlie", "Davis", "charlie@example.com", Role.STUDENT);
        User student4 = new User(null, "student4", "password", "Diana", "Edwards", "diana@example.com", Role.STUDENT);
        User student5 = new User(null, "student5", "password", "Edward", "Franklin", "edward@example.com", Role.STUDENT);
        User student6 = new User(null, "student6", "password", "Fiona", "Garcia", "fiona@example.com", Role.STUDENT);
        User student7 = new User(null, "student7", "password", "George", "Hernandez", "george@example.com", Role.STUDENT);
        User student8 = new User(null, "student8", "password", "Hannah", "Ingram", "hannah@example.com", Role.STUDENT);
        
        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(student3);
        userRepository.save(student4);
        userRepository.save(student5);
        userRepository.save(student6);
        userRepository.save(student7);
        userRepository.save(student8);
        
        // Create sample courses with rich content
        Course course1 = createCourse("CS101", "Introduction to Programming", 
                "Learn fundamentals of programming using Java", 30, 3, faculty1,
                "# Introduction to Programming\n\n" +
                "This course introduces students to the fundamentals of programming using Java.\n\n" +
                "## Course Objectives\n\n" +
                "- Understand basic programming concepts\n" +
                "- Learn Java syntax and semantics\n" +
                "- Develop problem-solving skills\n" +
                "- Create simple programs\n\n" +
                "## Weekly Topics\n\n" +
                "### Week 1: Introduction to Computing\n" +
                "- What is programming?\n" +
                "- History of programming languages\n" +
                "- Setting up the development environment\n\n" +
                "### Week 2: Variables and Data Types\n" +
                "- Primitive data types\n" +
                "- Variables and constants\n" +
                "- Operators and expressions\n\n" +
                "### Week 3: Control Structures\n" +
                "- Conditional statements (if, switch)\n" +
                "- Loops (for, while, do-while)\n" +
                "- Break and continue statements");
                
        Course course2 = createCourse("CS201", "Data Structures", 
                "Advanced data structures and algorithms", 25, 4, faculty1,
                "# Data Structures\n\n" +
                "This course covers advanced data structures and algorithms essential for efficient programming.\n\n" +
                "## Course Objectives\n\n" +
                "- Understand various data structures\n" +
                "- Analyze algorithm complexity\n" +
                "- Implement data structures in Java\n" +
                "- Apply appropriate data structures to solve problems\n\n" +
                "## Weekly Topics\n\n" +
                "### Week 1: Arrays and Lists\n" +
                "- Arrays and dynamic arrays\n" +
                "- Linked lists (singly, doubly)\n" +
                "- Implementation and operations\n\n" +
                "### Week 2: Stacks and Queues\n" +
                "- Stack operations and implementations\n" +
                "- Queue operations and implementations\n" +
                "- Applications of stacks and queues\n\n" +
                "### Week 3: Trees\n" +
                "- Binary trees\n" +
                "- Binary search trees\n" +
                "- Tree traversals");
                
        Course course3 = createCourse("CS301", "Database Systems", 
                "Introduction to database design and SQL", 20, 3, faculty2,
                "# Database Systems\n\n" +
                "This course introduces students to database design principles and SQL.\n\n" +
                "## Course Objectives\n\n" +
                "- Understand database concepts\n" +
                "- Master SQL for data manipulation\n" +
                "- Design efficient database schemas\n" +
                "- Implement database applications\n\n" +
                "## Weekly Topics\n\n" +
                "### Week 1: Introduction to Databases\n" +
                "- Database management systems\n" +
                "- Relational model\n" +
                "- ER diagrams\n\n" +
                "### Week 2: SQL Fundamentals\n" +
                "- Basic queries (SELECT, INSERT, UPDATE, DELETE)\n" +
                "- Filtering and sorting\n" +
                "- Joins and relationships\n\n" +
                "### Week 3: Database Design\n" +
                "- Normalization\n" +
                "- Keys and constraints\n" +
                "- Indexing for performance");
                
        Course course4 = createCourse("MATH101", "Calculus I", 
                "Introduction to differential calculus", 35, 4, faculty2,
                "# Calculus I\n\n" +
                "This course introduces students to differential calculus and its applications.\n\n" +
                "## Course Objectives\n\n" +
                "- Understand limits and continuity\n" +
                "- Master derivative concepts\n" +
                "- Apply derivatives to real-world problems\n" +
                "- Analyze functions using calculus\n\n" +
                "## Weekly Topics\n\n" +
                "### Week 1: Limits and Continuity\n" +
                "- The concept of limits\n" +
                "- Properties of limits\n" +
                "- Continuity and discontinuities\n\n" +
                "### Week 2: Derivatives\n" +
                "- Definition of the derivative\n" +
                "- Differentiation rules\n" +
                "- Higher-order derivatives\n\n" +
                "### Week 3: Applications of Derivatives\n" +
                "- Rate of change\n" +
                "- Related rates\n" +
                "- Optimization problems");
                
        Course course5 = createCourse("CS401", "Artificial Intelligence", 
                "Introduction to AI concepts and algorithms", 25, 4, faculty3,
                "# Artificial Intelligence\n\n" +
                "This course introduces students to the principles and techniques of artificial intelligence.\n\n" +
                "## Course Objectives\n\n" +
                "- Understand AI foundations\n" +
                "- Implement search algorithms\n" +
                "- Develop knowledge representation systems\n" +
                "- Apply machine learning techniques\n\n" +
                "## Weekly Topics\n\n" +
                "### Week 1: Introduction to AI\n" +
                "- History of AI\n" +
                "- Intelligent agents\n" +
                "- Problem-solving approaches\n\n" +
                "### Week 2: Search Algorithms\n" +
                "- Uninformed search strategies\n" +
                "- Informed search strategies\n" +
                "- Heuristic functions\n\n" +
                "### Week 3: Knowledge Representation\n" +
                "- Predicate logic\n" +
                "- Rule-based systems\n" +
                "- Reasoning under uncertainty");
                
        Course course6 = createCourse("CS501", "Web Development", 
                "Modern web application development", 30, 3, faculty4,
                "# Web Development\n\n" +
                "This course covers modern web application development technologies and practices.\n\n" +
                "## Course Objectives\n\n" +
                "- Understand web architecture\n" +
                "- Master HTML, CSS, and JavaScript\n" +
                "- Develop server-side applications\n" +
                "- Build responsive and accessible websites\n\n" +
                "## Weekly Topics\n\n" +
                "### Week 1: HTML and CSS Fundamentals\n" +
                "- HTML structure and semantics\n" +
                "- CSS styling and layouts\n" +
                "- Responsive design principles\n\n" +
                "### Week 2: JavaScript Programming\n" +
                "- JavaScript syntax and features\n" +
                "- DOM manipulation\n" +
                "- Event handling\n\n" +
                "### Week 3: Server-side Development\n" +
                "- Backend frameworks\n" +
                "- RESTful API design\n" +
                "- Database integration");
        
        // Add schedules to courses
        addSchedule(course1, DayOfWeek.MONDAY, "09:00", "10:30", "Room 101");
        addSchedule(course1, DayOfWeek.WEDNESDAY, "09:00", "10:30", "Room 101");
        
        addSchedule(course2, DayOfWeek.TUESDAY, "11:00", "12:30", "Room 102");
        addSchedule(course2, DayOfWeek.THURSDAY, "11:00", "12:30", "Room 102");
        
        addSchedule(course3, DayOfWeek.MONDAY, "14:00", "15:30", "Room 201");
        addSchedule(course3, DayOfWeek.WEDNESDAY, "14:00", "15:30", "Room 201");
        
        addSchedule(course4, DayOfWeek.TUESDAY, "09:00", "10:30", "Room 301");
        addSchedule(course4, DayOfWeek.THURSDAY, "09:00", "10:30", "Room 301");
        
        addSchedule(course5, DayOfWeek.MONDAY, "11:00", "12:30", "Room 401");
        addSchedule(course5, DayOfWeek.FRIDAY, "11:00", "12:30", "Room 401");
        
        addSchedule(course6, DayOfWeek.WEDNESDAY, "16:00", "17:30", "Room 501");
        addSchedule(course6, DayOfWeek.FRIDAY, "14:00", "15:30", "Lab 101");
        
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);
        courseRepository.save(course6);
        
        // Create some enrollments
        enrollStudents(course1, student1, student2, student3, student5);
        enrollStudents(course2, student2, student4, student6);
        enrollStudents(course3, student1, student3, student7, student8);
        enrollStudents(course4, student2, student4, student5, student6, student8);
        enrollStudents(course5, student1, student3, student7);
        enrollStudents(course6, student2, student4, student6, student8);
    }
    
    private Course createCourse(String code, String courseName, String courseDescription, 
                              Integer capacity, Integer creditHours, User faculty, String content) {
        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(courseName);
        course.setCourseDescription(courseDescription);
        course.setCreditHours(creditHours);
        course.setCapacity(capacity);
        course.setFaculty(faculty);
        course.setContent(content);
        course.setSchedules(new ArrayList<>());
        course.setEnrollments(new ArrayList<>());
        return course;
    }
    
    private void addSchedule(Course course, DayOfWeek day, String startTime, String endTime, String location) {
        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(day);
        schedule.setStartTime(LocalTime.parse(startTime));
        schedule.setEndTime(LocalTime.parse(endTime));
        schedule.setLocation(location);
        course.getSchedules().add(schedule);
    }
    
    private void enrollStudents(Course course, User... students) {
        for (User student : students) {
            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(course);
            enrollment.setStudent(student);
            enrollment.setEnrollmentDate(LocalDateTime.now().minusDays(random.nextInt(30)));
            enrollment.setActive(true);
            enrollmentRepository.save(enrollment);
        }
    }
} 