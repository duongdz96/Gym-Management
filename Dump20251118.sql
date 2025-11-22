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

--
-- Table structure for table `access_logs`
--

DROP TABLE IF EXISTS `access_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `access_method` varchar(20) NOT NULL,
  `access_time` datetime(6) NOT NULL,
  `device_id` varchar(50) NOT NULL,
  `location_type` varchar(50) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `result` varchar(20) NOT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdk3wj918ygucveg1qxynfv4a5` (`member_id`),
  CONSTRAINT `FKdk3wj918ygucveg1qxynfv4a5` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_logs`
--

LOCK TABLES `access_logs` WRITE;
/*!40000 ALTER TABLE `access_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `access_logs` ENABLE KEYS */;
UNLOCK TABLES;

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
  `receptionist_id` bigint NOT NULL,
  `issuedcoupon_id` bigint DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe7be3f2kodvg2gbr2jo3sw541` (`receptionist_id`),
  KEY `FKfm6hcm13whj1tebds24byqwot` (`issuedcoupon_id`),
  KEY `FKonro5jn4au67k8m3bmj2ru1l2` (`member_id`),
  CONSTRAINT `FKe7be3f2kodvg2gbr2jo3sw541` FOREIGN KEY (`receptionist_id`) REFERENCES `receptionist` (`id`),
  CONSTRAINT `FKfm6hcm13whj1tebds24byqwot` FOREIGN KEY (`issuedcoupon_id`) REFERENCES `issued_coupon` (`id`),
  CONSTRAINT `FKonro5jn4au67k8m3bmj2ru1l2` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill`
--

LOCK TABLES `bill` WRITE;
/*!40000 ALTER TABLE `bill` DISABLE KEYS */;
INSERT INTO `bill` VALUES (1,'2025-09-30 23:00:00.000000','CASH','PAID',2900000,3,NULL,NULL),(2,'2025-10-01 01:30:00.000000','BANKING','PAID',990000,3,NULL,NULL),(3,'2025-10-01 16:00:00.000000','CARD','PAID',3580000,3,NULL,NULL),(4,'2025-10-01 22:30:00.000000','CASH','PENDING',0,3,NULL,NULL),(5,'2025-10-03 23:01:21.641000','CARD','COMPLETE',1180000,3,NULL,NULL),(6,'2025-10-06 17:50:51.925000','BANKING','PAID',680000,3,NULL,NULL),(7,'2025-10-06 21:26:54.091000','CARD','PAID',1180000,3,NULL,NULL),(8,'2025-10-07 14:58:11.038000','CASH','PAID',1850000,3,NULL,NULL),(9,'2025-10-01 22:30:00.000000','CASH','PENDING',425000,3,1,1),(10,'2025-10-30 21:20:20.417000','CARD','PAID',2100000,3,NULL,7),(11,'2025-10-30 21:26:13.673000','CASH','PAID',250000,3,NULL,1),(12,'2025-10-31 08:31:05.992000','CASH','PAID',1300000,3,NULL,1),(13,'2025-11-07 16:12:10.937000','CARD','PAID',750000,3,NULL,1),(14,'2025-11-13 21:31:49.224000','CASH','PAID',250000,3,NULL,1),(15,'2025-11-13 21:33:07.304000','CASH','PAID',1200000,3,NULL,1);
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
  PRIMARY KEY (`id`),
  KEY `FK50pjec4nrlas4himt2kjn2kty` (`class_template_id`),
  KEY `FKpepp4wbsjr6by4kqnvcpenfrm` (`staff_id`),
  CONSTRAINT `FK50pjec4nrlas4himt2kjn2kty` FOREIGN KEY (`class_template_id`) REFERENCES `class_template` (`id`),
  CONSTRAINT `FKpepp4wbsjr6by4kqnvcpenfrm` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_registration`
--

LOCK TABLES `class_registration` WRITE;
/*!40000 ALTER TABLE `class_registration` DISABLE KEYS */;
INSERT INTO `class_registration` VALUES (1,'Staff id=5 phụ trách lớp bơi cơ bản',2,5),(5,'Staff ID 2 registered for class Bơi cơ bản',4,2),(7,'Staff ID 2 registered for class Yoga cơ bản',1,2),(8,'Staff ID 2 registered for class Nhảy nâng cao',5,2);
/*!40000 ALTER TABLE `class_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_template`
--

DROP TABLE IF EXISTS `class_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` text,
  `difficulty_level` varchar(50) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_template`
--

LOCK TABLES `class_template` WRITE;
/*!40000 ALTER TABLE `class_template` DISABLE KEYS */;
INSERT INTO `class_template` VALUES (1,'Lớp yoga dành cho người mới bắt đầu, tập trung vào các động tác căn bản và thở.','BEGINNER','Yoga cơ bản','ACTIVE'),(2,'Lớp tập tạ nhóm, kết hợp nhiều động tác tăng sức mạnh và sức bền.','ADVANCED','BodyPump nâng cao','ACTIVE'),(3,'Lớp nhảy zumba sôi động, giúp đốt cháy calo và cải thiện tim mạch.','INTERMEDIATE','Zumba buổi tối','INACTIVE'),(4,'Lớp học bơi cho trẻ em và người lớn chưa biết bơi.','BEGINNER','Bơi cơ bản','ACTIVE'),(5,'Nhảy nâng cao đặc biệt','INTERMEDIATE','Nhảy nâng cao','ACTIVE'),(6,'ke chuyen ma','BEGINNER','Kể chuyện đêm muộn','ACTIVE');
/*!40000 ALTER TABLE `class_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classschedule`
--

DROP TABLE IF EXISTS `classschedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classschedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `classtemplate_id` bigint DEFAULT NULL,
  `room_id` bigint DEFAULT NULL,
  `schedule_pattern_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcyvmpt3g8kw38n3a3r91i8gdl` (`classtemplate_id`),
  KEY `FKma7fv8alkxxcy1w8wus721n6x` (`room_id`),
  KEY `FK8wcgsgex8y5ikmhoh9eopsm09` (`schedule_pattern_id`),
  CONSTRAINT `FK8wcgsgex8y5ikmhoh9eopsm09` FOREIGN KEY (`schedule_pattern_id`) REFERENCES `schedule_pattern` (`id`),
  CONSTRAINT `FKcyvmpt3g8kw38n3a3r91i8gdl` FOREIGN KEY (`classtemplate_id`) REFERENCES `class_template` (`id`),
  CONSTRAINT `FKma7fv8alkxxcy1w8wus721n6x` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classschedule`
--

LOCK TABLES `classschedule` WRITE;
/*!40000 ALTER TABLE `classschedule` DISABLE KEYS */;
INSERT INTO `classschedule` VALUES (1,'2025-10-20 15:30:00.000000','2025-10-20 15:00:00.000000','OPEN',1,1,1),(2,'2025-10-27 15:30:00.000000','2025-10-27 14:00:00.000000','OPEN',1,1,1),(3,'2025-11-03 15:30:00.000000','2025-11-03 14:00:00.000000','OPEN',1,1,1),(4,'2025-11-10 15:30:00.000000','2025-11-10 14:00:00.000000','OPEN',1,1,1),(5,'2025-11-17 15:30:00.000000','2025-11-17 14:00:00.000000','OPEN',1,1,1),(6,'2025-11-24 15:30:00.000000','2025-11-24 14:00:00.000000','OPEN',1,1,1),(7,'2025-12-01 15:30:00.000000','2025-12-01 14:00:00.000000','OPEN',1,1,1),(8,'2025-12-08 15:30:00.000000','2025-12-08 14:00:00.000000','OPEN',1,1,1),(9,'2025-12-15 15:30:00.000000','2025-12-15 14:00:00.000000','OPEN',1,1,1),(10,'2025-10-20 11:00:00.000000','2025-10-20 10:00:00.000000','OPEN',2,2,8),(11,'2025-10-22 11:00:00.000000','2025-10-22 10:00:00.000000','OPEN',2,2,8),(12,'2025-10-27 11:00:00.000000','2025-10-27 10:00:00.000000','OPEN',2,2,8),(13,'2025-10-29 11:00:00.000000','2025-10-29 10:00:00.000000','OPEN',2,2,8),(14,'2025-10-22 16:00:00.000000','2025-10-22 15:00:00.000000','OPEN',4,3,7),(15,'2025-10-29 16:00:00.000000','2025-10-29 15:00:00.000000','OPEN',4,3,7),(16,'2025-10-21 10:30:00.000000','2025-10-21 09:00:00.000000','OPEN',4,4,6),(17,'2025-10-28 10:30:00.000000','2025-10-28 09:00:00.000000','OPEN',4,4,6),(18,'2025-10-29 14:00:00.000000','2025-10-29 13:00:00.000000','OPEN',2,1,9),(19,'2025-10-20 15:30:00.000000','2025-10-20 14:00:00.000000','OPEN',3,3,1),(20,'2025-10-27 15:30:00.000000','2025-10-27 14:00:00.000000','OPEN',3,3,1),(21,'2025-11-03 15:30:00.000000','2025-11-03 14:00:00.000000','OPEN',3,3,1),(22,'2025-11-10 15:30:00.000000','2025-11-10 14:00:00.000000','OPEN',3,3,1),(23,'2025-11-17 15:30:00.000000','2025-11-17 14:00:00.000000','OPEN',3,3,1),(24,'2025-11-24 15:30:00.000000','2025-11-24 14:00:00.000000','OPEN',3,3,1),(25,'2025-12-01 15:30:00.000000','2025-12-01 14:00:00.000000','OPEN',3,3,1),(26,'2025-12-08 15:30:00.000000','2025-12-08 14:00:00.000000','OPEN',3,3,1),(27,'2025-12-15 15:30:00.000000','2025-12-15 14:00:00.000000','OPEN',3,3,1),(28,'2025-10-20 15:30:00.000000','2025-10-20 14:00:00.000000','OPEN',6,4,1),(29,'2025-10-27 15:30:00.000000','2025-10-27 14:00:00.000000','OPEN',6,4,1),(30,'2025-11-03 15:30:00.000000','2025-11-03 14:00:00.000000','OPEN',6,4,1),(31,'2025-11-10 15:30:00.000000','2025-11-10 14:00:00.000000','OPEN',6,4,1),(32,'2025-11-17 15:30:00.000000','2025-11-17 14:00:00.000000','OPEN',6,4,1),(33,'2025-11-24 15:30:00.000000','2025-11-24 14:00:00.000000','OPEN',6,4,1),(34,'2025-12-01 15:30:00.000000','2025-12-01 14:00:00.000000','OPEN',6,4,1),(35,'2025-12-08 15:30:00.000000','2025-12-08 14:00:00.000000','OPEN',6,4,1),(36,'2025-12-15 15:30:00.000000','2025-12-15 14:00:00.000000','OPEN',6,4,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configs`
--

LOCK TABLES `configs` WRITE;
/*!40000 ALTER TABLE `configs` DISABLE KEYS */;
INSERT INTO `configs` VALUES (1,_binary '','banner','/image/banners/20251030_221433_istockphoto-2027278927-612x612.jpg.jpg'),(2,_binary '','banner','/image/banners/20251030_221510_photo-1534438327276-14e5300c3a48.jpg.jpg');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (1,'GYM50','PERCENTAGE',50,'2026-01-01 06:59:59.000000','GOLD','2025-10-10 07:00:00.000000','ACTIVE',NULL),(2,'GYM30','PERCENTAGE',30,'2026-01-01 06:59:59.000000','SILVER','2025-10-10 07:00:00.000000','ACTIVE',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_membership`
--

LOCK TABLES `customer_membership` WRITE;
/*!40000 ALTER TABLE `customer_membership` DISABLE KEYS */;
INSERT INTO `customer_membership` VALUES (1,'2026-01-15 00:00:00.000000','2025-01-15 00:00:00.000000','Active',1,1),(2,'2025-06-10 00:00:00.000000','2025-05-10 00:00:00.000000','Expired',7,5),(3,'2025-12-01 00:00:00.000000','2025-09-01 00:00:00.000000','Active',8,4),(4,'2025-05-12 00:00:00.000000','2025-04-12 00:00:00.000000','Expired',9,6),(5,'2026-01-01 00:00:00.000000','2025-07-01 00:00:00.000000','Active',10,2),(6,'2025-01-15 00:00:00.000000','2024-01-15 00:00:00.000000','Expired',1,3);
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,'',0,1),(2,'Phòng tập rất sạch sẽ, trang thiết bị hiện đại. Huấn luyện viên thân thiện và luôn hỗ trợ tận tình. Mình cảm thấy rất thoải mái mỗi khi đến tập\r\n',5,1),(3,'Các lớp yoga và zumba rất tuyệt, giáo viên dạy chuyên nghiệp, nhiệt tình. Lịch học linh hoạt nên mình dễ sắp xếp thời gian\r\n',4,1),(4,'Tham gia được 3 tháng, mình thấy sức khỏe cải thiện rõ rệt. Ứng dụng đặt lịch của phòng tập cũng rất tiện lợi\r\n',5,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback_attachments`
--

LOCK TABLES `feedback_attachments` WRITE;
/*!40000 ALTER TABLE `feedback_attachments` DISABLE KEYS */;
INSERT INTO `feedback_attachments` VALUES (1,'uploads/feedback/1759763080053_Screenshot 2025-08-10 213638.png',34224,'image/png',1),(2,'uploads/feedback/1759809999068_1759808347100_1.jpg',11894,'image/jpeg',2),(3,'uploads/feedback/1759810011480_1759808347112_2.jpg',286745,'image/jpeg',3),(4,'uploads/feedback/1759810043897_1759808372874_3.jpg',161687,'image/jpeg',4),(5,'uploads/feedback/1759810043904_1759808372886_4.jpg',12660,'image/jpeg',4);
/*!40000 ALTER TABLE `feedback_attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow_class`
--

DROP TABLE IF EXISTS `follow_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follow_date` datetime(6) DEFAULT NULL,
  `class_template_id` bigint NOT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe9ofpcj71j0vcur4oqvjoumwj` (`class_template_id`),
  KEY `FKhcqewkgs5trn41aww9sgq9g8m` (`member_id`),
  CONSTRAINT `FKe9ofpcj71j0vcur4oqvjoumwj` FOREIGN KEY (`class_template_id`) REFERENCES `class_template` (`id`),
  CONSTRAINT `FKhcqewkgs5trn41aww9sgq9g8m` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow_class`
--

LOCK TABLES `follow_class` WRITE;
/*!40000 ALTER TABLE `follow_class` DISABLE KEYS */;
INSERT INTO `follow_class` VALUES (3,'2025-10-30 15:33:20.540000',5,1),(4,'2025-10-30 15:37:46.141000',4,1),(12,'2025-11-17 21:37:51.822000',1,1);
/*!40000 ALTER TABLE `follow_class` ENABLE KEYS */;
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
  `manager_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKao72nt6hraqdgibljo1ynjwf9` (`manager_id`),
  KEY `FKkf17st5r6dawedwsjltt1gqbq` (`provider_id`),
  CONSTRAINT `FKao72nt6hraqdgibljo1ynjwf9` FOREIGN KEY (`manager_id`) REFERENCES `manager` (`id`),
  CONSTRAINT `FKkf17st5r6dawedwsjltt1gqbq` FOREIGN KEY (`provider_id`) REFERENCES `providers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_bills`
--

LOCK TABLES `import_bills` WRITE;
/*!40000 ALTER TABLE `import_bills` DISABLE KEYS */;
INSERT INTO `import_bills` VALUES (1,'2025-09-30 17:30:00.000000',6,1,123),(2,'2025-09-29 23:45:00.000000',6,2,456),(3,'2025-10-07 12:32:05.184000',6,2,285000),(4,'2025-10-07 12:39:02.504000',6,2,570000),(5,'2025-10-07 14:51:24.982000',6,2,570000);
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
  `quantity` int NOT NULL,
  `import_bill_id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1f4c5c0irjyxv907t96hull7i` (`import_bill_id`),
  KEY `FKjsww4dcpgg2kiqk0cu71p6byi` (`product_id`),
  CONSTRAINT `FK1f4c5c0irjyxv907t96hull7i` FOREIGN KEY (`import_bill_id`) REFERENCES `import_bills` (`id`),
  CONSTRAINT `FKjsww4dcpgg2kiqk0cu71p6byi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imported_products`
--

LOCK TABLES `imported_products` WRITE;
/*!40000 ALTER TABLE `imported_products` DISABLE KEYS */;
INSERT INTO `imported_products` VALUES (1,2,1,2),(2,2,1,4),(3,50,2,1),(4,10,2,5),(5,1,3,3),(6,1,3,8),(7,2,4,8),(8,2,4,3),(9,2,5,3),(10,2,5,8);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issued_coupon`
--

LOCK TABLES `issued_coupon` WRITE;
/*!40000 ALTER TABLE `issued_coupon` DISABLE KEYS */;
INSERT INTO `issued_coupon` VALUES (1,2,'AVAILABLE',1,1),(2,3,'AVAILABLE',2,7);
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
INSERT INTO `manager` VALUES (6),(12);
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
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
INSERT INTO `members` VALUES ('CARD001','FACE001','2024-01-15 00:00:00.000000','Premium','Active',1),('CARD002','FACE002','2024-02-10 00:00:00.000000','Standard','Inactive',7),('CARD003','FACE003','2024-03-05 00:00:00.000000','Gold','Active',8),('CARD004','FACE004','2024-04-12 00:00:00.000000','Basic','Suspended',9),('CARD005','FACE005','2024-05-25 00:00:00.000000','VIP','Active',10);
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_plan`
--

LOCK TABLES `membership_plan` WRITE;
/*!40000 ALTER TABLE `membership_plan` DISABLE KEYS */;
INSERT INTO `membership_plan` VALUES (1,'Full access, Free PT sessions, Sauna, Pool, 2 free guest passes/month','12 Months','Platinum 12 Months',15000000,'Active',1),(2,'Full access, Free PT sessions, Sauna, Pool, 1 free guest pass/month','6 Months','Platinum 6 Months',8000000,'Active',1),(3,'Full access, Sauna, Pool','12 Months','Gold 12 Months',9500000,'Active',2),(4,'Full access, Sauna, Pool','3 Months','Gold 3 Months',2800000,'Active',2),(5,'Full access to gym floor and group classes','1 Month','Silver 1 Month',850000,'Active',3),(6,'Full access to gym floor and group classes','1 Month','Basic 1 Month',500000,'Active',4),(7,'Full access, Sauna','1 Month','Old Gold 1 Month',1000000,'Inactive',2),(8,'abc','6 Months','Dirt',100000,'Active',1),(9,'hello','2 Months','Plastic',50000,'Active',4);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_tier`
--

LOCK TABLES `membership_tier` WRITE;
/*!40000 ALTER TABLE `membership_tier` DISABLE KEYS */;
INSERT INTO `membership_tier` VALUES (1,'Platinum',1,'Active'),(2,'Gold',2,'Active'),(3,'Silver',3,'Active'),(4,'Basic',4,'Active'),(5,'Student Pass',5,'Inactive');
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
  `type` varchar(50) NOT NULL,
  `import_price` double DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Nike','T-Shirt Gym',250000,69,'clothes',200000,0),(2,'Optimum Nutrition','Protein Powder 2kg',850000,9,'powder',800000,0),(3,'Adidas','Shorts Training',180000,50,'clothes',100000,0),(4,'Dymatize','Mass Gainer 5kg',1200000,16,'powder',NULL,0),(5,'Under Armour','Gym Gloves',120000,27,'clothes',NULL,0),(6,'Lavie','Lavie water bottle 1L',10000,0,'drinks',NULL,0),(7,'Red Bull','Red Bull Energy Drink 250ml',15000,0,'drinks',NULL,0),(8,'Adidas','Test',200000,5,'powder',NULL,1),(9,'abccc','abc',10000000,0,'drinks',NULL,1),(10,NULL,'1 Day PT',200000,NULL,'PT',NULL,0),(11,NULL,'30 session PT',1000000,NULL,'PT',NULL,0),(12,NULL,'2 Month PT',2000000,NULL,'PT',NULL,0);
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `providers`
--

LOCK TABLES `providers` WRITE;
/*!40000 ALTER TABLE `providers` DISABLE KEYS */;
INSERT INTO `providers` VALUES (1,'Nike','Hoàng Gia Sport','0905123456'),(2,'Adidas','Thể Thao Việt','0987123456'),(3,'Optimum Nutrition','Gym Pro Supplier','0932123456'),(4,'Under Armour','ABC Fitness','0912345678'),(5,'Dymatize','Thực phẩm thể hình HN','0976123456');
/*!40000 ALTER TABLE `providers` ENABLE KEYS */;
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
  `package_id` bigint NOT NULL,
  `staff_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhoalkw31q4jfsq53cgrlb69r` (`package_id`),
  KEY `FK2k0kkug1f96q91dppi5itf4f8` (`staff_id`),
  CONSTRAINT `FK2k0kkug1f96q91dppi5itf4f8` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`),
  CONSTRAINT `FKhoalkw31q4jfsq53cgrlb69r` FOREIGN KEY (`package_id`) REFERENCES `pt_package_issued` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_appointment`
--

LOCK TABLES `pt_appointment` WRITE;
/*!40000 ALTER TABLE `pt_appointment` DISABLE KEYS */;
INSERT INTO `pt_appointment` VALUES (1,'2025-10-27 10:00:00.000000',_binary '','2025-10-27 09:00:00.000000','Completed',1,2),(2,'2025-10-29 10:00:00.000000',_binary '\0','2025-10-29 09:00:00.000000','Scheduled',1,2),(3,'2025-11-03 16:00:00.000000',_binary '\0','2025-11-03 15:00:00.000000','Scheduled',2,2),(4,'2025-10-20 16:00:00.000000',_binary '','2025-10-20 15:00:00.000000','Canceled',2,2),(5,'2025-09-15 12:00:00.000000',_binary '','2025-09-15 11:00:00.000000','Completed',3,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_package`
--

LOCK TABLES `pt_package` WRITE;
/*!40000 ALTER TABLE `pt_package` DISABLE KEYS */;
INSERT INTO `pt_package` VALUES (1,'Gói Tập Thử 3 Buổi',3,'Active'),(2,'Gói Cơ Bản 12 Buổi',12,'Active'),(3,'Gói Nâng Cao 30 Buổi',30,'Active'),(4,'Gói Chuyên Sâu 50 Buổi',50,'Active'),(5,'Gói Khuyến Mãi Hè',15,'Inactive');
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
  `package_id` bigint NOT NULL,
  `staff_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeb267euikrhk2ml99ykuyaooy` (`member_id`),
  KEY `FK8amxa4fq0y9i3j5wikp3mn5v7` (`package_id`),
  KEY `FKsemsnhxlpxv8cfhvbgu74ux6h` (`staff_id`),
  CONSTRAINT `FK8amxa4fq0y9i3j5wikp3mn5v7` FOREIGN KEY (`package_id`) REFERENCES `pt_package` (`id`),
  CONSTRAINT `FKeb267euikrhk2ml99ykuyaooy` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FKsemsnhxlpxv8cfhvbgu74ux6h` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pt_package_issued`
--

LOCK TABLES `pt_package_issued` WRITE;
/*!40000 ALTER TABLE `pt_package_issued` DISABLE KEYS */;
INSERT INTO `pt_package_issued` VALUES (1,25,1,3,2),(2,12,8,2,2),(3,0,10,1,2),(4,0,1,2,4);
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
INSERT INTO `receptionist` VALUES (3),(13);
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
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (39,'2025-11-24 14:24:40.839158','0cdf9fe5-4be4-446e-b669-c46936ebf5a3',3),(41,'2025-11-24 14:34:05.731429','0f89fb3c-0ba8-4098-99ab-fec714d167d8',1),(42,'2025-11-24 14:50:13.063334','d4265919-b214-4c62-9f14-5c089065a7ff',2),(43,'2025-11-24 15:08:03.596714','ea52fe39-9351-40f6-b9cc-de9bc726bf81',6);
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'Tầng 1 - Khu A','Phòng Yoga 101','Phòng có gương và thảm tập đầy đủ'),(2,'Tầng 2 - Khu B','Phòng Gym 201','Phòng có máy chạy bộ và tạ'),(3,'Tầng 3 - Khu C','Phòng Aerobic 301','Phòng có loa và gương lớn'),(4,'Tầng 4 - Khu D','Phòng Zumba 401','Phòng rộng, có sàn gỗ');
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
  `class_end_date` date DEFAULT NULL,
  `class_start_date` date DEFAULT NULL,
  `days_of_week` varchar(255) DEFAULT NULL,
  `time_end` time(6) DEFAULT NULL,
  `time_start` time(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule_pattern`
--

LOCK TABLES `schedule_pattern` WRITE;
/*!40000 ALTER TABLE `schedule_pattern` DISABLE KEYS */;
INSERT INTO `schedule_pattern` VALUES (1,'2025-12-20','2025-10-20','MONDAY','15:30:00.000000','14:00:00.000000'),(3,'2025-01-16','2025-10-16','MONDAY,WEDNESDAY,FRIDAY','10:30:00.000000','09:00:00.000000'),(4,'2025-11-24','2025-11-03','MONDAY','08:30:00.000000','07:00:00.000000'),(5,'2025-10-26','2025-10-19','MONDAY','19:00:00.000000','06:00:00.000000'),(6,'2025-10-29','2025-10-19','TUESDAY','10:30:00.000000','09:00:00.000000'),(7,'2025-10-30','2025-10-21','WEDNESDAY','16:00:00.000000','15:00:00.000000'),(8,'2025-10-30','2025-10-19','MONDAY, WEDNESDAY','11:00:00.000000','10:00:00.000000'),(9,'2025-10-30','2025-10-28','WEDNESDAY','14:00:00.000000','13:00:00.000000');
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
  `bill_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKex0sc04uw5ojgek6k4gd28l2h` (`bill_id`),
  KEY `FKa1mbo3owjt7o4tq95p5k1j3a1` (`product_id`),
  CONSTRAINT `FKa1mbo3owjt7o4tq95p5k1j3a1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKex0sc04uw5ojgek6k4gd28l2h` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sold_product`
--

LOCK TABLES `sold_product` WRITE;
/*!40000 ALTER TABLE `sold_product` DISABLE KEYS */;
INSERT INTO `sold_product` VALUES (1,2,1,2),(2,1,1,4),(3,3,2,1),(4,2,2,5),(5,4,3,2),(6,1,3,3),(7,4,5,1),(8,1,5,3),(9,2,6,1),(10,1,6,3),(11,4,7,1),(12,1,7,3),(13,1,8,2),(14,4,8,1),(15,1,9,2),(16,5,10,1),(17,1,10,2),(18,1,11,1),(19,4,12,1),(20,1,12,3),(21,1,12,5),(22,3,13,1),(23,1,14,1),(24,1,15,10),(25,1,15,11);
/*!40000 ALTER TABLE `sold_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staffassigned`
--

DROP TABLE IF EXISTS `staffassigned`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staffassigned` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `training_session` int NOT NULL,
  `bill_id` bigint NOT NULL,
  `staff_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2hg3ogg0uv723v1o204l9w67d` (`bill_id`),
  KEY `FKtg7xckp94wqj3cwtkt3460s7l` (`staff_id`),
  CONSTRAINT `FK2hg3ogg0uv723v1o204l9w67d` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`),
  CONSTRAINT `FKtg7xckp94wqj3cwtkt3460s7l` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staffassigned`
--

LOCK TABLES `staffassigned` WRITE;
/*!40000 ALTER TABLE `staffassigned` DISABLE KEYS */;
INSERT INTO `staffassigned` VALUES (1,12,1,4),(2,8,2,4),(3,15,4,4),(4,2,6,2),(5,2,6,4),(6,1,7,2),(7,1,7,4),(8,1,8,2),(9,4,8,4),(10,15,9,4);
/*!40000 ALTER TABLE `staffassigned` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staffs`
--

DROP TABLE IF EXISTS `staffs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staffs` (
  `hire_price` varchar(255) DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `specialize` varchar(255) DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKdrcbb0t4jyjslw24sf1tkfk2p` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staffs`
--

LOCK TABLES `staffs` WRITE;
/*!40000 ALTER TABLE `staffs` DISABLE KEYS */;
INSERT INTO `staffs` VALUES ('400000','Personal Trainer','Weight Training',2),('450000','Personal Trainer','Cardio & Body Toning',4),('350000','Yoga Instructor','Yoga & Flexibility',5),('32','In dolore excepteur ','Libero ad itaque et ',11);
/*!40000 ALTER TABLE `staffs` ENABLE KEYS */;
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
  `staff_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtq5alsq2hqepysabml44aoj1p` (`member_id`),
  KEY `FKgrfh9u2f9wty6tj4vmbnil8uv` (`staff_id`),
  CONSTRAINT `FKgrfh9u2f9wty6tj4vmbnil8uv` FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`id`),
  CONSTRAINT `FKtq5alsq2hqepysabml44aoj1p` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_profile`
--

LOCK TABLES `student_profile` WRITE;
/*!40000 ALTER TABLE `student_profile` DISABLE KEYS */;
INSERT INTO `student_profile` VALUES (1,175,'Phase 1: Strength building. Focus on compound lifts (Squat, Bench, Deadlift) 3 times/week. Caloric surplus diet aiming for 3000 kcal/day.',72.5,1,2),(2,160.5,'Focus on fat loss. HIIT cardio 3 times/week. Full-body resistance training 2 times/week. Caloric deficit diet aiming for 1800 kcal/day.',65,8,2),(3,180,'Beginner foundation plan. Full-body workout with machines to learn form. Focus on consistency and building a habit.',70,10,2),(4,174,'Old plan from 2024: General fitness and improving stamina. Circuit training and moderate cardio.',68,1,4);
/*!40000 ALTER TABLE `student_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_types`
--

DROP TABLE IF EXISTS `ticket_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `duration_days` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` double NOT NULL,
  `service_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_types`
--

LOCK TABLES `ticket_types` WRITE;
/*!40000 ALTER TABLE `ticket_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `card_id` varchar(50) DEFAULT NULL,
  `purchase_date` datetime(6) NOT NULL,
  `status` varchar(20) NOT NULL,
  `member_id` bigint NOT NULL,
  `ticket_type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsu652786vh446isbo27g0ohwb` (`member_id`),
  KEY `FKotik7mbbb14hu8n9og7o92k5h` (`ticket_type_id`),
  CONSTRAINT `FKotik7mbbb14hu8n9og7o92k5h` FOREIGN KEY (`ticket_type_id`) REFERENCES `ticket_types` (`id`),
  CONSTRAINT `FKsu652786vh446isbo27g0ohwb` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
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
INSERT INTO `training_session` VALUES (1,'2025-10-27 10:00:00.000000','Hội viên có tiến bộ tốt ở bài tập Squat, form đã cải thiện. Buổi sau cần tập trung vào sự ổn định của bài đẩy ngực (Bench press).','2025-10-27 09:02:00.000000',1),(2,'2025-09-15 11:58:00.000000','Buổi tập đầu tiên sau kỳ nghỉ. Tập trung lấy lại form và kiểm tra sức bền. Hội viên thực hiện tốt.','2025-09-15 11:00:00.000000',5);
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
  `password` varchar(255) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'1998-05-20 07:00:00.000000','member01@gmail.com','Nguyen Van A','MALE','$2a$10$x4Av9E85Vz4.UsoUJOajDeKm.79EhL2QoXrUSZVB.kybS0HdbxawK','0905123456','MEMBER',0),(2,'1995-11-15 07:00:00.000000','pt01@gmail.com','Tran Thi B','FEMALE','$2a$10$KSXa.XmoKJ6LqO4G.qE19etguvSGo4VKqcCz0uz8NtnNmgeG7ZolW','0912345678','STAFF',0),(3,'2000-03-10 07:00:00.000000','reception01@gmail.com','Le Van C','MALE','$2a$10$c3CPbyQeMUDZR7ZqloXRh.AxaPkIn4uMDLSg15eglZntMVJhabKEO','0987123456','RECEPTIONIST',0),(4,'1992-07-15 07:00:00.000000','pt02@gmail.com','Pham Van PT','MALE','$2a$10$gzJMleT1hS8nP/bJMDTIV.kcYg3Vydg4iJp.RYqb0DWXdUsRkOO3y','0908000111','STAFF',0),(5,'1990-11-22 07:00:00.000000','teacher01@gmail.com','Nguyen Thi Teacher','FEMALE','$2a$10$I.rH9glcI/kGIRzRx6AuW.y6xr8PwFC/fN3Gqu5N9y0Htgc.jG1bG','0908000222','STAFF',0),(6,'1980-05-15 07:00:00.000000','manager@example.com','Tran Van B','Male','$2a$10$jInlJR/HUohcblQyUKcJG.bonDqBhTc7L8ceG9p48Lvz8IHRiFXmq','0123456789','MANAGER',0),(7,'2000-03-14 07:00:00.000000','member02@gmail.com','Tran Thi B','FEMALE','$2a$10$nAvnPgMpwwGceeFZtY1VnuBXzdIYNU1qibcv78zqkGtTcPisVspDS','0906234567','MEMBER',0),(8,'1997-10-02 07:00:00.000000','member03@gmail.com','Le Van C','MALE','$2a$10$Zum3vuBm9i5GVzhzlJfkWOWTrApw.ydZ5YuO3/dFeA2CXJheA3yea','0917123456','MEMBER',0),(9,'2001-07-22 07:00:00.000000','member04@gmail.com','Pham Thi D','FEMALE','$2a$10$dieeFuq2OXS5L7aIA4k70eRdVsPGdv4BcGSnNyNPLUiQKow8DR2t2','0938123456','MEMBER',0),(10,'1999-12-10 07:00:00.000000','member05@gmail.com','Do Van E','MALE','$2a$10$Zglk528kIPB2RW2Wglume.mCFhserkPu2CVjFjcSv1yH4MAg.8kvm','0979123456','MEMBER',1),(11,'2019-12-23 07:00:00.000000','pt4@gmail.com','Laborum Sequi odit ','Male','$2a$10$/V3R9Tqo7xmEyupVdvzGzubBVRR2H9Wm4t5KgpgqGbLZawGlCgPmG','+1 (739) 787-3042','STAFF',0),(12,'1971-02-14 07:00:00.000000','manager@gmail.com','Est velit est accusa','Female','$2a$10$yDUtxvdsBr/VNzkFauz0kuA4LP73HtjGGd2Q4C0i90vR.K/I3IxAe','+1 (814) 182-1928','MANAGER',0),(13,'2023-09-16 07:00:00.000000','reception2@gmail.com','rec2edit','Other','$2a$10$ER/p.gQjrn50X2mDP2qfP.Ns9mbkUwqj7.sdy0CC9nIS8u7cN5yYC','+1 (138) 514-9189','RECEPTIONIST',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-18 10:29:27
