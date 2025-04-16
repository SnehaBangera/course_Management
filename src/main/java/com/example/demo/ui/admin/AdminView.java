package com.example.demo.ui.admin;

import com.example.demo.ui.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Dashboard | Course Management System")
public class AdminView extends VerticalLayout {
    
    public AdminView() {
        add(
            new H2("Admin Dashboard"),
            new Paragraph("Welcome to the Admin Dashboard. Use the navigation menu to manage courses and users.")
        );
        
        setSizeFull();
        setSpacing(true);
        setPadding(true);
    }
} 