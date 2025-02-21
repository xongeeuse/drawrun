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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `id` varchar(45) NOT NULL,
  `user_email` varchar(45) NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `user_nickname` varchar(45) NOT NULL,
  `user_password` varchar(225) NOT NULL,
  `profile_img_url` varchar(225) DEFAULT NULL,
  `social_type` int DEFAULT NULL,
  `social_id` int DEFAULT NULL,
  `badge_id` int DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  `is_deleted` int DEFAULT NULL,
  `region` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'test1','test1@test.com','ji','ji','$2a$10$8BQ5XcE4lO1Zp3G2PQZb9Ow9..64E5ogc.gZ/dDkwzemEb1kjhZGW',NULL,NULL,NULL,NULL,'2025-02-18 05:21:58','2025-02-18 05:21:58',NULL,NULL),(2,'test2','test2@test.com','ny','ny','$2a$10$kZGq4rr/AT//T4MminlsPOrciAtau0rJaD70H2xpvwtvVA5v.fFS.',NULL,NULL,NULL,NULL,'2025-02-18 05:22:09','2025-02-18 05:22:09',NULL,NULL),(3,'test3','test3@test.com','jy','jy','$2a$10$UBTuvnRkEI22E26N5bAtEe/sDhnjPrNRWKt7U/Dd4ocMN3XowD7je',NULL,NULL,NULL,NULL,'2025-02-18 05:22:18','2025-02-18 05:22:18',NULL,NULL),(4,'test4','test4@test.com','dg','dg','$2a$10$vW3SOHkansypfJG2t69MOubUQHNbc6m4cT6OGhe4nhR87LqXemjlS',NULL,NULL,NULL,NULL,'2025-02-18 05:22:26','2025-02-18 05:22:26',NULL,NULL),(5,'test5','test5@test.com','sh','sh','$2a$10$kMSOH2qDIPkhRM0gFzctfuHzwH9W12sn6rUqsF4Eot1xpL0h2deeK',NULL,NULL,NULL,NULL,'2025-02-18 05:22:47','2025-02-18 05:22:47',NULL,NULL),(6,'testuser','testuser@test.com','테스트유저','TESTUSER','$2a$10$GpJOd7.DN5EwT.4oPWYvH.DfqifqfIxn2DiGL/lNeUtgYw3DM/6NC',NULL,NULL,NULL,NULL,'2025-02-18 11:54:05','2025-02-18 11:54:05',NULL,NULL),(7,'xongeeuse','testuser1@user.com','지영','지영','$2a$10$/oA3U9nUE.GBNLqhYIJ1HONT61Uny0JzTHfQW1i7cLkXTVcLyQ.7m',NULL,NULL,NULL,NULL,'2025-02-19 08:02:39','2025-02-19 08:02:39',NULL,NULL),(8,'jiyoung','jiyoung@naver.com','쏭쏭','쏭쏭','$2a$10$i13zie/USJB0DCm8I/DvjOOrdqfWvMVElLhj5jfThxnw0gObukiLG',NULL,NULL,NULL,NULL,'2025-02-20 20:50:12','2025-02-20 20:50:12',NULL,NULL),(9,'skdud5126 ','skdud5126@ssafy.com','김나영','봇대전','$2a$10$ioiuOpktHj1ikP6rpe25euyGCtogrruu5hubk8WGEKgy9ASQRZTgC',NULL,NULL,NULL,NULL,'2025-02-20 20:50:33','2025-02-20 20:50:33',NULL,NULL),(10,'ssafy','skdud5126@naver.com','김나녕','봇대전 ','$2a$10$Qbd9Ms17nqsTEkDT6MWulOhe6ZtAaqq4m0HoJeaWkxUUc4T.t5DCu',NULL,NULL,NULL,NULL,'2025-02-20 20:52:32','2025-02-20 20:52:32',NULL,NULL),(11,'iwannabegosu','iwannabegosu@gmail.com','동규뽀이','동규뽀이','$2a$10$7BeArV7QEs5ZQ3/HQcscBO6hOkA8jUsDQzJi2N/oLiLlxB/5nuh8K',NULL,NULL,NULL,NULL,'2025-02-20 21:31:01','2025-02-20 21:31:01',NULL,NULL),(12,'test','test@google.com','다시마','다시마','$2a$10$jlXqGDqP4g/zEaJw1IrhceCF2h/Uiu7FAus039dMuIh4ydn8iAj5K',NULL,NULL,NULL,NULL,'2025-02-20 23:50:29','2025-02-20 23:50:29',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  9:08:31
