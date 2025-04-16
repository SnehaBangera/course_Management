# Course Management System

A Spring Boot application with Vaadin Flow for a course management system with student, faculty and admin roles.

## Requirements
- Java 17 or higher
- Maven 3.6.3 or higher

## Features

### Student Features
- Browse available courses for enrollment
- Register for courses with available seats
- View enrolled courses
- Drop courses
- View course content
- View personal timetable

### Faculty Features
- Upload course materials using the Vaadin Upload component
- Grant or update access to course content
- View enrolled students with filtering options
- Generate course reports (showing enrollment statistics)

### Admin Features
- Set enrollment limits for each course
- Manage and view the course timetable
- Add, edit, and delete courses
- Manage users (students, faculty, and admins)

### Login Information
Use the following credentials to test the application:

- **Student:**
  - Username: student1
  - Password: password

- **Faculty:**
  - Username: prof1
  - Password: password

- **Admin:**
  - Username: admin
  - Password: password

## How to Run

1. Navigate to the project directory:
   ```
   cd spring-boot-app
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. Access the application at:
   ```
   http://localhost:8081/
   ```

## Technology Stack
- Spring Boot 3.2.2
- Vaadin Flow 24.3.0
- H2 In-memory Database
- Spring Data JPA
- Project Lombok

## Project Structure

- `src/main/java/com/example/demo/DemoApplication.java` - Main application class
- `src/main/java/com/example/demo/controller/` - REST controllers
- `src/main/resources/application.properties` - Application configuration 