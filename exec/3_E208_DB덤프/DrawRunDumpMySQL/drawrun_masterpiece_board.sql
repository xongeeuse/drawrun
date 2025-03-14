CREATE DATABASE  IF NOT EXISTS `drawrun` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `drawrun`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 13.124.222.21    Database: drawrun
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `masterpiece_board`
--

DROP TABLE IF EXISTS `masterpiece_board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `masterpiece_board` (
  `masterpiece_board_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `restrict_count` int NOT NULL,
  `state` int NOT NULL,
  `user_path_id` int NOT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  `expire_date` datetime NOT NULL,
  PRIMARY KEY (`masterpiece_board_id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `masterpiece_board`
--

LOCK TABLES `masterpiece_board` WRITE;
/*!40000 ALTER TABLE `masterpiece_board` DISABLE KEYS */;
INSERT INTO `masterpiece_board` VALUES (4,2,3,0,4,'2025-02-18 05:29:11','2025-02-18 05:29:11','2025-02-18 05:29:12'),(11,1,3,0,11,'2025-02-18 07:31:39','2025-02-18 07:31:39','2025-02-28 07:31:39'),(17,1,13,0,27,'2025-02-18 11:26:51','2025-02-18 11:26:51','2025-02-21 11:26:51'),(18,1,5,0,32,'2025-02-18 12:04:04','2025-02-18 12:04:04','2025-02-28 12:04:04'),(19,1,5,0,33,'2025-02-18 12:06:59','2025-02-18 12:06:59','2025-02-28 12:06:59'),(20,6,17,0,34,'2025-02-18 12:07:12','2025-02-18 12:07:12','2025-02-22 12:07:13'),(22,6,35,0,39,'2025-02-18 12:56:34','2025-02-18 12:56:34','2025-02-19 12:56:35'),(23,6,10,0,41,'2025-02-18 13:28:50','2025-02-18 13:28:50','2025-02-22 13:28:51'),(24,2,4,0,42,'2025-02-18 14:25:16','2025-02-18 14:25:16','2025-02-28 14:25:17'),(25,6,5,0,43,'2025-02-18 16:31:21','2025-02-18 16:31:21','2025-02-22 16:31:21'),(26,6,3,0,44,'2025-02-18 17:30:56','2025-02-18 17:30:56','2025-02-19 17:30:57'),(27,6,7,0,45,'2025-02-18 17:38:17','2025-02-18 17:38:17','2025-02-26 17:38:17'),(39,6,5,0,76,'2025-02-19 10:56:41','2025-02-19 10:56:41','2025-02-19 10:56:42'),(41,6,4,0,80,'2025-02-19 18:11:37','2025-02-19 18:11:37','2025-02-25 18:11:37'),(42,6,4,0,81,'2025-02-19 18:13:40','2025-02-19 18:13:40','2025-02-27 18:13:40'),(43,6,5,0,82,'2025-02-19 19:24:42','2025-02-19 19:24:42','2025-02-27 19:24:43'),(45,6,5,0,84,'2025-02-20 00:16:44','2025-02-20 00:16:44','2025-02-27 00:16:45'),(54,1,20,0,96,'2025-02-20 02:18:45','2025-02-20 02:18:45','2025-02-27 02:18:46'),(55,1,10,0,97,'2025-02-20 02:30:33','2025-02-20 02:30:33','2025-02-27 02:30:33'),(58,6,10,0,100,'2025-02-20 02:41:24','2025-02-20 02:41:24','2025-02-27 02:41:25'),(59,6,15,0,101,'2025-02-20 02:43:05','2025-02-20 02:43:05','2025-02-27 02:43:06'),(60,6,4,0,102,'2025-02-20 03:40:09','2025-02-20 03:40:09','2025-02-27 03:40:10'),(64,6,6,0,109,'2025-02-20 07:19:44','2025-02-20 07:19:44','2025-02-27 07:19:45'),(67,6,5,0,112,'2025-02-20 08:12:00','2025-02-20 08:12:00','2025-02-20 08:12:00'),(69,6,4,0,116,'2025-02-20 10:15:31','2025-02-20 10:15:31','2025-02-27 10:15:31'),(70,1,5,0,117,'2025-02-20 12:58:51','2025-02-20 12:58:51','2025-02-22 12:58:52'),(71,1,7,0,118,'2025-02-20 13:00:15','2025-02-20 13:00:15','2025-02-22 13:00:15'),(72,1,11,0,119,'2025-02-20 13:03:30','2025-02-20 13:03:30','2025-02-22 13:03:30'),(73,1,7,0,120,'2025-02-20 13:04:51','2025-02-20 13:04:51','2025-02-22 13:04:52'),(74,1,12,0,122,'2025-02-20 13:09:58','2025-02-20 13:09:58','2025-02-22 13:09:59'),(75,6,5,0,123,'2025-02-20 13:57:38','2025-02-20 13:57:38','2025-02-27 13:57:38'),(76,6,10,0,124,'2025-02-20 14:07:29','2025-02-20 14:07:29','2025-02-27 14:07:30'),(77,6,4,0,126,'2025-02-20 20:35:31','2025-02-20 20:35:31','2025-02-21 20:35:31');
/*!40000 ALTER TABLE `masterpiece_board` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  9:08:30
