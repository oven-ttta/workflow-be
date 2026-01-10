-- Create Database Schema for Part-time Student Management System

-- Drop tables if exists (for clean setup)
DROP TABLE IF EXISTS project_members CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS timetable_slots CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table (นักเรียน Part-time และ Admin)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    custom_id VARCHAR(20) UNIQUE NOT NULL, -- TTTP01, TTTP02, etc.
    first_name VARCHAR(100) NOT NULL,
    year_level VARCHAR(50) NOT NULL,
    specialty VARCHAR(50) NOT NULL, -- Frontend, Backend, ML Engineer, UX/UI, QA, DevOps
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Should be hashed
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT', -- STUDENT, PM, ADMIN
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Timetable slots (ตารางเรียนของแต่ละคน)
CREATE TABLE timetable_slots (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL, -- Monday, Tuesday, etc.
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject VARCHAR(100),
    is_free BOOLEAN NOT NULL, -- true = ว่าง, false = มีเรียน
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Projects table
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    project_name VARCHAR(200) NOT NULL,
    difficulty_level INTEGER NOT NULL CHECK (difficulty_level BETWEEN 1 AND 5),
    duration_days INTEGER NOT NULL,
    pm_user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED', -- NOT_STARTED, IN_PROCESS, TEST, REVIEW, DONE, HELP
    start_date DATE,
    deadline DATE,
    created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Project members (สมาชิกในแต่ละโปรเจค)
CREATE TABLE project_members (
    id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, user_id)
);

-- Indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_custom_id ON users(custom_id);
CREATE INDEX idx_timetable_user_id ON timetable_slots(user_id);
CREATE INDEX idx_projects_pm_user_id ON projects(pm_user_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_project_members_project_id ON project_members(project_id);
CREATE INDEX idx_project_members_user_id ON project_members(user_id);

