package com.example.demo.ui;

import com.example.demo.model.Role;
import com.example.demo.security.SecurityService;
import com.example.demo.ui.admin.AdminView;
import com.example.demo.ui.faculty.FacultyView;
import com.example.demo.ui.student.AvailableCoursesView;
import com.example.demo.ui.student.MyCoursesView;
import com.example.demo.ui.student.StudentView;
import com.example.demo.ui.student.TimetableView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        
        if (!securityService.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }
        
        createHeader();
        createDrawer();
    }
    
    private void createHeader() {
        H1 logo = new H1("Course Management System");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        
        String username = securityService.getCurrentUser().getFirstName() + " " 
                + securityService.getCurrentUser().getLastName();
        Span userInfo = new Span("Logged in as: " + username + " (" 
                + securityService.getCurrentUser().getRole() + ")");
        
        Button logoutButton = new Button("Logout", e -> {
            securityService.logout();
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, userInfo, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        
        addToNavbar(header);
    }
    
    private void createDrawer() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        
        if (securityService.hasRole(Role.STUDENT)) {
            tabs.add(createTab(VaadinIcon.ACADEMY_CAP, "Available Courses", AvailableCoursesView.class));
            tabs.add(createTab(VaadinIcon.LIST, "My Courses", MyCoursesView.class));
            tabs.add(createTab(VaadinIcon.CALENDAR, "Timetable", TimetableView.class));
        } else if (securityService.hasRole(Role.FACULTY)) {
            tabs.add(createTab(VaadinIcon.ACADEMY_CAP, "My Courses", com.example.demo.ui.faculty.CoursesView.class));
            tabs.add(createTab(VaadinIcon.USERS, "Students", com.example.demo.ui.faculty.StudentsView.class));
            tabs.add(createTab(VaadinIcon.CALENDAR, "Timetable", com.example.demo.ui.faculty.FacultyTimetableView.class));
        } else if (securityService.hasRole(Role.ADMIN)) {
            tabs.add(createTab(VaadinIcon.ACADEMY_CAP, "Courses", com.example.demo.ui.admin.CoursesManagementView.class));
            tabs.add(createTab(VaadinIcon.USERS, "Users", com.example.demo.ui.admin.UsersView.class));
            tabs.add(createTab(VaadinIcon.CALENDAR, "Timetable", com.example.demo.ui.admin.TimetableManagementView.class));
        }
        
        addToDrawer(tabs);
    }
    
    private <T extends Component> Tab createTab(VaadinIcon viewIcon, String viewName, Class<T> viewClass) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");
        
        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setRoute(viewClass);
        link.setTabIndex(-1);
        
        return new Tab(link);
    }
} 