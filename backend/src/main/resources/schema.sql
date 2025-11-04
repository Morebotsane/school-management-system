-- School Management System Database Schema
-- PostgreSQL 14+

-- Create database (run separately if needed)
-- CREATE DATABASE school_management_db;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================
-- ENUMS
-- ==========================================

CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'PRINCIPAL', 
    'VICE_PRINCIPAL',
    'CLASS_TEACHER',
    'SUBJECT_TEACHER',
    'STUDENT',
    'PARENT'
);

CREATE TYPE gender AS ENUM ('MALE', 'FEMALE', 'OTHER');

CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID', 'OVERDUE', 'PARTIAL');

CREATE TYPE notification_type AS ENUM ('SMS', 'EMAIL', 'IN_APP');

CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'FAILED', 'READ');

CREATE TYPE performance_trend AS ENUM ('IMPROVING', 'DECLINING', 'STABLE');

CREATE TYPE appointment_status AS ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED');

CREATE TYPE academic_period_type AS ENUM ('TERM', 'QUARTER', 'SEMESTER');

-- ==========================================
-- CORE TABLES
-- ==========================================

-- Users (Base table for all user types)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    is_active BOOLEAN DEFAULT true,
    two_factor_enabled BOOLEAN DEFAULT false,
    two_factor_secret VARCHAR(100),
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Profiles (Extended information)
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE,
    gender gender,
    phone_number VARCHAR(20),
    emergency_contact VARCHAR(20),
    address TEXT,
    profile_picture_url VARCHAR(500),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Academic Years
CREATE TABLE academic_years (
    id BIGSERIAL PRIMARY KEY,
    year_name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Academic Periods (Terms/Quarters/Semesters)
CREATE TABLE academic_periods (
    id BIGSERIAL PRIMARY KEY,
    academic_year_id BIGINT REFERENCES academic_years(id) ON DELETE CASCADE,
    period_name VARCHAR(50) NOT NULL,
    period_type academic_period_type NOT NULL,
    period_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Classes/Grades (e.g., Grade 1, Grade 2, Form 1, etc.)
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    class_level INTEGER NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sections (e.g., Grade 1A, Grade 1B)
CREATE TABLE sections (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT REFERENCES classes(id) ON DELETE CASCADE,
    section_name VARCHAR(50) NOT NULL,
    max_students INTEGER DEFAULT 40,
    class_teacher_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(class_id, section_name)
);

-- Subjects
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Student Details (extends users table)
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    admission_number VARCHAR(50) UNIQUE NOT NULL,
    admission_date DATE NOT NULL,
    section_id BIGINT REFERENCES sections(id),
    guardian_id BIGINT REFERENCES users(id),
    medical_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teacher Details (extends users table)
CREATE TABLE teachers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    employee_number VARCHAR(50) UNIQUE NOT NULL,
    hire_date DATE NOT NULL,
    qualification VARCHAR(255),
    specialization VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teacher-Subject Assignment
CREATE TABLE teacher_subjects (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES teachers(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id) ON DELETE CASCADE,
    section_id BIGINT REFERENCES sections(id) ON DELETE CASCADE,
    academic_year_id BIGINT REFERENCES academic_years(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(teacher_id, subject_id, section_id, academic_year_id)
);

-- ==========================================
-- TIMETABLE
-- ==========================================

CREATE TABLE timetable_slots (
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT REFERENCES sections(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    teacher_id BIGINT REFERENCES teachers(id),
    day_of_week INTEGER NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_number VARCHAR(50),
    academic_period_id BIGINT REFERENCES academic_periods(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- FINANCE
-- ==========================================

-- Fee Structure
CREATE TABLE fee_structures (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT REFERENCES classes(id) ON DELETE CASCADE,
    academic_year_id BIGINT REFERENCES academic_years(id),
    fee_type VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Student Payments
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    academic_period_id BIGINT REFERENCES academic_periods(id),
    amount_due DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) DEFAULT 0,
    payment_status payment_status DEFAULT 'PENDING',
    due_date DATE NOT NULL,
    payment_date DATE,
    receipt_url VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- ACADEMICS
-- ==========================================

-- Exams
CREATE TABLE exams (
    id BIGSERIAL PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    subject_id BIGINT REFERENCES subjects(id),
    section_id BIGINT REFERENCES sections(id),
    academic_period_id BIGINT REFERENCES academic_periods(id),
    exam_date DATE NOT NULL,
    max_marks DECIMAL(5, 2) NOT NULL,
    weightage DECIMAL(5, 2) DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Grades/Results
CREATE TABLE grades (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    exam_id BIGINT REFERENCES exams(id) ON DELETE CASCADE,
    marks_obtained DECIMAL(5, 2) NOT NULL,
    grade VARCHAR(5),
    remarks TEXT,
    submitted_by BIGINT REFERENCES teachers(id),
    submitted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, exam_id)
);

-- Assignments
CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    subject_id BIGINT REFERENCES subjects(id),
    section_id BIGINT REFERENCES sections(id),
    teacher_id BIGINT REFERENCES teachers(id),
    due_date TIMESTAMP NOT NULL,
    max_marks DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Assignment Submissions
CREATE TABLE assignment_submissions (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT REFERENCES assignments(id) ON DELETE CASCADE,
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    submission_file_url VARCHAR(500),
    submission_text TEXT,
    submitted_at TIMESTAMP,
    marks_obtained DECIMAL(5, 2),
    feedback TEXT,
    graded_by BIGINT REFERENCES teachers(id),
    graded_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(assignment_id, student_id)
);

-- Attendance
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    is_present BOOLEAN DEFAULT true,
    remarks TEXT,
    marked_by BIGINT REFERENCES teachers(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, date)
);

-- Performance Tracking
CREATE TABLE performance_tracking (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    academic_period_id BIGINT REFERENCES academic_periods(id),
    average_score DECIMAL(5, 2),
    grade VARCHAR(5),
    rank_in_class INTEGER,
    percentage_change DECIMAL(5, 2),
    trend performance_trend,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, subject_id, academic_period_id)
);

-- ==========================================
-- COMMUNICATION
-- ==========================================

-- Notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    notification_type notification_type NOT NULL,
    channel VARCHAR(50),
    title VARCHAR(255),
    message TEXT NOT NULL,
    status notification_status DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SMS Log (for Africa's Talking tracking)
CREATE TABLE sms_log (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    cost DECIMAL(10, 4),
    status VARCHAR(50),
    africas_talking_message_id VARCHAR(100),
    error_message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages (Internal messaging system)
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    recipient_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    parent_message_id BIGINT REFERENCES messages(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appointments
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT REFERENCES students(id) ON DELETE CASCADE,
    teacher_id BIGINT REFERENCES teachers(id),
    parent_id BIGINT REFERENCES users(id),
    appointment_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 30,
    purpose TEXT,
    status appointment_status DEFAULT 'SCHEDULED',
    notes TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Events (School-wide events)
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    event_description TEXT,
    event_date TIMESTAMP NOT NULL,
    location VARCHAR(255),
    is_mandatory BOOLEAN DEFAULT false,
    target_audience user_role[],
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- AUDIT & TRACKING
-- ==========================================

-- Audit Log
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- INDEXES FOR PERFORMANCE
-- ==========================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_students_admission_number ON students(admission_number);
CREATE INDEX idx_students_section ON students(section_id);
CREATE INDEX idx_grades_student ON grades(student_id);
CREATE INDEX idx_grades_exam ON grades(exam_id);
CREATE INDEX idx_attendance_student_date ON attendance(student_id, date);
CREATE INDEX idx_payments_student ON payments(student_id);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_performance_student ON performance_tracking(student_id);

-- ==========================================
-- TRIGGERS FOR UPDATED_AT
-- ==========================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_performance_updated_at BEFORE UPDATE ON performance_tracking
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ==========================================
-- SAMPLE DATA (for testing)
-- ==========================================

-- Insert a default admin user (password: Admin@123 - CHANGE THIS!)
-- Password hash for 'Admin@123' using BCrypt
INSERT INTO users (username, email, password_hash, role) 
VALUES ('admin', 'admin@school.com', '$2a$10$xG1VQPDlLhKuL3fPzT9EqeYsRBkxPnQZvKVCZz3jVrXt1xDxL1234', 'ADMIN');

INSERT INTO user_profiles (user_id, first_name, last_name, phone_number)
VALUES (1, 'System', 'Administrator', '+254700000000');
