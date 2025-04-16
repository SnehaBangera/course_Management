package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.CourseMaterial;
import com.example.demo.repository.CourseMaterialRepository;
import com.example.demo.repository.MaterialFileRepository;
import com.example.demo.model.MaterialFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseMaterialService {
    private final CourseMaterialRepository courseMaterialRepository;
    private final MaterialFileRepository materialFileRepository;

    @Transactional(readOnly = true)
    public List<CourseMaterial> getMaterialsByCourse(Course course) {
        return courseMaterialRepository.findByCourse(course);
    }

    @Transactional
    public void saveMaterialWithFile(CourseMaterial material, byte[] fileData) {
        // Save the course material first
        CourseMaterial savedMaterial = courseMaterialRepository.save(material);

        // Create and save the material file
        MaterialFile materialFile = new MaterialFile();
        materialFile.setMaterialId(savedMaterial.getMaterialId());
        materialFile.setFileData(fileData);
        materialFileRepository.save(materialFile);
    }

    @Transactional
    public void deleteMaterial(Long materialId) {
        // Delete the file data first
        materialFileRepository.deleteByMaterialId(materialId);
        // Then delete the material
        courseMaterialRepository.deleteById(materialId);
    }

    @Transactional(readOnly = true)
    public byte[] getFileData(Long materialId) {
        MaterialFile materialFile = materialFileRepository.findByMaterialId(materialId)
            .orElse(null);
        return materialFile != null ? materialFile.getFileData() : null;
    }
} 