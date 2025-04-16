package com.example.demo.ui.admin;

import com.example.demo.model.Course;
import com.example.demo.model.Schedule;
import com.example.demo.service.CourseService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

@Route(value = "admin/timetable", layout = MainLayout.class)
@PageTitle("Timetable Management | Admin")
public class TimetableManagementView extends VerticalLayout {
    private final Grid<TimetableEntry> timetableGrid = new Grid<>(TimetableEntry.class);
    private final CourseService courseService;
    private ComboBox<Course> courseFilter;
    
    private final List<TimetableEntry> timetableEntries = new ArrayList<>();
    
    public TimetableManagementView(CourseService courseService) {
        this.courseService = courseService;
        
        setSizeFull();
        setSpacing(true);
        setPadding(true);
        
        H2 title = new H2("Course Timetable Management");
        add(title);
        
        createFilterControls();
        configureGrid();
        refreshTimetable(null);
        
        add(timetableGrid);
    }
    
    private void createFilterControls() {
        courseFilter = new ComboBox<>("Filter by Course");
        courseFilter.setItems(courseService.getAllCoursesWithSchedules());
        courseFilter.setItemLabelGenerator(course -> course.getCourseCode() + ": " + course.getCourseName());
        courseFilter.setClearButtonVisible(true);
        
        Button addScheduleButton = new Button("Add Schedule", e -> openScheduleDialog(null));
        
        courseFilter.addValueChangeListener(e -> refreshTimetable(e.getValue()));
        
        HorizontalLayout controls = new HorizontalLayout(courseFilter, addScheduleButton);
        controls.setSpacing(true);
        
        add(controls);
    }
    
    private void configureGrid() {
        timetableGrid.removeAllColumns();
        
        timetableGrid.addColumn(TimetableEntry::getCourseCode).setHeader("Course Code").setAutoWidth(true);
        timetableGrid.addColumn(TimetableEntry::getCourseName).setHeader("Course Name").setAutoWidth(true);
        timetableGrid.addColumn(TimetableEntry::getDay).setHeader("Day").setAutoWidth(true);
        timetableGrid.addColumn(TimetableEntry::getStartTime).setHeader("Start Time").setAutoWidth(true);
        timetableGrid.addColumn(TimetableEntry::getEndTime).setHeader("End Time").setAutoWidth(true);
        timetableGrid.addColumn(TimetableEntry::getLocation).setHeader("Location").setAutoWidth(true);
        
        timetableGrid.addComponentColumn(entry -> {
            HorizontalLayout actions = new HorizontalLayout();
            
            Button editButton = new Button("Edit", e -> openScheduleDialog(entry));
            Button deleteButton = new Button("Delete", e -> {
                deleteSchedule(entry);
                Notification.show("Schedule deleted", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            });
            
            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
    }
    
    private void refreshTimetable(Course selectedCourse) {
        timetableEntries.clear();
        
        List<Course> courses;
        if (selectedCourse != null) {
            courses = Collections.singletonList(selectedCourse);
        } else {
            courses = courseService.getAllCoursesWithSchedules();
        }
        
        for (Course course : courses) {
            for (Schedule schedule : course.getSchedules()) {
                timetableEntries.add(new TimetableEntry(
                        course.getId(),
                        course.getCourseCode(),
                        course.getCourseName(),
                        schedule.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        schedule.getStartTime().toString(),
                        schedule.getEndTime().toString(),
                        schedule.getLocation()
                ));
            }
        }
        
        timetableGrid.setItems(timetableEntries);
    }
    
    private void openScheduleDialog(TimetableEntry entry) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        
        VerticalLayout content = new VerticalLayout();
        
        boolean isEdit = entry != null;
        H3 title = new H3(isEdit ? "Edit Schedule" : "Add Schedule");
        content.add(title);
        
        FormLayout formLayout = new FormLayout();
        
        ComboBox<Course> courseField = new ComboBox<>("Course");
        courseField.setItems(courseService.getAllCourses());
        courseField.setItemLabelGenerator(course -> course.getCourseCode() + ": " + course.getCourseName());
        
        if (isEdit) {
            // Pre-select the course for editing
            courseService.getCourseById(entry.getCourseId()).ifPresent(courseField::setValue);
        } else if (courseFilter.getValue() != null) {
            // If a course is selected in the filter, use it as default
            courseField.setValue(courseFilter.getValue());
        }
        
        ComboBox<DayOfWeek> dayField = new ComboBox<>("Day");
        dayField.setItems(DayOfWeek.values());
        dayField.setItemLabelGenerator(day -> day.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        
        if (isEdit) {
            // Convert the day string back to enum
            Arrays.stream(DayOfWeek.values())
                    .filter(day -> day.getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(entry.getDay()))
                    .findFirst()
                    .ifPresent(dayField::setValue);
        }
        
        TimePicker startTimeField = new TimePicker("Start Time");
        TimePicker endTimeField = new TimePicker("End Time");
        
        if (isEdit) {
            startTimeField.setValue(LocalTime.parse(entry.getStartTime()));
            endTimeField.setValue(LocalTime.parse(entry.getEndTime()));
        }
        
        TextField locationField = new TextField("Location");
        if (isEdit) {
            locationField.setValue(entry.getLocation());
        }
        
        formLayout.add(courseField, dayField, startTimeField, endTimeField, locationField);
        content.add(formLayout);
        
        HorizontalLayout buttons = new HorizontalLayout();
        
        Button saveButton = new Button("Save", e -> {
            try {
                Course selectedCourse = courseField.getValue();
                DayOfWeek selectedDay = dayField.getValue();
                LocalTime startTime = startTimeField.getValue();
                LocalTime endTime = endTimeField.getValue();
                String location = locationField.getValue();
                
                if (selectedCourse == null || selectedDay == null || startTime == null || 
                        endTime == null || location == null || location.trim().isEmpty()) {
                    Notification.show("All fields are required", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                
                if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                    Notification.show("Start time must be before end time", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                
                // Create the new schedule
                Schedule schedule = new Schedule();
                schedule.setDayOfWeek(selectedDay);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setLocation(location);
                
                // If editing, first remove the old schedule
                if (isEdit) {
                    Schedule oldSchedule = new Schedule();
                    oldSchedule.setDayOfWeek(DayOfWeek.valueOf(entry.getDay().toUpperCase().replace(" ", "_")));
                    oldSchedule.setStartTime(LocalTime.parse(entry.getStartTime()));
                    oldSchedule.setEndTime(LocalTime.parse(entry.getEndTime()));
                    oldSchedule.setLocation(entry.getLocation());
                    
                    courseService.removeScheduleFromCourse(selectedCourse, oldSchedule);
                }
                
                // Add the new schedule
                courseService.addScheduleToCourse(selectedCourse, schedule);
                
                dialog.close();
                refreshTimetable(courseFilter.getValue());
                
                Notification.show("Schedule saved successfully", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error saving schedule: " + ex.getMessage(), 
                    3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        
        buttons.add(saveButton, cancelButton);
        content.add(buttons);
        
        dialog.add(content);
        dialog.open();
    }
    
    private void deleteSchedule(TimetableEntry entry) {
        try {
            courseService.getCourseById(entry.getCourseId()).ifPresent(course -> {
                // Create a schedule object for removal
                Schedule scheduleToRemove = new Schedule();
                scheduleToRemove.setDayOfWeek(DayOfWeek.valueOf(entry.getDay().toUpperCase().replace(" ", "_")));
                scheduleToRemove.setStartTime(LocalTime.parse(entry.getStartTime()));
                scheduleToRemove.setEndTime(LocalTime.parse(entry.getEndTime()));
                scheduleToRemove.setLocation(entry.getLocation());
                
                // Use the service method to remove the schedule
                courseService.removeScheduleFromCourse(course, scheduleToRemove);
                
                // Refresh the grid
                refreshTimetable(courseFilter.getValue());
                
                Notification.show("Schedule deleted successfully", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            });
        } catch (Exception e) {
            Notification.show("Error deleting schedule: " + e.getMessage(), 
                3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    // Helper class for timetable entries
    public static class TimetableEntry {
        private final Long courseId;
        private final String courseCode;
        private final String courseName;
        private final String day;
        private final String startTime;
        private final String endTime;
        private final String location;
        
        public TimetableEntry(Long courseId, String courseCode, String courseName, String day, String startTime, String endTime, String location) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
        }
        
        public Long getCourseId() {
            return courseId;
        }
        
        public String getCourseCode() {
            return courseCode;
        }
        
        public String getCourseName() {
            return courseName;
        }
        
        public String getDay() {
            return day;
        }
        
        public String getStartTime() {
            return startTime;
        }
        
        public String getEndTime() {
            return endTime;
        }
        
        public String getLocation() {
            return location;
        }
    }
} 