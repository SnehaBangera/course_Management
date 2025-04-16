package com.example.demo.ui;

import com.example.demo.model.Role;
import com.example.demo.security.SecurityService;
import com.example.demo.ui.admin.AdminView;
import com.example.demo.ui.faculty.FacultyView;
import com.example.demo.ui.student.StudentView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@PageTitle("Login | Course Management System")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    private final SecurityService securityService;
    
    public LoginView(SecurityService securityService) {
        this.securityService = securityService;
        
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        var loginForm = new VerticalLayout();
        loginForm.setWidth("400px");
        loginForm.setAlignItems(Alignment.STRETCH);
        
        var title = new H1("Course Management System");
        
        var username = new TextField("Username");
        var password = new PasswordField("Password");
        
        var loginButton = new Button("Login", e -> login(username.getValue(), password.getValue()));
        
        loginForm.add(title, username, password, loginButton);
        add(loginForm);
    }
    
    private void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showNotification("Please enter username and password", NotificationVariant.LUMO_ERROR);
            return;
        }
        
        if (securityService.login(username, password)) {
            Role role = securityService.getCurrentUser().getRole();
            
            if (role == Role.STUDENT) {
                getUI().ifPresent(ui -> ui.navigate(StudentView.class));
            } else if (role == Role.FACULTY) {
                getUI().ifPresent(ui -> ui.navigate(FacultyView.class));
            } else if (role == Role.ADMIN) {
                getUI().ifPresent(ui -> ui.navigate(AdminView.class));
            }
        } else {
            showNotification("Invalid username or password", NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void showNotification(String message, NotificationVariant variant) {
        var notification = Notification.show(message, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(variant);
    }
} 