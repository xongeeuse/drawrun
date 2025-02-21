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
-- Table structure for table `user_stat`
--

DROP TABLE IF EXISTS `user_stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_stat` (
  `stat_id` int NOT NULL AUTO_INCREMENT,
  `distance_km` double DEFAULT NULL,
  `time_s` bigint DEFAULT NULL,
  `pace_s` bigint DEFAULT NULL,
  `state` int DEFAULT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `heartbeat` int DEFAULT NULL,
  `run_img_url` varchar(2000) DEFAULT NULL,
  `cadence` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`stat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_stat`
--

LOCK TABLES `user_stat` WRITE;
/*!40000 ALTER TABLE `user_stat` DISABLE KEYS */;
INSERT INTO `user_stat` VALUES (1,93.219,65,10,1,'2025-02-18 10:32:33',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739874738522_tracking_snapshot_1739874738421.jpg',1,2),(2,46.329,29,10,1,'2025-02-18 11:02:52',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739876540819_tracking_snapshot_1739876541102.jpg',1,1),(3,253.613,178,10,1,'2025-02-18 11:06:22',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739876771357_tracking_snapshot_1739876771632.jpg',1,1),(4,93.219,65,10,1,'2025-02-18 11:17:20',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739877432764_tracking_snapshot_1739877433171.jpg',1,1),(5,0.07668289750022277,13,5,1,'2025-02-18 11:20:44',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739877638625_tracking_snapshot_1739877639032.jpg',1,1),(6,0.12908335102876295,33,5,1,'2025-02-18 11:23:38',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739877812791_tracking_snapshot_1739877813067.jpg',1,1),(7,1.2978955101767247,100,77,1,'2025-02-18 11:29:20',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739878152849_tracking_snapshot_1739878153063.jpg',1,1),(8,0.2631837995702767,51,5,1,'2025-02-18 12:09:54',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739880587482_tracking_snapshot_1739880587805.jpg',1,6),(9,0.2950867851297111,92,5,1,'2025-02-18 13:27:34',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739885245992_tracking_snapshot_1739885246331.jpg',1,6),(10,0.5868024887287077,127,216,1,'2025-02-18 13:31:46',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739885500042_tracking_snapshot_1739885500394.jpg',1,6),(11,0.7723782276364954,47,60,1,'2025-02-18 17:33:56',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739900030667_tracking_snapshot_1739900030539.jpg',1,6),(12,0.8506247116225618,63,74,1,'2025-02-18 18:24:25',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739903054567_tracking_snapshot_1739903054351.jpg',1,6),(13,0.3407199352469934,42,5,1,'2025-02-18 18:32:31',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739903546734_tracking_snapshot_1739903546515.jpg',1,6),(14,0.11011950003756767,36,5,1,'2025-02-19 00:43:54',94,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739925828239_tracking_snapshot_1739925828616.jpg',1,1),(15,0.12987853538375396,36,5,1,'2025-02-19 00:55:46',91,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739926531246_tracking_snapshot_1739926531522.jpg',1,1),(16,0.13194870544642476,37,5,1,'2025-02-19 01:20:30',93,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739928023987_tracking_snapshot_1739928024351.jpg',1,1),(17,0.5102032148210175,166,325,1,'2025-02-19 01:30:26',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739928559045_tracking_snapshot_1739928559537.jpg',1,2),(18,0.18935690820601822,20,5,1,'2025-02-19 01:50:05',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739929800343_tracking_snapshot_1739929800044.jpg',1,6),(19,0.12728721500296059,34,5,1,'2025-02-19 01:50:57',104,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739929837272_tracking_snapshot_1739929837641.jpg',1,1),(20,0.298598442163073,28,5,1,'2025-02-19 01:53:53',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739930028291_tracking_snapshot_1739930028098.jpg',1,6),(21,0.2225977046602936,58,5,1,'2025-02-19 02:11:57',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739931113350_tracking_snapshot_1739931112608.jpg',1,6),(22,0.17042704050526775,22,5,1,'2025-02-19 02:16:18',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739931355118_tracking_snapshot_1739931354702.jpg',1,6),(23,0.5337406577811583,32,59,1,'2025-02-19 02:17:13',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739931422994_tracking_snapshot_1739931422731.jpg',1,6),(24,0.14009506299143298,9,5,1,'2025-02-19 02:47:52',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739933268338_tracking_snapshot_1739933268078.jpg',1,1),(25,0.11567283357044772,19,5,1,'2025-02-19 02:54:50',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739933668857_tracking_snapshot_1739933668201.jpg',1,1),(26,0.23916159684613725,168,5,1,'2025-02-19 04:36:25',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739939728112_tracking_snapshot_1739939728435.jpg',1,2),(27,0.34515153372689067,424,5,1,'2025-02-19 04:38:59',137,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739939919089_tracking_snapshot_1739939918855.jpg',1,1),(28,0.1765907747090592,55,5,1,'2025-02-19 04:39:13',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739939920477_tracking_snapshot_1739939920860.jpg',1,2),(29,0.13405023978657826,39,5,1,'2025-02-19 05:18:22',104,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739942294696_tracking_snapshot_1739942295017.jpg',1,1),(30,0.15397794596658482,44,5,1,'2025-02-19 10:19:21',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739957064563_tracking_snapshot_1739957064341.jpg',1,6),(31,1.1406963714461327,128,112,1,'2025-02-19 13:39:02',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739972332661_tracking_snapshot_1739972332581.jpg',1,6),(32,0.10159219403007853,31,10,1,'2025-02-19 18:51:14',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739991067183_tracking_snapshot_1739991067233.jpg',1,6),(33,2.4957547968105103,157,62,1,'2025-02-20 00:21:41',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740010774455_tracking_snapshot_1740010774042.jpg',1,6),(34,0.26848875135451516,69,5,1,'2025-02-20 00:53:52',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740012808109_tracking_snapshot_1740012809303.jpg',1,2),(35,2.854033722213978,747,261,1,'2025-02-20 00:53:58',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740012769946_tracking_snapshot_1740012769584.jpg',1,6),(36,0.5301464676474315,169,318,1,'2025-02-20 01:06:38',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740013587356_tracking_snapshot_1740013588516.jpg',1,2),(37,0.5411145343365458,150,277,1,'2025-02-20 01:15:16',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740014110499_tracking_snapshot_1740014111618.jpg',1,2),(38,1.5138599027834427,124,81,1,'2025-02-20 01:28:55',-1,NULL,1,6),(39,0.48713234377569414,131,5,1,'2025-02-20 01:36:02',1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740015350766_tracking_snapshot_1740015351693.jpg',1,2),(40,0.21618205771510005,72,5,1,'2025-02-20 01:40:31',1,NULL,1,2),(41,0.10810269721438048,41,10,1,'2025-02-20 03:18:53',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740021523921_tracking_snapshot_1740021525068.jpg',1,1),(42,0.1117494653261568,43,10,1,'2025-02-20 03:41:29',105,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740022875651_tracking_snapshot_1740022877001.jpg',1,1),(43,0.39464700708582057,243,10,1,'2025-02-20 05:25:56',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740028473989_tracking_snapshot_1740028473931.jpg',1,6),(44,0.2696968335680633,237,10,1,'2025-02-20 08:47:00',114,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740041211765_tracking_snapshot_1740041213513.jpg',1,1),(45,0.16522393856023693,138,10,1,'2025-02-20 08:49:50',116,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740041383549_tracking_snapshot_1740041385315.jpg',1,1),(46,0.1834262924515801,148,10,1,'2025-02-20 09:30:01',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740043749314_tracking_snapshot_1740043750700.jpg',1,1),(47,0.12496843175268556,37,10,1,'2025-02-20 13:16:17',104,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740057366689_tracking_snapshot_1740057368205.jpg',1,1),(48,0.17251712491760673,66,10,1,'2025-02-20 20:54:47',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740084844989_tracking_snapshot_1740084845047.jpg',1,8),(49,0.7584710779799437,54,71,1,'2025-02-20 20:59:12',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740085145821_tracking_snapshot_1740085145896.jpg',1,8),(50,0.238728898899136,20,10,1,'2025-02-20 21:00:49',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740085236700_tracking_snapshot_1740085236850.jpg',1,1),(51,0.4005825356632571,29,10,1,'2025-02-20 21:12:07',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740085908725_tracking_snapshot_1740085910756.jpg',1,10),(52,1.140143010964504,161,141,1,'2025-02-20 21:38:16',-1,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740087490724_tracking_snapshot_1740087490809.jpg',1,11),(53,26.31,15600,363,1,'2025-02-20 00:04:56',131,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/image+(2).png',1,12),(54,7.22,4102,371,1,'2025-02-19 00:07:17',128,'https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/image+(1).png',1,12),(55,5.2,3317,373,1,'2025-02-18 00:08:21',128,'	\nhttps://drawrunbucket.s3.ap-northeast-2.amazonaws.com/image+(4).png',1,12);
/*!40000 ALTER TABLE `user_stat` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  9:08:34
