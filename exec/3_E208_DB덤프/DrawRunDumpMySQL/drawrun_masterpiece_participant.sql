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
-- Table structure for table `masterpiece_participant`
--

DROP TABLE IF EXISTS `masterpiece_participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `masterpiece_participant` (
  `masterpiece_participant_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `state` int NOT NULL,
  `masterpiece_seg_id` int NOT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`masterpiece_participant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `masterpiece_participant`
--

LOCK TABLES `masterpiece_participant` WRITE;
/*!40000 ALTER TABLE `masterpiece_participant` DISABLE KEYS */;
INSERT INTO `masterpiece_participant` VALUES (1,8,1,442,'2025-02-20 20:52:56'),(2,8,1,9,'2025-02-20 20:58:09'),(3,1,1,7,'2025-02-20 21:00:14'),(4,2,0,8,'2025-02-20 21:01:14'),(5,10,1,210,'2025-02-20 21:05:23'),(6,10,1,6,'2025-02-20 21:11:16'),(7,10,1,341,'2025-02-20 21:17:25'),(8,11,1,191,'2025-02-20 21:35:27'),(9,1,0,443,'2025-02-21 00:00:45');
/*!40000 ALTER TABLE `masterpiece_participant` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  9:08:29
