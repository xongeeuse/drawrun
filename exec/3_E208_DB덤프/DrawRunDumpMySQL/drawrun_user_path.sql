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
-- Table structure for table `user_path`
--

DROP TABLE IF EXISTS `user_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_path` (
  `user_path_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `path_id` varchar(45) NOT NULL,
  `path_img_url` longtext NOT NULL,
  `name` varchar(45) NOT NULL,
  `create_date` datetime NOT NULL,
  `distance` double NOT NULL,
  `address` varchar(225) NOT NULL,
  `address2` varchar(225) DEFAULT NULL,
  PRIMARY KEY (`user_path_id`),
  UNIQUE KEY `path_id_UNIQUE` (`path_id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_path`
--

LOCK TABLES `user_path` WRITE;
/*!40000 ALTER TABLE `user_path` DISABLE KEYS */;
INSERT INTO `user_path` VALUES (4,2,'67b41aa776fabd6eb816d9b7','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739856531724_temp_image.jpg','No.6','2025-02-18 05:29:11',1.45,'부산광역시 강서구 송정동',NULL),(11,1,'67b4375bc27c885e95381a59','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739863861553_temp_image.jpg','신호공룡','2025-02-18 07:31:39',28.31,'부산광역시 강서구 송정동',NULL),(15,1,'67b43fab9a9289534902fd21','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739865988097_temp_image.jpg','닭벼슬강아지','2025-02-18 08:07:07',9.31,'부산광역시 강서구 송정동',NULL),(16,1,'67b440bd9a9289534902fd22','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739866274379_temp_image.jpg','찌그러진하투','2025-02-18 08:11:41',12.86,'부산광역시 부산진구 범천동',NULL),(27,1,'67b46e7b5f52eb2e5a001b44','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739877969493_temp_image.jpg','갱얼쥐','2025-02-18 11:26:51',12.74,'부산광역시 강서구 송정동',NULL),(32,1,'67b477348e05177d991e6db9','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739880226528_temp_image.jpg','함께달려요','2025-02-18 12:04:04',23.83,'부산광역시 부산진구 전포동',NULL),(33,1,'67b477e38e05177d991e6dbf','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739880404015_temp_image.jpg','함께달려요','2025-02-18 12:06:59',24.75,'부산광역시 부산진구 전포동',NULL),(34,6,'67b477f08e05177d991e6dc5','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739880238325_temp_image.jpg','티라노','2025-02-18 12:07:12',12.54,'부산광역시 부산진구 부전동',NULL),(38,6,'67b482fea9894a26ed727aa9','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739883241163_temp_image.jpg','ssafy','2025-02-18 12:54:23',21.5,'부산광역시 강서구 송정동',NULL),(39,6,'67b48382a9894a26ed727acd','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739883368912_temp_image.jpg','SSAFY','2025-02-18 12:56:34',28.04,'부산광역시 강서구 송정동',NULL),(41,6,'67b48b12abb30b1ee6f4c420','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739885312391_temp_image.jpg','하뚜','2025-02-18 13:28:50',7.03,'부산광역시 부산진구 부전동',NULL),(42,2,'67b4984cabb30b1ee6f4c42b','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739888576293_temp_image.jpg','AI 댕댕런','2025-02-18 14:25:16',7.62,'경상남도 김해시 어방동',NULL),(43,6,'67b4b5d8deff1042532a2b16','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739896259346_temp_image.jpg','뛰뛰런','2025-02-18 16:31:20',5.92,'부산광역시 강서구 송정동',NULL),(44,6,'67b4c3d007523e42e765505f','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739899841698_temp_image.jpg','돌돌런','2025-02-18 17:30:56',3.64,'부산광역시 강서구 송정동',NULL),(45,6,'67b4c58907523e42e7655063','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739900271758_temp_image.jpg','런런','2025-02-18 17:38:17',13.48,'부산광역시 부산진구 양정동',NULL),(71,1,'67b56870ac66bc3f567979f3','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739941980693_temp_image.jpg','강아지','2025-02-19 05:13:20',5.71,'부산광역시 부산진구 전포동','부산광역시 부산진구 동천로 58'),(72,1,'67b56897ac66bc3f567979f4','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739942022530_temp_image.jpg','함께뛰어요','2025-02-19 05:13:59',9.26,'부산광역시 부산진구 전포동','부산 부산진구 전포동 870-1'),(75,6,'67b5b21aac66bc3f56797a01','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739960825265_temp_image.jpg','함께달려요','2025-02-19 10:27:38',12.69,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단289로 46'),(76,6,'67b5b8e9ac66bc3f56797a07','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739962572373_temp_image.jpg','맨..','2025-02-19 10:56:41',7.46,'부산광역시 부산진구 부전동','부산광역시 부산진구 동천로85번길 31-3'),(80,6,'67b61ed8028c0351c2a8ca8a','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739988678803_temp_image.jpg','똥글런','2025-02-19 18:11:36',8.38,'부산광역시 동래구 안락동','부산광역시 동래구 충렬대로433번길 26-6'),(81,6,'67b61f54188c9129c4b3d4f4','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739988806105_temp_image.jpg','화살런','2025-02-19 18:13:40',9.79,'부산광역시 동래구 명륜동','부산 동래구 명륜동 433-4'),(82,6,'67b62ffa188c9129c4b3d4f9','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1739993071278_temp_image.jpg','돌돌','2025-02-19 19:24:42',8.62,'부산광역시 강서구 송정동','부산 강서구 송정동 1616'),(84,6,'67b6746c464f8855ddf998fa','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740010584171_temp_image.jpg','새싹런','2025-02-20 00:16:44',9.98,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(92,6,'67b684760a87cf333b2c8325','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740014687179_temp_image.jpg','거리테스트','2025-02-20 01:25:10',1.53,'부산광역시 강서구 송정동','부산 강서구 송정동 1616'),(95,1,'67b6899952b5f312b75ae126','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740016011722_temp_image.jpg','부츠런','2025-02-20 01:47:05',5.2,'부산광역시 강서구 송정동','부산 강서구 송정동 1732'),(96,1,'67b69105095ec71b466a9e4e','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740017903870_temp_image.jpg','강아지','2025-02-20 02:18:45',9.27,'부산광역시 강서구 송정동','부산 강서구 송정동 1641'),(97,1,'67b693c8a159b718bb63ba86','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740018614057_temp_image.jpg','스케이트런','2025-02-20 02:30:33',5.55,'부산광역시 강서구 송정동','부산 강서구 송정동 1602'),(100,6,'67b69654cdc1d242e2bfb9a9','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740019266686_temp_image.jpg','하뚜','2025-02-20 02:41:24',5.47,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로74번길 41'),(101,6,'67b696b9cdc1d242e2bfb9b4','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740019361865_temp_image.jpg','하트런','2025-02-20 02:43:05',6.19,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로74번길 42'),(102,6,'67b6a4194c13ab59142d4b34','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740022788812_temp_image.jpg','네모네모','2025-02-20 03:40:09',0.63,'부산광역시 강서구 송정동','부산 강서구 송정동 1628'),(109,6,'67b6d7903549012a75d3ec8d','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740035970567_temp_image.jpg','에이아이','2025-02-20 07:19:44',12.89,'부산광역시 강서구 신호동','부산광역시 강서구 르노삼성대로 61'),(112,6,'67b6e3d03549012a75d3eca3','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740039086299_temp_image.jpg','하트런','2025-02-20 08:12:00',9.37,'부산광역시 연제구 거제동','부산 연제구 거제동 1191-18'),(116,6,'67b700c33549012a75d3ecae','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740046513897_temp_image.jpg','ai하트','2025-02-20 10:15:31',11.23,'부산광역시 부산진구 부전동','부산 부산진구 부전동 573-1'),(117,1,'67b7270b1ee4415cb6250bfe','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740056256433_temp_image.jpg','AI김해댕댕이1','2025-02-20 12:58:51',6.89,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(118,1,'67b7275f1ee4415cb6250c04','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740056394770_temp_image.jpg','AI김해댕댕이2','2025-02-20 13:00:15',7.96,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(119,1,'67b728221ee4415cb6250c0c','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740056540170_temp_image.jpg','AI김해댕댕이3','2025-02-20 13:03:30',16.88,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(120,1,'67b728731ee4415cb6250c18','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740056672766_temp_image.jpg','AI김해댕댕이4','2025-02-20 13:04:51',9.46,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(121,1,'67b728a91ee4415cb6250c20','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740056723334_temp_image.jpg','AI김해댕댕이5','2025-02-20 13:05:45',7.39,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(122,1,'67b729a61ee4415cb6250c21','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740056974532_temp_image.jpg','AI김해댕댕이6','2025-02-20 13:09:58',7.38,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(123,6,'67b734d21c47754af04c0823','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740059841445_temp_image.jpg','ai하트','2025-02-20 13:57:38',8.51,'부산광역시 연제구 거제동','부산 연제구 거제동 203-8'),(124,6,'67b737211504d1495e88eb2f','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740060418764_temp_image.jpg','ai하트','2025-02-20 14:07:29',19.79,'부산광역시 연제구 거제동','부산 연제구 거제동 203-8'),(125,2,'67b78f5e71e94f7685baf69a','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740083004108_temp_image.jpg','하트런','2025-02-20 20:23:58',6.27,'부산광역시 부산진구 부전동','부산광역시 부산진구 중앙대로 지하 717'),(126,6,'67b7921371e94f7685baf69b','https://drawrunbucket.s3.ap-northeast-2.amazonaws.com/1740083712427_temp_image.jpg','네모네모런','2025-02-20 20:35:31',0.72,'부산광역시 강서구 송정동','부산 강서구 송정동 1630');
/*!40000 ALTER TABLE `user_path` ENABLE KEYS */;
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
