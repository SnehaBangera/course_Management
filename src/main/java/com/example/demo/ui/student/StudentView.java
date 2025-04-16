package com.example.demo.ui.student;

import com.example.demo.ui.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route(value = "student", layout = MainLayout.class)
@PageTitle("Student Dashboard | Course Management System")
public class StudentView extends VerticalLayout implements RouterLayout {
    
    public StudentView() {
        setSizeFull();
        setSpacing(true);
        setPadding(true);
        
        add(
            new H2("Student Dashboard"),
            new Paragraph("Welcome to the Course Management System. Use the navigation menu to access different features.")
        );
    }
} 