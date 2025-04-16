package com.example.demo.ui.admin;

import com.example.demo.model.Course;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.security.SecurityService;
import com.example.demo.service.CourseService;
import com.example.demo.service.UserService;
import com.example.demo.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "admin/courses", layout = MainLayout.class)
@PageTitle("Course Management | Admin")
public class CoursesManagementView extends VerticalLayout {
    private final Grid<Course> grid = new Grid<>(Course.class);
    private final CourseService courseService;
    private final UserService userService;
    private final SecurityService securityService;
    
    private final Button addCourseButton = new Button("Add Course");
    
    public CoursesManagementView(CourseService courseService, UserService userService, SecurityService securityService) {
        this.courseService = courseService;
        this.userService = userService;
        this.securityService = securityService;
        
        try {
            if (!securityService.isLoggedIn() || !securityService.hasRole(Role.ADMIN)) {
                getUI().ifPresent(ui -> ui.navigate(""));
                return;
            }
            
            setSizeFull();
            setSpacing(true);
            setPadding(true);
            
            H2 title = new H2("Course Management");
            add(title);
            
            addCourseButton.addClickListener(e -> openCourseForm(new Course()));
            
            HorizontalLayout toolBar = new HorizontalLayout(addCourseButton);
            add(toolBar);
            
            configureGrid();
            updateGrid();
            
            add(grid);
        } catch (Exception e) {
            Notification.show("Error initializing view: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void configureGrid() {
        try {
            grid.setSizeFull();
            grid.setColumns();
            
            grid.addColumn(Course::getCourseCode).setHeader("Course Code").setAutoWidth(true);
            grid.addColumn(Course::getCourseName).setHeader("Course Name").setAutoWidth(true);
            
            grid.addColumn(course -> {
                try {
                    User faculty = course.getFaculty();
                    return faculty == null ? "Not assigned" : faculty.getFirstName() + " " + faculty.getLastName();
                } catch (Exception e) {
                    return "Error loading faculty";
                }
            }).setHeader("Faculty").setAutoWidth(true);
            
            grid.addColumn(Course::getCreditHours).setHeader("Credit Hours").setAutoWidth(true);
            grid.addColumn(Course::getCapacity).setHeader("Capacity").setAutoWidth(true);
            grid.addColumn(course -> {
                try {
                    return course.getCurrentEnrollmentCount() + " / " + course.getCapacity();
                } catch (Exception e) {
                    return "Error";
                }
            }).setHeader("Enrollment").setAutoWidth(true);
            
            grid.addComponentColumn(course -> {
                HorizontalLayout actions = new HorizontalLayout();
                
                Button editButton = new Button("Edit", e -> openCourseForm(course));
                Button setLimitButton = new Button("Set Limit", e -> openCapacityDialog(course));
                
                actions.add(editButton, setLimitButton);
                return actions;
            }).setHeader("Actions");
        } catch (Exception e) {
            Notification.show("Error configuring grid: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void updateGrid() {
        try {
            List<Course> courses = courseService.getAllCourses();
            grid.setItems(courses);
        } catch (Exception e) {
            Notification.show("Error loading courses: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void openCourseForm(Course course) {
        Dialog dialog = new Dialog();
        dialog.setWidth("800px");
        
        FormLayout formLayout = new FormLayout();
        
        TextField courseCodeField = new TextField("Course Code");
        TextField courseNameField = new TextField("Course Name");
        TextArea courseDescriptionField = new TextArea("Description");
        IntegerField creditHoursField = new IntegerField("Credit Hours");
        ComboBox<User> facultyField = new ComboBox<>("Faculty");
        
        // Faculty name fields
        TextField facultyFirstNameField = new TextField("Faculty First Name");
        TextField facultyLastNameField = new TextField("Faculty Last Name");
        
        // Initially hide faculty name fields
        facultyFirstNameField.setVisible(false);
        facultyLastNameField.setVisible(false);
        
        // Load faculty list
        List<User> facultyList = userService.getUsersByRole(Role.FACULTY);
        facultyField.setItems(facultyList);
        facultyField.setItemLabelGenerator(user -> user.getFirstName() + " " + user.getLastName());
        
        // Add edit faculty button
        Button editFacultyButton = new Button("Edit Faculty Details");
        editFacultyButton.setEnabled(false);
        
        facultyField.addValueChangeListener(event -> {
            User selectedFaculty = event.getValue();
            editFacultyButton.setEnabled(selectedFaculty != null);
            if (selectedFaculty != null) {
                facultyFirstNameField.setValue(selectedFaculty.getFirstName());
                facultyLastNameField.setValue(selectedFaculty.getLastName());
            }
        });
        
        editFacultyButton.addClickListener(e -> {
            facultyFirstNameField.setVisible(true);
            facultyLastNameField.setVisible(true);
        });
        
        Binder<Course> binder = new Binder<>(Course.class);
        
        binder.forField(courseCodeField)
            .asRequired()
            .bind("courseCode");
            
        binder.forField(courseNameField)
            .asRequired()
            .bind("courseName");
            
        binder.forField(courseDescriptionField)
            .bind("courseDescription");
            
        binder.forField(creditHoursField)
            .asRequired()
            .bind("creditHours");
            
        binder.forField(facultyField)
            .bind("faculty");
        
        formLayout.add(
            courseCodeField,
            courseNameField,
            courseDescriptionField,
            creditHoursField,
            facultyField,
            editFacultyButton,
            facultyFirstNameField,
            facultyLastNameField
        );
        
        Button saveButton = new Button("Save", e -> {
            if (binder.validate().isOk()) {
                try {
                    binder.writeBean(course);
                    
                    // Update faculty details if they were changed
                    if (facultyFirstNameField.isVisible() && course.getFaculty() != null) {
                        User faculty = course.getFaculty();
                        faculty.setFirstName(facultyFirstNameField.getValue());
                        faculty.setLastName(facultyLastNameField.getValue());
                        userService.updateUser(faculty);
                    }
                    
                    courseService.updateCourse(course);
                    updateGrid();
                    dialog.close();
                    Notification.show("Course and faculty details saved successfully", 
                        3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    Notification.show("Error saving: " + ex.getMessage(), 
                        3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        
        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setSpacing(true);
        
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttons);
        dialog.add(dialogLayout);
        
        // Load existing data
        binder.readBean(course);
        if (course.getFaculty() != null) {
            facultyFirstNameField.setValue(course.getFaculty().getFirstName());
            facultyLastNameField.setValue(course.getFaculty().getLastName());
            editFacultyButton.setEnabled(true);
        }
        
        dialog.open();
    }
    
    private void openCapacityDialog(Course course) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        
        VerticalLayout content = new VerticalLayout();
        content.add(new H3("Set Enrollment Limit"));
        
        FormLayout formLayout = new FormLayout();
        
        IntegerField capacityField = new IntegerField("Capacity");
        capacityField.setMin(course.getCurrentEnrollmentCount()); // Cannot be less than current enrollments
        capacityField.setStep(1);
        capacityField.setValue(course.getCapacity());
        
        formLayout.add(capacityField);
        content.add(formLayout);
        
        HorizontalLayout buttons = new HorizontalLayout();
        
        Button saveButton = new Button("Save", e -> {
            if (capacityField.getValue() != null) {
                course.setCapacity(capacityField.getValue());
                courseService.updateCourse(course);
                
                Notification.show("Enrollment limit updated", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                dialog.close();
                updateGrid();
            }
        });
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        
        buttons.add(saveButton, cancelButton);
        content.add(buttons);
        
        dialog.add(content);
        dialog.open();
    }
} 