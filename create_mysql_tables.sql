-- MySQL Tables for Face Recognition Integration
-- Based on Dump20251118.sql structure + SQLite MemberEmbeddings
-- Focus: Members with face_id, keep MemberEmbeddings structure
-- Generated: December 2025

-- Users table - Common for all users (customers, staff, PT, etc.)
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dob` datetime(6) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `full_name` varchar(50) NOT NULL,
  `gender` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKqv8dhcohbkxtcq4i7beyy6t9x` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Members table - Customers with membership and face_id (no card_id as requested)
CREATE TABLE IF NOT EXISTS `members` (
  `face_id` varchar(255) DEFAULT NULL,  -- For face recognition
  `join_date` datetime(6) DEFAULT NULL,
  `membership` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8xc8ex06k91fyx0v7hqafk62u` (`id`),
  CONSTRAINT `FK8xc8ex06k91fyx0v7hqafk62u` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Staffs table - Employees (PT, reception, etc.)
CREATE TABLE IF NOT EXISTS `staffs` (
  `hire_price` varchar(255) DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `specialize` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdrcbb0t4jyjslw24sf1tkfk2p` (`id`),
  CONSTRAINT `FKdrcbb0t4jyjslw24sf1tkfk2p` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Access logs table - Member check-in/out logs
CREATE TABLE IF NOT EXISTS `access_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `access_method` varchar(20) NOT NULL DEFAULT 'FACE',
  `access_time` datetime(6) NOT NULL,
  `device_id` varchar(50) NOT NULL DEFAULT 'FACE_RECOGNITION_DEVICE_01',
  `location_type` varchar(50) NOT NULL DEFAULT 'MAIN_ENTRANCE',
  `reason` varchar(255) DEFAULT NULL,
  `result` varchar(20) NOT NULL DEFAULT 'SUCCESS',
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdk3wj918ygucveg1qxynfv4a5` (`member_id`),
  CONSTRAINT `FKdk3wj918ygucveg1qxynfv4a5` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- MemberEmbeddings table - Keep same structure as SQLite for members
CREATE TABLE IF NOT EXISTS `MemberEmbeddings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `embeddings` longblob NOT NULL,
  `model_name` varchar(255) NOT NULL DEFAULT 'ArcFace',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_member_embeddings_member_id` (`member_id`),
  CONSTRAINT `fk_member_embeddings_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- EmployeeEmbeddings table - For future use (staff face recognition)
CREATE TABLE IF NOT EXISTS `EmployeeEmbeddings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `embeddings` longblob NOT NULL,
  `model_name` varchar(255) NOT NULL DEFAULT 'ArcFace',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_embeddings_employee_id` (`employee_id`),
  CONSTRAINT `fk_employee_embeddings_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `staffs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Sample data for testing (optional)
-- Uncomment to insert sample users/members

/*
-- Sample user (customer)
INSERT INTO users (id, full_name, email, phone, dob, gender, password, role, is_deleted) VALUES
(1001, 'Nguyen Van Test', 'test@example.com', '0905123456', '1990-01-01 00:00:00.000000', 'MALE', '$2a$10$testpassword', 'MEMBER', 0);

-- Sample member with face_id
INSERT INTO members (id, face_id, join_date, membership, status) VALUES
(1001, 'FACE001', '2025-01-01 00:00:00.000000', 'Premium', 'Active');

-- Sample member embeddings (if you have embeddings data)
-- INSERT INTO MemberEmbeddings (member_id, embeddings, model_name) VALUES
-- (1001, <embeddings_blob>, 'ArcFace');
*/