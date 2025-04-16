package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Enrollment;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {
    
    public byte[] generateCourseReport(Course course, List<Enrollment> enrollments) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Course Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            
            // Course details
            document.add(new Paragraph("Course Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph("Code: " + course.getCourseCode()));
            document.add(new Paragraph("Name: " + course.getCourseName()));
            document.add(new Paragraph("Description: " + course.getCourseDescription()));
            document.add(new Paragraph("Credit Hours: " + course.getCreditHours()));
            document.add(new Paragraph("Capacity: " + course.getCapacity()));
            document.add(new Paragraph("\n"));
            
            // Enrollment statistics
            int activeEnrollments = (int) enrollments.stream()
                .filter(Enrollment::isActive)
                .count();
            
            document.add(new Paragraph("Enrollment Statistics:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph("Total Enrollments: " + enrollments.size()));
            document.add(new Paragraph("Active Enrollments: " + activeEnrollments));
            document.add(new Paragraph("Available Seats: " + (course.getCapacity() - activeEnrollments)));
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
} 