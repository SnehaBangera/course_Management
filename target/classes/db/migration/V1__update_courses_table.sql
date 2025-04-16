-- Update courses table to match entity
ALTER TABLE courses CHANGE COLUMN title course_name VARCHAR(255) NOT NULL; 