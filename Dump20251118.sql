-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: gymsystem
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE gympool;

--
-- Table structure for table `access_logs`
--

DROP TABLE IF EXISTS `access_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `access_time` datetime(6) NOT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdk3wj918ygucveg1qxynfv4a5` (`member_id`),
  CONSTRAINT `FKdk3wj918ygucveg1qxynfv4a5` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_logs`
--

LOCK TABLES `access_logs` WRITE;
/*!40000 ALTER TABLE `access_logs` DISABLE KEYS */;
INSERT INTO `access_logs` VALUES (1,'2025-12-09 11:29:36.000000',3),(2,'2025-12-09 11:35:00.000000',6);
/*!40000 ALTER TABLE `access_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `access_logs`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `check_in_time` datetime(6) NOT NULL,
  `check_out_time` datetime(6) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdk3wj918ygucveg1qxynfv4a5` (`member_id`),
  CONSTRAINT `FKdk3wj918ygucveg1qxynfv4a5` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` datetime(6) NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `payment_status` varchar(50) NOT NULL,
  `total` double NOT NULL,
  `issuedcoupon_id` bigint DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  `receptionist_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfm6hcm13whj1tebds24byqwot` (`issuedcoupon_id`),
  KEY `FKonro5jn4au67k8m3bmj2ru1l2` (`member_id`),
  KEY `FKe7be3f2kodvg2gbr2jo3sw541` (`receptionist_id`),
  CONSTRAINT `FKe7be3f2kodvg2gbr2jo3sw541` FOREIGN KEY (`receptionist_id`) REFERENCES `receptionist` (`id`),
  CONSTRAINT `FKfm6hcm13whj1tebds24byqwot` FOREIGN KEY (`issuedcoupon_id`) REFERENCES `issued_coupon` (`id`),
  CONSTRAINT `FKonro5jn4au67k8m3bmj2ru1l2` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill`
--

LOCK TABLES `bill` WRITE;
/*!40000 ALTER TABLE `bill` DISABLE KEYS */;
INSERT INTO `bill` VALUES (1,'2025-12-02 21:50:03.544000','CASH','PAID',3500,1,3,2),(2,'2025-12-08 21:31:03.927000','CASH','PAID',1000,NULL,3,2),(3,'2025-12-09 21:22:35.825000','BANKING','PAID',4000,NULL,3,2);
/*!40000 ALTER TABLE `bill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_registration`
--

DROP TABLE IF EXISTS `class_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `class_template_id` bigint NOT NULL,
  `staff_id` bigint NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf0d3i5tnvtwmw9ry4bjbp6pan` (`class_template_id`),
  KEY `FKc5whc1ic8lpt70o0r6cnbbq7l` (`staff_id`),
  KEY `FKhyuwlmprhvccyh7hmw6y3pwu1` (`teacher_id`),
  CONSTRAINT `FKc5whc1ic8lpt70o0r6cnbbq7l` FOREIGN KEY (`staff_id`) REFERENCES `teacher` (`id`),
  CONSTRAINT `FKf0d3i5tnvtwmw9ry4bjbp6pan` FOREIGN KEY (`class_template_id`) REFERENCES `fitness_class` (`id`),
  CONSTRAINT `FKhyuwlmprhvccyh7hmw6y3pwu1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_registration`
--

LOCK TABLES `class_registration` WRITE;
/*!40000 ALTER TABLE `class_registration` DISABLE KEYS */;
INSERT INTO `class_registration` VALUES (1,'Đăng ký giảng dạy khóa Yoga cơ bản cho giáo viên có ID 4',1,4,NULL,4),(2,'Pending approval',2,4,NULL,4);
/*!40000 ALTER TABLE `class_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classschedule`
--

DROP TABLE IF EXISTS `classschedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classschedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `capacity` int DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `fitness_class_id` bigint DEFAULT NULL,
  `room_id` bigint DEFAULT NULL,
  `schedule_pattern_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrll65rkafgc5p6j4yl83ld5ya` (`fitness_class_id`),
  KEY `FKma7fv8alkxxcy1w8wus721n6x` (`room_id`),
  KEY `FK8wcgsgex8y5ikmhoh9eopsm09` (`schedule_pattern_id`),
  CONSTRAINT `FK8wcgsgex8y5ikmhoh9eopsm09` FOREIGN KEY (`schedule_pattern_id`) REFERENCES `schedule_pattern` (`id`),
  CONSTRAINT `FKma7fv8alkxxcy1w8wus721n6x` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
  CONSTRAINT `FKrll65rkafgc5p6j4yl83ld5ya` FOREIGN KEY (`fitness_class_id`) REFERENCES `fitness_class` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classschedule`
--

LOCK TABLES `classschedule` WRITE;
/*!40000 ALTER TABLE `classschedule` DISABLE KEYS */;
INSERT INTO `classschedule` VALUES (1,20,'2025-12-04 13:00:00.000000','2025-12-04 12:00:00.000000','OPEN',1,1,1),(2,20,'2025-12-11 13:00:00.000000','2025-12-11 12:00:00.000000','OPEN',1,1,1),(3,20,'2025-12-18 13:00:00.000000','2025-12-18 12:00:00.000000','OPEN',1,1,1),(4,20,'2025-12-25 13:00:00.000000','2025-12-25 12:00:00.000000','OPEN',1,1,1),(5,20,'2025-12-05 14:00:00.000000','2025-12-05 13:00:00.000000','OPEN',2,1,2),(6,20,'2025-12-12 14:00:00.000000','2025-12-12 13:00:00.000000','OPEN',2,1,2),(7,20,'2025-12-19 14:00:00.000000','2025-12-19 13:00:00.000000','OPEN',2,1,2),(8,20,'2025-12-26 14:00:00.000000','2025-12-26 13:00:00.000000','OPEN',2,1,2),(9,10,'2025-12-01 14:00:00.000000','2025-12-01 13:00:00.000000','OPEN',3,2,3),(10,10,'2025-12-08 14:00:00.000000','2025-12-08 13:00:00.000000','OPEN',3,2,3),(11,10,'2025-12-15 14:00:00.000000','2025-12-15 13:00:00.000000','OPEN',3,2,3),(12,10,'2025-12-22 14:00:00.000000','2025-12-22 13:00:00.000000','OPEN',3,2,3),(13,10,'2025-12-29 14:00:00.000000','2025-12-29 13:00:00.000000','OPEN',3,2,3),(14,20,'2026-01-02 14:00:00.000000','2026-01-02 13:00:00.000000','OPEN',2,1,4),(15,20,'2026-01-09 14:00:00.000000','2026-01-09 13:00:00.000000','OPEN',2,1,4),(16,20,'2026-01-16 14:00:00.000000','2026-01-16 13:00:00.000000','OPEN',2,1,4),(17,20,'2026-01-23 14:00:00.000000','2026-01-23 13:00:00.000000','OPEN',2,1,4),(18,20,'2026-01-30 14:00:00.000000','2026-01-30 13:00:00.000000','OPEN',2,1,4),(19,20,'2025-12-03 14:00:00.000000','2025-12-03 13:00:00.000000','OPEN',1,1,5),(20,20,'2025-12-10 14:00:00.000000','2025-12-10 13:00:00.000000','OPEN',1,1,5),(21,20,'2025-12-17 14:00:00.000000','2025-12-17 13:00:00.000000','OPEN',1,1,5),(22,20,'2025-12-24 14:00:00.000000','2025-12-24 13:00:00.000000','OPEN',1,1,5),(23,20,'2025-12-01 08:30:00.000000','2025-12-01 07:00:00.000000','OPEN',4,1,6),(24,20,'2025-12-08 08:30:00.000000','2025-12-08 07:00:00.000000','OPEN',4,1,6),(25,20,'2025-12-15 08:30:00.000000','2025-12-15 07:00:00.000000','OPEN',4,1,6),(26,20,'2025-12-22 08:30:00.000000','2025-12-22 07:00:00.000000','OPEN',4,1,6),(27,20,'2025-12-29 08:30:00.000000','2025-12-29 07:00:00.000000','OPEN',4,1,6);
/*!40000 ALTER TABLE `classschedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configs`
--

DROP TABLE IF EXISTS `configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_public` bit(1) DEFAULT NULL,
  `config_key` varchar(255) DEFAULT NULL,
  `value` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configs`
--

LOCK TABLES `configs` WRITE;
/*!40000 ALTER TABLE `configs` DISABLE KEYS */;
INSERT INTO `configs` VALUES (1,_binary '\0','banner','/image/banners/20251209_211848_NCCHN3_-_Đào_Quang_Hưng_-_Intern.jpg.jpg');
/*!40000 ALTER TABLE `configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `discount_type` varchar(255) DEFAULT NULL,
  `discount_value` double DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_uses` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (1,'GYM01','PERCENTAGE',50,'2026-02-12 07:00:00.000000','Basic','2025-02-12 07:00:00.000000','ACTIVE',NULL),(2,'GYM1','FIXED_AMOUNT',1000,'2003-11-28 07:00:00.000000',NULL,'2006-08-07 07:00:00.000000','ACTIVE',33);
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_membership`
--

DROP TABLE IF EXISTS `customer_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_membership` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_date` datetime(6) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `member_id` bigint NOT NULL,
  `plan_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbb9ebigogyjag1r07rdstb2sc` (`member_id`),
  KEY `FKl8q48lhlny94u78mj35je94ts` (`plan_id`),
  CONSTRAINT `FKbb9ebigogyjag1r07rdstb2sc` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FKl8q48lhlny94u78mj35je94ts` FOREIGN KEY (`plan_id`) REFERENCES `membership_plan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_membership`
--

LOCK TABLES `customer_membership` WRITE;
/*!40000 ALTER TABLE `customer_membership` DISABLE KEYS */;
INSERT INTO `customer_membership` VALUES (1,'2026-01-15 00:00:00.000000','2025-01-15 00:00:00.000000','Active',3,1),(2,'2009-02-24 07:00:00.000000','2009-01-24 07:00:00.000000','Active',6,2),(3,'2026-01-01 07:00:00.000000','2025-12-01 07:00:00.000000','Active',8,1);
/*!40000 ALTER TABLE `customer_membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrg5vwwmqtc5xvrku1gndtoclp` (`member_id`),
  CONSTRAINT `FKrg5vwwmqtc5xvrku1gndtoclp` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback_attachments`
--

DROP TABLE IF EXISTS `feedback_attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) NOT NULL,
  `file_size` bigint DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `feedback_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKctq38barkc53t8kpxwejkqa2x` (`feedback_id`),
  CONSTRAINT `FKctq38barkc53t8kpxwejkqa2x` FOREIGN KEY (`feedback_id`) REFERENCES `feedback` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback_attachments`
--

LOCK TABLES `feedback_attachments` WRITE;
/*!40000 ALTER TABLE `feedback_attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback_attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fitness_class`
--

DROP TABLE IF EXISTS `fitness_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fitness_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` text,
  `difficulty_level` varchar(50) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fitness_class`
--

LOCK TABLES `fitness_class` WRITE;
/*!40000 ALTER TABLE `fitness_class` DISABLE KEYS */;
INSERT INTO `fitness_class` VALUES (1,'','BEGINNER','Yoga Thursday 9pm','ACTIVE'),(2,'Gym Friday','BEGINNER','Gym Friday','ACTIVE'),(3,'Cardio Monday','INTERMEDIATE','Cardio Monday','ACTIVE'),(4,'Volvo Monday','Beginner','Volvo Monday','ACTIVE');
/*!40000 ALTER TABLE `fitness_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `import_bills`
--

DROP TABLE IF EXISTS `import_bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `import_bills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` datetime(6) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `manager_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKao72nt6hraqdgibljo1ynjwf9` (`manager_id`),
  KEY `FKkf17st5r6dawedwsjltt1gqbq` (`provider_id`),
  CONSTRAINT `FKao72nt6hraqdgibljo1ynjwf9` FOREIGN KEY (`manager_id`) REFERENCES `manager` (`id`),
  CONSTRAINT `FKkf17st5r6dawedwsjltt1gqbq` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_bills`
--

LOCK TABLES `import_bills` WRITE;
/*!40000 ALTER TABLE `import_bills` DISABLE KEYS */;
INSERT INTO `import_bills` VALUES (1,'2025-12-08 21:30:05.749000',0,1,1),(2,'2025-12-09 21:14:11.406000',0,1,6);
/*!40000 ALTER TABLE `import_bills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imported_products`
--

DROP TABLE IF EXISTS `imported_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `imported_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `import_price` double DEFAULT NULL,
  `quantity` int NOT NULL,
  `import_bill_id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1f4c5c0irjyxv907t96hull7i` (`import_bill_id`),
  KEY `FKjsww4dcpgg2kiqk0cu71p6byi` (`product_id`),
  CONSTRAINT `FK1f4c5c0irjyxv907t96hull7i` FOREIGN KEY (`import_bill_id`) REFERENCES `import_bills` (`id`),
  CONSTRAINT `FKjsww4dcpgg2kiqk0cu71p6byi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imported_products`
--

LOCK TABLES `imported_products` WRITE;
/*!40000 ALTER TABLE `imported_products` DISABLE KEYS */;
INSERT INTO `imported_products` VALUES (1,0,1,1,2),(2,0,1,1,7),(3,0,5,2,2);
/*!40000 ALTER TABLE `imported_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `issued_coupon`
--

DROP TABLE IF EXISTS `issued_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issued_coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `remaining_uses` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `coupon_id` bigint DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6tlq06yimib5ocxhp6qw0iemg` (`coupon_id`),
  KEY `FK1i913ss5i5pqyq9ja4fela7nb` (`member_id`),
  CONSTRAINT `FK1i913ss5i5pqyq9ja4fela7nb` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FK6tlq06yimib5ocxhp6qw0iemg` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issued_coupon`
--

LOCK TABLES `issued_coupon` WRITE;
/*!40000 ALTER TABLE `issued_coupon` DISABLE KEYS */;
INSERT INTO `issued_coupon` VALUES (1,2,'AVAILABLE',1,3),(2,33,'AVAILABLE',2,3);
/*!40000 ALTER TABLE `issued_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKmqwhyh7lyvaoxegkx6nwj5u43` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES (1);
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_registration`
--

DROP TABLE IF EXISTS `member_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follow_date` datetime(6) DEFAULT NULL,
  `class_schedule_id` bigint NOT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1urb4hfw516cgqpd7b8m2e76e` (`class_schedule_id`),
  KEY `FKpiund9a4woixgb9k6wyrbddnt` (`member_id`),
  CONSTRAINT `FK1urb4hfw516cgqpd7b8m2e76e` FOREIGN KEY (`class_schedule_id`) REFERENCES `classschedule` (`id`),
  CONSTRAINT `FKpiund9a4woixgb9k6wyrbddnt` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_registration`
--

LOCK TABLES `member_registration` WRITE;
/*!40000 ALTER TABLE `member_registration` DISABLE KEYS */;
INSERT INTO `member_registration` VALUES (1,'2025-12-07 18:35:20.994000',2,3),(2,'2025-12-07 18:35:21.015000',3,3),(3,'2025-12-07 21:12:17.418000',4,3),(4,'2025-12-08 17:28:11.580000',6,3),(5,'2025-12-08 17:28:11.594000',7,3),(6,'2025-12-08 17:28:11.608000',8,3),(7,'2025-12-09 21:20:14.406000',14,3);
/*!40000 ALTER TABLE `member_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_face_embeddings`
--

DROP TABLE IF EXISTS `member_face_embeddings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_face_embeddings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `embeddings` longblob NOT NULL,
  `model_name` varchar(50) DEFAULT 'ArcFace',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_member_embedding` (`member_id`),
  KEY `idx_member_embeddings_created` (`created_at`),
  CONSTRAINT `fk_member_face_embeddings_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_face_embeddings`
--

LOCK TABLES `member_face_embeddings` WRITE;
/*!40000 ALTER TABLE `member_face_embeddings` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_face_embeddings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `members`
--

DROP TABLE IF EXISTS `members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `members` (
  `card_id` varchar(255) DEFAULT NULL,
  `face_id` varchar(255) DEFAULT NULL,
  `join_date` datetime(6) DEFAULT NULL,
  `membership` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK8xc8ex06k91fyx0v7hqafk62u` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `members`
--

LOCK TABLES `members` WRITE;
/*!40000 ALTER TABLE `members` DISABLE KEYS */;
INSERT INTO `members` VALUES (NULL,NULL,'2025-12-08 21:32:26.120000','VIP','Active',3),(NULL,NULL,'2025-12-08 21:32:26.120000','VIP','Active',6),(NULL,NULL,'2025-12-09 21:26:44.793000','Basic','Active',8);
/*!40000 ALTER TABLE `members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_plan`
--

DROP TABLE IF EXISTS `membership_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `benefits` varchar(255) DEFAULT NULL,
  `duration` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tier_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl06i110eke0eciahdfimh4ln2` (`tier_id`),
  CONSTRAINT `FKl06i110eke0eciahdfimh4ln2` FOREIGN KEY (`tier_id`) REFERENCES `membership_tier` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plan`
--

LOCK TABLES `membership_plan` WRITE;
/*!40000 ALTER TABLE `membership_plan` DISABLE KEYS */;
INSERT INTO `membership_plan` VALUES (1,'','1 Months','Basic 1 month',1000,NULL,1),(2,'','1 Months','VIP 1 month',2000,NULL,2),(3,'hehehe','1 Months','Standard 1 month',1000,NULL,3);
/*!40000 ALTER TABLE `membership_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_tier`
--

DROP TABLE IF EXISTS `membership_tier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membership_tier` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_tier`
--

LOCK TABLES `membership_tier` WRITE;
/*!40000 ALTER TABLE `membership_tier` DISABLE KEYS */;
INSERT INTO `membership_tier` VALUES (1,'Basic',5,'Active'),(2,'VIP',1,'Active'),(3,'Standard',3,'Active');
/*!40000 ALTER TABLE `membership_tier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(50) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `price` double NOT NULL,
  `quantity` int DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `type` varchar(50) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `import_date` datetime(6) DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Adidas','T-shirt gym',1000,10,0,'clothes',NULL,NULL,NULL),(2,'Whey','Whey powder',1000,25,0,'Supplement',NULL,NULL,NULL),(3,'Cocacola','Ice+ Peach',1000,20,1,'drinks',NULL,NULL,NULL),(4,NULL,'PT 1 month',1000,NULL,0,'PT',NULL,NULL,NULL),(5,NULL,'Basic',1000,NULL,0,'Membership',NULL,NULL,NULL),(6,NULL,'VIP',2000,NULL,0,'Membership',NULL,NULL,NULL),(7,'','Gói thành viên Basic',0,2,0,'Membership','image/defaults/no-image.png','2025-12-08 21:30:05.710000','Gói');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `providers`
--

DROP TABLE IF EXISTS `providers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `providers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `providers`
--

LOCK TABLES `providers` WRITE;
/*!40000 ALTER TABLE `providers` DISABLE KEYS */;
INSERT INTO `providers` VALUES (1,'Nike','Hoàng Gia Sport','0905123456','duong pho','dmngoclongngu@gmail.com'),(2,'Adidas','Thể Thao Việt','0987123456',NULL,NULL),(3,'Optimum Nutrition','Gym Pro Supplier','0932123456',NULL,NULL),(4,'Under Armour','ABC Fitness','0912345678',NULL,NULL),(5,'Dymatize','Thực phẩm thể hình HN','0976123456',NULL,NULL),(6,'Nike','Unde dolorum quos re','+1 (267) 244-1165','Veritatis laborum A','zywico@mailinator.com');
/*!40000 ALTER TABLE `providers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pt`
--

DROP TABLE IF EXISTS `pt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pt` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKmdhig1h3agasagq2tdc2q8enr` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt`
--

LOCK TABLES `pt` WRITE;
/*!40000 ALTER TABLE `pt` DISABLE KEYS */;
INSERT INTO `pt` VALUES (5);
/*!40000 ALTER TABLE `pt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pt_appointment`
--

DROP TABLE IF EXISTS `pt_appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pt_appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) DEFAULT NULL,
  `notification_sent` bit(1) NOT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `pt_id` bigint NOT NULL,
  `package_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa5na1x3h6pu3ke2oenun37gk3` (`pt_id`),
  KEY `FKhoalkw31q4jfsq53cgrlb69r` (`package_id`),
  CONSTRAINT `FKa5na1x3h6pu3ke2oenun37gk3` FOREIGN KEY (`pt_id`) REFERENCES `pt` (`id`),
  CONSTRAINT `FKhoalkw31q4jfsq53cgrlb69r` FOREIGN KEY (`package_id`) REFERENCES `pt_package_issued` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_appointment`
--

LOCK TABLES `pt_appointment` WRITE;
/*!40000 ALTER TABLE `pt_appointment` DISABLE KEYS */;
INSERT INTO `pt_appointment` VALUES (1,'2025-12-15 11:00:00.000000',_binary '\0','2025-12-15 10:00:00.000000','Scheduled',5,1),(2,'2025-12-15 15:00:00.000000',_binary '\0','2025-12-15 14:00:00.000000','Scheduled',5,2),(3,'2025-12-08 09:00:00.000000',_binary '','2025-12-08 08:00:00.000000','Completed',5,1),(4,'2025-12-09 09:00:00.000000',_binary '','2025-12-09 08:00:00.000000','Completed',5,1);
/*!40000 ALTER TABLE `pt_appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pt_package`
--

DROP TABLE IF EXISTS `pt_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pt_package` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `sessions` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_package`
--

LOCK TABLES `pt_package` WRITE;
/*!40000 ALTER TABLE `pt_package` DISABLE KEYS */;
INSERT INTO `pt_package` VALUES (1,'Gói Cơ Bản (12 Buổi)',12,'Active'),(2,'Gói Tăng Cơ (24 Buổi)',24,'Active'),(3,'Gói Giảm Mỡ Cấp Tốc (36 Buổi)',36,'Active');
/*!40000 ALTER TABLE `pt_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pt_package_issued`
--

DROP TABLE IF EXISTS `pt_package_issued`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pt_package_issued` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `remaining_sessions` int DEFAULT NULL,
  `member_id` bigint NOT NULL,
  `pt_id` bigint NOT NULL,
  `package_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeb267euikrhk2ml99ykuyaooy` (`member_id`),
  KEY `FKqm5dc9ym8vpjw8bn45lhhuew3` (`pt_id`),
  KEY `FK8amxa4fq0y9i3j5wikp3mn5v7` (`package_id`),
  CONSTRAINT `FK8amxa4fq0y9i3j5wikp3mn5v7` FOREIGN KEY (`package_id`) REFERENCES `pt_package` (`id`),
  CONSTRAINT `FKeb267euikrhk2ml99ykuyaooy` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FKqm5dc9ym8vpjw8bn45lhhuew3` FOREIGN KEY (`pt_id`) REFERENCES `pt` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_package_issued`
--

LOCK TABLES `pt_package_issued` WRITE;
/*!40000 ALTER TABLE `pt_package_issued` DISABLE KEYS */;
INSERT INTO `pt_package_issued` VALUES (1,12,3,5,1),(2,24,6,5,2),(3,20,3,5,2);
/*!40000 ALTER TABLE `pt_package_issued` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receptionist`
--

DROP TABLE IF EXISTS `receptionist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receptionist` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK57lyax49639qfih0ippbkyhac` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receptionist`
--

LOCK TABLES `receptionist` WRITE;
/*!40000 ALTER TABLE `receptionist` DISABLE KEYS */;
INSERT INTO `receptionist` VALUES (2),(7);
/*!40000 ALTER TABLE `receptionist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKghpmfn23vmxfu3spu3lfg4r2d` (`token`),
  UNIQUE KEY `UK7tdcd6ab5wsgoudnvj7xf1b7l` (`user_id`),
  CONSTRAINT `FK1lih5y2npsf8u5o3vhdb9y0os` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (86,'2025-12-16 14:21:15.526696','bca6f61f-30df-4672-831e-91f86d931f28',2),(89,'2025-12-16 14:30:40.716668','0a55b189-e9f3-451f-b418-c28c19628da8',5),(106,'2025-12-17 14:53:10.878533','328ca0a5-ed63-4429-a3ae-544a59c49013',3),(110,'2025-12-19 02:21:08.515455','8451e425-b54a-4e02-938f-f9152fdea938',4),(111,'2025-12-19 08:50:11.847896','dae973b2-f5ef-4f62-b153-94cf464673a2',1);
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `capacity` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'Ground Floor, Section A','Cardio Zone','Area dedicated to treadmills, ellipticals, and stationary bikes.',NULL,NULL),(2,'Ground Floor, Section B','Free Weights Area','Contains dumbbells, barbells, and squat racks.',NULL,NULL),(3,'First Floor, East Wing','Studio 1','Used for Yoga, Pilates, and Spinning classes.',NULL,NULL),(4,'Basement Level','Mens Locker Room','Storage and changing facilities for male members.',NULL,NULL),(5,'First Floor, West Wing','Admin Office','Room for gym staff and management.',20,NULL),(6,NULL,'Phòng tầng 1 bên trái',NULL,20,'Phòng tầng 1 bên trái');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule_pattern`
--

DROP TABLE IF EXISTS `schedule_pattern`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule_pattern` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_end_date` date NOT NULL,
  `class_start_date` date NOT NULL,
  `days_of_week` varchar(255) DEFAULT NULL,
  `time_end` time(6) NOT NULL,
  `time_start` time(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule_pattern`
--

LOCK TABLES `schedule_pattern` WRITE;
/*!40000 ALTER TABLE `schedule_pattern` DISABLE KEYS */;
INSERT INTO `schedule_pattern` VALUES (1,'2025-12-31','2025-12-01','THURSDAY','12:00:00.000000','21:00:00.000000'),(2,'2025-12-30','2025-12-01','FRIDAY','14:00:00.000000','13:00:00.000000'),(3,'2025-12-30','2025-12-01','MONDAY','14:00:00.000000','13:00:00.000000'),(4,'2026-01-30','2026-01-01','FRIDAY','14:00:00.000000','13:00:00.000000'),(5,'2025-12-30','2025-12-01','WEDNESDAY','14:00:00.000000','13:00:00.000000'),(6,'2025-12-30','2025-12-01','MONDAY','08:30:00.000000','07:00:00.000000');
/*!40000 ALTER TABLE `schedule_pattern` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sold_product`
--

DROP TABLE IF EXISTS `sold_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sold_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `sold_price` double DEFAULT NULL,
  `bill_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKex0sc04uw5ojgek6k4gd28l2h` (`bill_id`),
  KEY `FKa1mbo3owjt7o4tq95p5k1j3a1` (`product_id`),
  CONSTRAINT `FKa1mbo3owjt7o4tq95p5k1j3a1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKex0sc04uw5ojgek6k4gd28l2h` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sold_product`
--

LOCK TABLES `sold_product` WRITE;
/*!40000 ALTER TABLE `sold_product` DISABLE KEYS */;
INSERT INTO `sold_product` VALUES (1,6,1000,1,1),(2,1,1000,1,2),(3,1,1000,2,4),(4,4,1000,3,1);
/*!40000 ALTER TABLE `sold_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_class_attendance`
--

DROP TABLE IF EXISTS `student_class_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_class_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checked_in_at` datetime(6) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `checked_in_by_id` bigint DEFAULT NULL,
  `class_schedule_id` bigint NOT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi20d7q81ad5wxcln6kbujlqaq` (`checked_in_by_id`),
  KEY `FKgoylwje8271ykbeb6ynynxeaf` (`class_schedule_id`),
  KEY `FKohi3ajmsbim5my38uvjo6dwo5` (`member_id`),
  CONSTRAINT `FKgoylwje8271ykbeb6ynynxeaf` FOREIGN KEY (`class_schedule_id`) REFERENCES `classschedule` (`id`),
  CONSTRAINT `FKi20d7q81ad5wxcln6kbujlqaq` FOREIGN KEY (`checked_in_by_id`) REFERENCES `teacher` (`id`),
  CONSTRAINT `FKohi3ajmsbim5my38uvjo6dwo5` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_class_attendance`
--

LOCK TABLES `student_class_attendance` WRITE;
/*!40000 ALTER TABLE `student_class_attendance` DISABLE KEYS */;
INSERT INTO `student_class_attendance` VALUES (1,'2025-12-10 14:24:02.805008',NULL,'PRESENT',4,2,3);
/*!40000 ALTER TABLE `student_class_attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_profile`
--

DROP TABLE IF EXISTS `student_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `height` float DEFAULT NULL,
  `training_plan` varchar(255) DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `member_id` bigint NOT NULL,
  `pt_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtq5alsq2hqepysabml44aoj1p` (`member_id`),
  KEY `FKjgq3aa0hwbayu16y2nyj3c2du` (`pt_id`),
  CONSTRAINT `FKjgq3aa0hwbayu16y2nyj3c2du` FOREIGN KEY (`pt_id`) REFERENCES `pt` (`id`),
  CONSTRAINT `FKtq5alsq2hqepysabml44aoj1p` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_profile`
--

LOCK TABLES `student_profile` WRITE;
/*!40000 ALTER TABLE `student_profile` DISABLE KEYS */;
INSERT INTO `student_profile` VALUES (1,1.75,'Tập luyện tăng cơ 4 buổi/tuần, dinh dưỡng Calo thặng dư',70.5,3,5),(2,1.63,'Giảm mỡ cấp tốc 3 buổi/tuần, Cardio xen kẽ',65,6,5);
/*!40000 ALTER TABLE `student_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `position` varchar(255) DEFAULT NULL,
  `specialize` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKspqy2s83gvl6g8nt5aubls90k` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
INSERT INTO `teacher` VALUES (NULL,NULL,4),(NULL,NULL,9),('yoga','yoga',10);
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_session`
--

DROP TABLE IF EXISTS `training_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `start_at` datetime(6) DEFAULT NULL,
  `appointment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK541ushli26fm1u9p6xy08ac33` (`appointment_id`),
  CONSTRAINT `FK541ushli26fm1u9p6xy08ac33` FOREIGN KEY (`appointment_id`) REFERENCES `pt_appointment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_session`
--

LOCK TABLES `training_session` WRITE;
/*!40000 ALTER TABLE `training_session` DISABLE KEYS */;
INSERT INTO `training_session` VALUES (1,'2025-12-08 08:58:00.000000','Tập chân (Leg Day). Member có tiến bộ trong squat form. Tăng 5kg tạ cho bài Leg Press.','2025-12-08 08:02:00.000000',3),(2,'2025-12-15 11:00:00.000000','Kế hoạch: Tập ngực và tay. Bắt đầu bằng Barbell Bench Press.','2025-12-15 10:00:00.000000',1);
/*!40000 ALTER TABLE `training_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dob` datetime(6) NOT NULL,
  `email` varchar(50) NOT NULL,
  `full_name` varchar(50) NOT NULL,
  `gender` varchar(50) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `password` varchar(255) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'1980-05-15 07:00:00.000000','manager@example.com','Tran Van B','Male',0,'$2a$10$alTBb/fNyxE2g4Wt1xeOLesTV1XhKzXmQ1UhUAi9QFZiv2nt0LzKy','0123456789','MANAGER'),(2,'2003-01-01 07:00:00.000000','rec1@gmail.com','Nguyen Ba Duong','Male',0,'$2a$10$tnYLJAtPSZ3nAOfAHDpAuOd97VlFAFJXzqA3s4kEuLWNwq2wHtBRS','113','RECEPTIONIST'),(3,'1999-12-10 07:00:00.000000','member01@gmail.com','Do Van E','MALE',0,'$2a$10$JaB39GiciFDE6afHP3BVm.u8g6eF7jwxsL6vZXHaGnKVGEyi1aQgW','0979123456','MEMBER'),(4,'2003-01-01 07:00:00.000000','teacher01@gmail.com','Đào Quang Hưng','Male',0,'$2a$10$aJkUuiNy4J5lVcmoAfCTYOCJ8cFZItHat/hLg1vjuGaykZJTKwTji','123456789','TEACHER'),(5,'2004-01-13 07:00:00.000000','pt01@gmail.com','Exercitationem enim ','Male',0,'$2a$10$yXWwSfxaiTA3NxU5zmFHQePACGTZGdqpsYaeoeiOENX.DaTpU3uWK','+1 (235) 947-5657','PT'),(6,'1985-04-23 07:00:00.000000','member02@gmail.com','Ea aspernatur laudan','Female',0,'$2a$10$rqnOILVdoqmp6TS/.aO3kuEARUTjPf.kn7v98JOaC7hgYu7YFPyQG','+1 (109) 175-4136','MEMBER'),(7,'1990-02-11 07:00:00.000000','rec2@gmail.com','Aut sunt consequatu','Female',0,'$2a$10$mw7qRok1cZRql9Df0VG7F.D/sI2NhUeswTY3f/nEiDoiwcS02VyTK','+1 (319) 159-7746','RECEPTIONIST'),(8,'1990-08-01 07:00:00.000000','birong@gmail.com','Magna quia iure temp','Female',0,'$2a$10$yEXehISIHWfnI8sejp0iKueCA/FRKGrosmL.aWe1yrh4jH4avqkMO','+1 (917) 402-5135','MEMBER'),(9,'2012-09-12 07:00:00.000000','teacher02@gmail.com','Veritatis ducimus c','Female',0,'$2a$10$uTL3zd1qOlxNZIZVkOnK1.4KZGtT0AAukx89Ld9bWRSt05KeBJh5m','+1 (376) 907-6134','TEACHER'),(10,'1994-05-25 07:00:00.000000','teacher3@gmail.com','Anim et rerum et nes','Female',0,'$2a$10$80EUpYP8nvQaWQ5JgFI7tuNldpH7iLP0aAK2z8hotRQcxd4ZnHA7W','+1 (766) 591-8608','TEACHER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee_face_embeddings`
--

DROP TABLE IF EXISTS `employee_face_embeddings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_face_embeddings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `embeddings` longblob NOT NULL,
  `model_name` varchar(50) DEFAULT 'ArcFace',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_employee_embedding` (`user_id`),
  KEY `idx_employee_embeddings_created` (`created_at`),
  CONSTRAINT `fk_employee_face_embeddings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee_face_embeddings`
--

LOCK TABLES `employee_face_embeddings` WRITE;
/*!40000 ALTER TABLE `employee_face_embeddings` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee_face_embeddings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-13 15:45:06
