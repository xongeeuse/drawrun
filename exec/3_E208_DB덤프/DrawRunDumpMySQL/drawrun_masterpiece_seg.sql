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
-- Table structure for table `masterpiece_seg`
--

DROP TABLE IF EXISTS `masterpiece_seg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `masterpiece_seg` (
  `masterpiece_seg_id` int NOT NULL AUTO_INCREMENT,
  `masterpiece_board_id` int NOT NULL,
  `mongo_id` varchar(225) NOT NULL,
  `path_num` int NOT NULL,
  `address` varchar(225) NOT NULL,
  `address2` varchar(226) DEFAULT NULL,
  PRIMARY KEY (`masterpiece_seg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=444 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `masterpiece_seg`
--

LOCK TABLES `masterpiece_seg` WRITE;
/*!40000 ALTER TABLE `masterpiece_seg` DISABLE KEYS */;
INSERT INTO `masterpiece_seg` VALUES (5,3,'67b41a2e76fabd6eb816d9b5',1,'부산광역시 강서구 송정동',NULL),(6,3,'67b41a2e76fabd6eb816d9b6',2,'부산광역시 강서구 송정동',NULL),(7,4,'67b41aa776fabd6eb816d9b8',1,'부산광역시 강서구 송정동',NULL),(8,4,'67b41aa776fabd6eb816d9b9',2,'부산광역시 강서구 송정동',NULL),(9,4,'67b41aa776fabd6eb816d9ba',3,'부산광역시 강서구 송정동',NULL),(36,11,'67b4375bc27c885e95381a5a',1,'부산광역시 강서구 송정동',NULL),(37,11,'67b4375bc27c885e95381a5b',2,'부산광역시 강서구 화전동',NULL),(38,11,'67b4375bc27c885e95381a5c',3,'부산광역시 강서구 송정동',NULL),(39,12,'67b4457b6befb056d9f1d961',1,'부산광역시 강서구 송정동',NULL),(40,12,'67b4457b6befb056d9f1d962',2,'부산광역시 강서구 녹산동',NULL),(41,12,'67b4457b6befb056d9f1d963',3,'부산광역시 강서구 송정동',NULL),(42,12,'67b4457b6befb056d9f1d964',4,'부산광역시 강서구 신호동',NULL),(43,12,'67b4457b6befb056d9f1d965',5,'부산광역시 강서구 화전동',NULL),(51,15,'67b468085f52eb2e5a001b38',1,'부산광역시 강서구 명지동',NULL),(52,15,'67b468095f52eb2e5a001b39',2,'부산광역시 강서구 명지동',NULL),(53,15,'67b468095f52eb2e5a001b3a',3,'부산광역시 강서구 명지동',NULL),(54,15,'67b468095f52eb2e5a001b3b',4,'부산광역시 강서구 명지동',NULL),(55,15,'67b468095f52eb2e5a001b3c',5,'부산광역시 강서구 명지동',NULL),(62,17,'67b46e7b5f52eb2e5a001b45',1,'부산광역시 강서구 송정동',NULL),(63,17,'67b46e7b5f52eb2e5a001b46',2,'부산광역시 강서구 송정동',NULL),(64,17,'67b46e7b5f52eb2e5a001b47',3,'부산광역시 강서구 화전동',NULL),(65,17,'67b46e7b5f52eb2e5a001b48',4,'부산광역시 강서구 화전동',NULL),(66,17,'67b46e7b5f52eb2e5a001b49',5,'부산광역시 강서구 송정동',NULL),(67,17,'67b46e7b5f52eb2e5a001b4a',6,'부산광역시 강서구 송정동',NULL),(68,17,'67b46e7b5f52eb2e5a001b4b',7,'부산광역시 강서구 송정동',NULL),(69,17,'67b46e7b5f52eb2e5a001b4c',8,'부산광역시 강서구 송정동',NULL),(70,17,'67b46e7b5f52eb2e5a001b4d',9,'부산광역시 강서구 송정동',NULL),(71,17,'67b46e7b5f52eb2e5a001b4e',10,'부산광역시 강서구 송정동',NULL),(72,17,'67b46e7b5f52eb2e5a001b4f',11,'부산광역시 강서구 송정동',NULL),(73,17,'67b46e7b5f52eb2e5a001b50',12,'부산광역시 강서구 송정동',NULL),(74,17,'67b46e7b5f52eb2e5a001b51',13,'부산광역시 강서구 화전동',NULL),(75,18,'67b477348e05177d991e6dba',1,'부산광역시 부산진구 전포동',NULL),(76,18,'67b477348e05177d991e6dbb',2,'부산광역시 부산진구 범천동',NULL),(77,18,'67b477348e05177d991e6dbc',3,'부산광역시 남구 문현동',NULL),(78,18,'67b477348e05177d991e6dbd',4,'부산광역시 부산진구 전포동',NULL),(79,18,'67b477348e05177d991e6dbe',5,'부산광역시 부산진구 연지동',NULL),(80,19,'67b477e38e05177d991e6dc0',1,'부산광역시 부산진구 전포동',NULL),(81,19,'67b477e38e05177d991e6dc1',2,'부산광역시 부산진구 당감동',NULL),(82,19,'67b477e38e05177d991e6dc2',3,'부산광역시 부산진구 범전동',NULL),(83,19,'67b477e38e05177d991e6dc3',4,'부산광역시 연제구 연산동',NULL),(84,19,'67b477e38e05177d991e6dc4',5,'부산광역시 부산진구 전포동',NULL),(85,20,'67b477f08e05177d991e6dc6',1,'부산광역시 부산진구 부전동',NULL),(86,20,'67b477f08e05177d991e6dc7',2,'부산광역시 부산진구 부전동',NULL),(87,20,'67b477f08e05177d991e6dc8',3,'부산광역시 부산진구 부전동',NULL),(88,20,'67b477f08e05177d991e6dc9',4,'부산광역시 부산진구 부전동',NULL),(89,20,'67b477f08e05177d991e6dca',5,'부산광역시 부산진구 전포동',NULL),(90,20,'67b477f18e05177d991e6dcb',6,'부산광역시 부산진구 전포동',NULL),(91,20,'67b477f18e05177d991e6dcc',7,'부산광역시 부산진구 양정동',NULL),(92,20,'67b477f18e05177d991e6dcd',8,'부산광역시 부산진구 양정동',NULL),(93,20,'67b477f18e05177d991e6dce',9,'부산광역시 부산진구 전포동',NULL),(94,20,'67b477f18e05177d991e6dcf',10,'부산광역시 부산진구 부전동',NULL),(95,20,'67b477f18e05177d991e6dd0',11,'부산광역시 부산진구 범전동',NULL),(96,20,'67b477f18e05177d991e6dd1',12,'부산광역시 부산진구 부전동',NULL),(97,20,'67b477f18e05177d991e6dd2',13,'부산광역시 부산진구 범전동',NULL),(98,20,'67b477f18e05177d991e6dd3',14,'부산광역시 부산진구 부전동',NULL),(99,20,'67b477f18e05177d991e6dd4',15,'부산광역시 부산진구 전포동',NULL),(100,20,'67b477f18e05177d991e6dd5',16,'부산광역시 부산진구 양정동',NULL),(101,20,'67b477f18e05177d991e6dd6',17,'부산광역시 부산진구 양정동',NULL),(102,21,'67b482ffa9894a26ed727aaa',1,'부산광역시 강서구 송정동',NULL),(103,21,'67b482ffa9894a26ed727aab',2,'부산광역시 강서구 송정동',NULL),(104,21,'67b482ffa9894a26ed727aac',3,'부산광역시 강서구 송정동',NULL),(105,21,'67b482ffa9894a26ed727aad',4,'부산광역시 강서구 송정동',NULL),(106,21,'67b482ffa9894a26ed727aae',5,'부산광역시 강서구 송정동',NULL),(107,21,'67b482ffa9894a26ed727aaf',6,'부산광역시 강서구 송정동',NULL),(108,21,'67b482ffa9894a26ed727ab0',7,'부산광역시 강서구 송정동',NULL),(109,21,'67b482ffa9894a26ed727ab1',8,'부산광역시 강서구 송정동',NULL),(110,21,'67b482ffa9894a26ed727ab2',9,'부산광역시 강서구 송정동',NULL),(111,21,'67b482ffa9894a26ed727ab3',10,'부산광역시 강서구 송정동',NULL),(112,21,'67b482ffa9894a26ed727ab4',11,'부산광역시 강서구 송정동',NULL),(113,21,'67b482ffa9894a26ed727ab5',12,'부산광역시 강서구 송정동',NULL),(114,21,'67b482ffa9894a26ed727ab6',13,'부산광역시 강서구 송정동',NULL),(115,21,'67b482ffa9894a26ed727ab7',14,'부산광역시 강서구 송정동',NULL),(116,21,'67b482ffa9894a26ed727ab8',15,'부산광역시 강서구 송정동',NULL),(117,21,'67b482ffa9894a26ed727ab9',16,'부산광역시 강서구 송정동',NULL),(118,21,'67b482ffa9894a26ed727aba',17,'부산광역시 강서구 송정동',NULL),(119,21,'67b482ffa9894a26ed727abb',18,'부산광역시 강서구 송정동',NULL),(120,21,'67b482ffa9894a26ed727abc',19,'부산광역시 강서구 송정동',NULL),(121,21,'67b48300a9894a26ed727abd',20,'부산광역시 강서구 송정동',NULL),(122,21,'67b48300a9894a26ed727abe',21,'부산광역시 강서구 송정동',NULL),(123,21,'67b48300a9894a26ed727abf',22,'부산광역시 강서구 송정동',NULL),(124,21,'67b48300a9894a26ed727ac0',23,'부산광역시 강서구 송정동',NULL),(125,21,'67b48300a9894a26ed727ac1',24,'부산광역시 강서구 송정동',NULL),(126,21,'67b48300a9894a26ed727ac2',25,'부산광역시 강서구 송정동',NULL),(127,21,'67b48300a9894a26ed727ac3',26,'부산광역시 강서구 송정동',NULL),(128,21,'67b48300a9894a26ed727ac4',27,'부산광역시 강서구 송정동',NULL),(129,21,'67b48300a9894a26ed727ac5',28,'부산광역시 강서구 송정동',NULL),(130,21,'67b48300a9894a26ed727ac6',29,'부산광역시 강서구 송정동',NULL),(131,21,'67b48300a9894a26ed727ac7',30,'부산광역시 강서구 송정동',NULL),(132,21,'67b48300a9894a26ed727ac8',31,'부산광역시 강서구 송정동',NULL),(133,21,'67b48300a9894a26ed727ac9',32,'부산광역시 강서구 송정동',NULL),(134,21,'67b48300a9894a26ed727aca',33,'부산광역시 강서구 송정동',NULL),(135,21,'67b48300a9894a26ed727acb',34,'부산광역시 강서구 신호동',NULL),(136,21,'67b48300a9894a26ed727acc',35,'부산광역시 강서구 신호동',NULL),(137,22,'67b48382a9894a26ed727ace',1,'부산광역시 강서구 송정동',NULL),(138,22,'67b48382a9894a26ed727acf',2,'부산광역시 강서구 송정동',NULL),(139,22,'67b48382a9894a26ed727ad0',3,'부산광역시 강서구 송정동',NULL),(140,22,'67b48382a9894a26ed727ad1',4,'부산광역시 강서구 송정동',NULL),(141,22,'67b48382a9894a26ed727ad2',5,'부산광역시 강서구 송정동',NULL),(142,22,'67b48382a9894a26ed727ad3',6,'부산광역시 강서구 송정동',NULL),(143,22,'67b48382a9894a26ed727ad4',7,'부산광역시 강서구 송정동',NULL),(144,22,'67b48383a9894a26ed727ad5',8,'부산광역시 강서구 송정동',NULL),(145,22,'67b48383a9894a26ed727ad6',9,'부산광역시 강서구 송정동',NULL),(146,22,'67b48383a9894a26ed727ad7',10,'부산광역시 강서구 송정동',NULL),(147,22,'67b48383a9894a26ed727ad8',11,'부산광역시 강서구 송정동',NULL),(148,22,'67b48383a9894a26ed727ad9',12,'부산광역시 강서구 송정동',NULL),(149,22,'67b48383a9894a26ed727ada',13,'부산광역시 강서구 송정동',NULL),(150,22,'67b48383a9894a26ed727adb',14,'부산광역시 강서구 송정동',NULL),(151,22,'67b48383a9894a26ed727adc',15,'부산광역시 강서구 송정동',NULL),(152,22,'67b48383a9894a26ed727add',16,'부산광역시 강서구 송정동',NULL),(153,22,'67b48383a9894a26ed727ade',17,'부산광역시 강서구 송정동',NULL),(154,22,'67b48383a9894a26ed727adf',18,'부산광역시 강서구 송정동',NULL),(155,22,'67b48383a9894a26ed727ae0',19,'부산광역시 강서구 송정동',NULL),(156,22,'67b48383a9894a26ed727ae1',20,'부산광역시 강서구 송정동',NULL),(157,22,'67b48383a9894a26ed727ae2',21,'부산광역시 강서구 송정동',NULL),(158,22,'67b48383a9894a26ed727ae3',22,'부산광역시 강서구 송정동',NULL),(159,22,'67b48383a9894a26ed727ae4',23,'부산광역시 강서구 송정동',NULL),(160,22,'67b48383a9894a26ed727ae5',24,'부산광역시 강서구 송정동',NULL),(161,22,'67b48383a9894a26ed727ae6',25,'부산광역시 강서구 송정동',NULL),(162,22,'67b48383a9894a26ed727ae7',26,'부산광역시 강서구 송정동',NULL),(163,22,'67b48383a9894a26ed727ae8',27,'부산광역시 강서구 송정동',NULL),(164,22,'67b48383a9894a26ed727ae9',28,'부산광역시 강서구 송정동',NULL),(165,22,'67b48383a9894a26ed727aea',29,'부산광역시 강서구 송정동',NULL),(166,22,'67b48383a9894a26ed727aeb',30,'부산광역시 강서구 송정동',NULL),(167,22,'67b48383a9894a26ed727aec',31,'부산광역시 강서구 송정동',NULL),(168,22,'67b48383a9894a26ed727aed',32,'부산광역시 강서구 송정동',NULL),(169,22,'67b48383a9894a26ed727aee',33,'부산광역시 강서구 송정동',NULL),(170,22,'67b48383a9894a26ed727aef',34,'부산광역시 강서구 신호동',NULL),(171,22,'67b48383a9894a26ed727af0',35,'부산광역시 강서구 송정동',NULL),(172,23,'67b48b12abb30b1ee6f4c421',1,'부산광역시 부산진구 부전동',NULL),(173,23,'67b48b12abb30b1ee6f4c422',2,'부산광역시 부산진구 부전동',NULL),(174,23,'67b48b12abb30b1ee6f4c423',3,'부산광역시 부산진구 부암동',NULL),(175,23,'67b48b12abb30b1ee6f4c424',4,'부산광역시 부산진구 부전동',NULL),(176,23,'67b48b12abb30b1ee6f4c425',5,'부산광역시 부산진구 부전동',NULL),(177,23,'67b48b12abb30b1ee6f4c426',6,'부산광역시 부산진구 부전동',NULL),(178,23,'67b48b12abb30b1ee6f4c427',7,'부산광역시 부산진구 전포동',NULL),(179,23,'67b48b12abb30b1ee6f4c428',8,'부산광역시 부산진구 전포동',NULL),(180,23,'67b48b12abb30b1ee6f4c429',9,'부산광역시 부산진구 전포동',NULL),(181,23,'67b48b12abb30b1ee6f4c42a',10,'부산광역시 부산진구 부전동',NULL),(182,24,'67b4984cabb30b1ee6f4c42c',1,'경상남도 김해시 어방동',NULL),(183,24,'67b4984cabb30b1ee6f4c42d',2,'경상남도 김해시 삼방동',NULL),(184,24,'67b4984cabb30b1ee6f4c42e',3,'경상남도 김해시 삼방동',NULL),(185,24,'67b4984dabb30b1ee6f4c42f',4,'경상남도 김해시 삼방동',NULL),(186,25,'67b4b5d9deff1042532a2b17',1,'부산광역시 강서구 송정동',NULL),(187,25,'67b4b5d9deff1042532a2b18',2,'부산광역시 강서구 송정동',NULL),(188,25,'67b4b5d9deff1042532a2b19',3,'부산광역시 강서구 송정동',NULL),(189,25,'67b4b5d9deff1042532a2b1a',4,'부산광역시 강서구 송정동',NULL),(190,25,'67b4b5d9deff1042532a2b1b',5,'부산광역시 강서구 송정동',NULL),(191,26,'67b4c3d007523e42e7655060',1,'부산광역시 강서구 송정동',NULL),(192,26,'67b4c3d007523e42e7655061',2,'부산광역시 강서구 송정동',NULL),(193,26,'67b4c3d007523e42e7655062',3,'부산광역시 강서구 송정동',NULL),(194,27,'67b4c58907523e42e7655064',1,'부산광역시 부산진구 양정동',NULL),(195,27,'67b4c58907523e42e7655065',2,'부산광역시 연제구 거제동',NULL),(196,27,'67b4c58907523e42e7655066',3,'부산광역시 연제구 연산동',NULL),(197,27,'67b4c58907523e42e7655067',4,'부산광역시 연제구 연산동',NULL),(198,27,'67b4c58907523e42e7655068',5,'부산광역시 연제구 연산동',NULL),(199,27,'67b4c58907523e42e7655069',6,'부산광역시 연제구 연산동',NULL),(200,27,'67b4c58907523e42e765506a',7,'부산광역시 수영구 망미동',NULL),(203,33,'67b5314c68ddc803d1cf6677',1,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단335로 24-20'),(204,33,'67b5314c68ddc803d1cf6678',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1625'),(205,33,'67b5314c68ddc803d1cf6679',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(206,33,'67b5314c68ddc803d1cf667a',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1636'),(207,34,'67b538acb5869a2f943bde88',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1505'),(208,34,'67b538acb5869a2f943bde89',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1505'),(209,34,'67b538acb5869a2f943bde8a',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1501'),(210,35,'67b53ebe48e4ea0b61e8a8c8',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1548'),(211,35,'67b53ebe48e4ea0b61e8a8c9',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1537'),(212,36,'67b56897ac66bc3f567979f5',1,'부산광역시 부산진구 전포동','부산 부산진구 전포동 870-1'),(213,36,'67b56897ac66bc3f567979f6',2,'부산광역시 부산진구 전포동','부산 부산진구 전포동 300-28'),(214,36,'67b56897ac66bc3f567979f7',3,'부산광역시 부산진구 부전동','부산 부산진구 부전동 576-1'),(215,36,'67b56898ac66bc3f567979f8',4,'부산광역시 부산진구 부전동','부산광역시 부산진구 신천대로62번길 69'),(216,36,'67b56898ac66bc3f567979f9',5,'부산광역시 부산진구 범천동','부산 부산진구 범천동 946-18'),(217,36,'67b56898ac66bc3f567979fa',6,'부산광역시 부산진구 전포동','부산광역시 부산진구 동성로 26'),(222,38,'67b5b21bac66bc3f56797a02',1,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단289로 46'),(223,38,'67b5b21bac66bc3f56797a03',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1620'),(224,38,'67b5b21bac66bc3f56797a04',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1618'),(225,38,'67b5b21bac66bc3f56797a05',4,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산업중로 333'),(226,38,'67b5b21bac66bc3f56797a06',5,'부산광역시 강서구 송정동','부산 강서구 송정동 1416-1'),(227,39,'67b5b8eaac66bc3f56797a08',1,'부산광역시 부산진구 부전동','부산광역시 부산진구 동천로85번길 31-3'),(228,39,'67b5b8eaac66bc3f56797a09',2,'부산광역시 부산진구 부전동','부산 부산진구 부전동 573-1'),(229,39,'67b5b8eaac66bc3f56797a0a',3,'부산광역시 부산진구 범전동','부산 부산진구 범전동 327-9'),(230,39,'67b5b8eaac66bc3f56797a0b',4,'부산광역시 부산진구 전포동','부산 부산진구 전포동 864-1'),(231,39,'67b5b8eaac66bc3f56797a0c',5,'부산광역시 부산진구 전포동','부산 부산진구 전포동 877'),(234,41,'67b61ed9028c0351c2a8ca8b',1,'부산광역시 동래구 안락동','부산광역시 동래구 충렬대로433번길 26-6'),(235,41,'67b61ed9028c0351c2a8ca8c',2,'부산광역시 동래구 명장동','부산 동래구 명장동 66-67'),(236,41,'67b61ed9028c0351c2a8ca8d',3,'부산광역시 금정구 서동','부산 금정구 서동 207-3'),(237,41,'67b61ed9028c0351c2a8ca8e',4,'부산광역시 해운대구 반여동','부산 해운대구 반여동 1646'),(238,42,'67b61f54188c9129c4b3d4f5',1,'부산광역시 동래구 명륜동','부산 동래구 명륜동 433-4'),(239,42,'67b61f54188c9129c4b3d4f6',2,'부산광역시 동래구 칠산동','부산광역시 동래구 충렬대로285번길 22'),(240,42,'67b61f54188c9129c4b3d4f7',3,'부산광역시 동래구 명륜동','부산 동래구 명륜동 429-14'),(241,42,'67b61f54188c9129c4b3d4f8',4,'부산광역시 동래구 안락동','부산광역시 동래구 충렬대로428번길 49'),(242,43,'67b62ffa188c9129c4b3d4fa',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1616'),(243,43,'67b62ffa188c9129c4b3d4fb',2,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로73번길 51'),(244,43,'67b62ffa188c9129c4b3d4fc',3,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단232로 38-6'),(245,43,'67b62ffa188c9129c4b3d4fd',4,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단484로 58'),(246,43,'67b62ffa188c9129c4b3d4fe',5,'부산광역시 강서구 송정동','부산광역시 강서구 녹산화전로 24'),(250,45,'67b6746c464f8855ddf998fb',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(251,45,'67b6746c464f8855ddf998fc',2,'부산광역시 강서구 신호동','부산광역시 강서구 르노삼성대로 61'),(252,45,'67b6746c464f8855ddf998fd',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1620'),(253,45,'67b6746c464f8855ddf998fe',4,'부산광역시 강서구 화전동','부산 강서구 화전동 산 81-2'),(254,45,'67b6746d464f8855ddf998ff',5,'부산광역시 강서구 신호동','부산광역시 강서구 르노삼성대로 61'),(269,51,'67b684760a87cf333b2c8326',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1616'),(270,51,'67b684760a87cf333b2c8327',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1628'),(271,51,'67b684760a87cf333b2c8328',3,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단289로 6'),(272,51,'67b684760a87cf333b2c8329',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1609'),(277,54,'67b69105095ec71b466a9e4f',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1641'),(278,54,'67b69105095ec71b466a9e50',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1637'),(279,54,'67b69106095ec71b466a9e51',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1657'),(280,54,'67b69106095ec71b466a9e52',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1642-1'),(281,54,'67b69106095ec71b466a9e53',5,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(282,54,'67b69106095ec71b466a9e54',6,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(283,54,'67b69106095ec71b466a9e55',7,'부산광역시 강서구 송정동','부산 강서구 송정동 1625'),(284,54,'67b69106095ec71b466a9e56',8,'부산광역시 강서구 송정동','부산 강서구 송정동 1612-5'),(285,54,'67b69106095ec71b466a9e57',9,'부산광역시 강서구 송정동','부산 강서구 송정동 1627-3'),(286,54,'67b69106095ec71b466a9e58',10,'부산광역시 강서구 송정동','부산 강서구 송정동 1632'),(287,54,'67b69106095ec71b466a9e59',11,'부산광역시 강서구 송정동','부산 강서구 송정동 1633-3'),(288,54,'67b69106095ec71b466a9e5a',12,'부산광역시 강서구 송정동','부산 강서구 송정동 1636'),(289,54,'67b69106095ec71b466a9e5b',13,'부산광역시 강서구 송정동','부산 강서구 송정동 1636'),(290,54,'67b69106095ec71b466a9e5c',14,'부산광역시 강서구 송정동','부산 강서구 송정동 1649'),(291,54,'67b69106095ec71b466a9e5d',15,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단382로49번길 11'),(292,54,'67b69106095ec71b466a9e5e',16,'부산광역시 강서구 송정동','부산 강서구 송정동 1740'),(293,54,'67b69106095ec71b466a9e5f',17,'부산광역시 강서구 송정동','부산 강서구 송정동 1628'),(294,54,'67b69107095ec71b466a9e60',18,'부산광역시 강서구 송정동','부산 강서구 송정동 1732'),(295,54,'67b69107095ec71b466a9e61',19,'부산광역시 강서구 송정동','부산 강서구 송정동 1732'),(296,54,'67b69107095ec71b466a9e62',20,'부산광역시 강서구 송정동','부산 강서구 송정동 1615'),(297,55,'67b693c9a159b718bb63ba87',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1602'),(298,55,'67b693c9a159b718bb63ba88',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1577'),(299,55,'67b693c9a159b718bb63ba89',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1568'),(300,55,'67b693c9a159b718bb63ba8a',4,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로13번길 14'),(301,55,'67b693c9a159b718bb63ba8b',5,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단289로 6'),(302,55,'67b693c9a159b718bb63ba8c',6,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산업중로 296'),(303,55,'67b693c9a159b718bb63ba8d',7,'부산광역시 강서구 송정동','부산 강서구 송정동 1729-10'),(304,55,'67b693caa159b718bb63ba8e',8,'부산광역시 강서구 송정동','부산 강서구 송정동 1595'),(305,55,'67b693caa159b718bb63ba8f',9,'부산광역시 강서구 송정동','부산 강서구 송정동 1576'),(306,55,'67b693caa159b718bb63ba90',10,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단262로 16'),(313,58,'67b69654cdc1d242e2bfb9aa',1,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로74번길 41'),(314,58,'67b69654cdc1d242e2bfb9ab',2,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단289로 45'),(315,58,'67b69654cdc1d242e2bfb9ac',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1613'),(316,58,'67b69654cdc1d242e2bfb9ad',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1628'),(317,58,'67b69654cdc1d242e2bfb9ae',5,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단381로 29'),(318,58,'67b69654cdc1d242e2bfb9af',6,'부산광역시 강서구 송정동','부산 강서구 송정동 1600'),(319,58,'67b69655cdc1d242e2bfb9b0',7,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산업중로 333'),(320,58,'67b69655cdc1d242e2bfb9b1',8,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산업중로 333'),(321,58,'67b69655cdc1d242e2bfb9b2',9,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산업중로 333'),(322,58,'67b69655cdc1d242e2bfb9b3',10,'부산광역시 강서구 송정동','부산 강서구 송정동 1600'),(323,59,'67b696b9cdc1d242e2bfb9b5',1,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로74번길 42'),(324,59,'67b696b9cdc1d242e2bfb9b6',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1596'),(325,59,'67b696b9cdc1d242e2bfb9b7',3,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산단261로87번길 10'),(326,59,'67b696b9cdc1d242e2bfb9b8',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1554'),(327,59,'67b696b9cdc1d242e2bfb9b9',5,'부산광역시 강서구 송정동','부산 강서구 송정동 1576'),(328,59,'67b696b9cdc1d242e2bfb9ba',6,'부산광역시 강서구 송정동','부산 강서구 송정동 1595'),(329,59,'67b696bacdc1d242e2bfb9bb',7,'부산광역시 강서구 송정동','부산 강서구 송정동 1728'),(330,59,'67b696bacdc1d242e2bfb9bc',8,'부산광역시 강서구 송정동','부산 강서구 송정동 1733'),(331,59,'67b696bacdc1d242e2bfb9bd',9,'부산광역시 강서구 송정동','부산 강서구 송정동 1627-4'),(332,59,'67b696bacdc1d242e2bfb9be',10,'부산광역시 강서구 송정동','부산 강서구 송정동 1629-8'),(333,59,'67b696bacdc1d242e2bfb9bf',11,'부산광역시 강서구 송정동','부산 강서구 송정동 1635-2'),(334,59,'67b696bacdc1d242e2bfb9c0',12,'부산광역시 강서구 송정동','부산 강서구 송정동 1637'),(335,59,'67b696bacdc1d242e2bfb9c1',13,'부산광역시 강서구 송정동','부산 강서구 송정동 1600'),(336,59,'67b696bacdc1d242e2bfb9c2',14,'부산광역시 강서구 송정동','부산 강서구 송정동 1600'),(337,59,'67b696bacdc1d242e2bfb9c3',15,'부산광역시 강서구 송정동','부산 강서구 송정동 1616'),(338,60,'67b6a4194c13ab59142d4b35',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1628'),(339,60,'67b6a41a4c13ab59142d4b36',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1630'),(340,60,'67b6a41a4c13ab59142d4b37',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1634'),(341,60,'67b6a41a4c13ab59142d4b38',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1632'),(342,61,'67b6a45b4c13ab59142d4b3a',1,'부산광역시 강서구 송정동','부산광역시 강서구 녹산산업중로 333'),(343,61,'67b6a45b4c13ab59142d4b3b',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(344,61,'67b6a45b4c13ab59142d4b3c',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1626'),(345,61,'67b6a45c4c13ab59142d4b3d',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1631-11'),(346,62,'67b6a5854c13ab59142d4b3f',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1624'),(347,62,'67b6a5854c13ab59142d4b40',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1626'),(353,64,'67b6d7913549012a75d3ec8e',1,'부산광역시 강서구 신호동','부산광역시 강서구 르노삼성대로 61'),(354,64,'67b6d7913549012a75d3ec8f',2,'부산광역시 강서구 화전동','부산 강서구 화전동 652'),(355,64,'67b6d7913549012a75d3ec90',3,'부산광역시 강서구 화전동','부산 강서구 화전동 669'),(356,64,'67b6d7913549012a75d3ec91',4,'부산광역시 강서구 화전동','부산 강서구 화전동 651'),(357,64,'67b6d7913549012a75d3ec92',5,'부산광역시 강서구 신호동','부산광역시 강서구 르노삼성대로 61'),(358,64,'67b6d7913549012a75d3ec93',6,'부산광역시 강서구 신호동','부산광역시 강서구 르노삼성대로 61'),(372,67,'67b6e3d03549012a75d3eca4',1,'부산광역시 연제구 거제동','부산 연제구 거제동 1191-18'),(373,67,'67b6e3d03549012a75d3eca5',2,'부산광역시 동래구 사직동','부산광역시 동래구 석사로 43'),(374,67,'67b6e3d03549012a75d3eca6',3,'부산광역시 동래구 온천동','부산광역시 동래구 아시아드대로 253-6'),(375,67,'67b6e3d03549012a75d3eca7',4,'부산광역시 동래구 온천동','부산광역시 동래구 충렬대로100번길 37'),(376,67,'67b6e3d03549012a75d3eca8',5,'부산광역시 동래구 온천동','부산 동래구 온천동 516'),(379,69,'67b700c33549012a75d3ecaf',1,'부산광역시 부산진구 부전동','부산 부산진구 부전동 573-1'),(380,69,'67b700c33549012a75d3ecb0',2,'부산광역시 부산진구 전포동','부산 부산진구 전포동 869-1'),(381,69,'67b700c33549012a75d3ecb1',3,'부산광역시 부산진구 부전동','부산 부산진구 부전동 168-2'),(382,69,'67b700c33549012a75d3ecb2',4,'부산광역시 부산진구 부전동','부산 부산진구 부전동 573-1'),(383,70,'67b7270b1ee4415cb6250bff',1,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(384,70,'67b7270b1ee4415cb6250c00',2,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-14'),(385,70,'67b7270b1ee4415cb6250c01',3,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-6'),(386,70,'67b7270b1ee4415cb6250c02',4,'경상남도 김해시 삼방동','경남 김해시 삼방동 308'),(387,70,'67b7270c1ee4415cb6250c03',5,'경상남도 김해시 어방동','경남 김해시 어방동 475'),(388,71,'67b7275f1ee4415cb6250c05',1,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(389,71,'67b7275f1ee4415cb6250c06',2,'경상남도 김해시 어방동','경상남도 김해시 인제로 197'),(390,71,'67b7275f1ee4415cb6250c07',3,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-5'),(391,71,'67b7275f1ee4415cb6250c08',4,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-6'),(392,71,'67b7275f1ee4415cb6250c09',5,'경상남도 김해시 삼방동','경남 김해시 삼방동 308'),(393,71,'67b7275f1ee4415cb6250c0a',6,'경상남도 김해시 삼방동','경남 김해시 삼방동 272'),(394,71,'67b7275f1ee4415cb6250c0b',7,'경상남도 김해시 어방동','경남 김해시 어방동 483-11'),(395,72,'67b728221ee4415cb6250c0d',1,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(396,72,'67b728221ee4415cb6250c0e',2,'경상남도 김해시 어방동','경상남도 김해시 인제로 197'),(397,72,'67b728221ee4415cb6250c0f',3,'경상남도 김해시 어방동','경상남도 김해시 인제로 197'),(398,72,'67b728221ee4415cb6250c10',4,'경상남도 김해시 어방동','경남 김해시 어방동 500-5'),(399,72,'67b728221ee4415cb6250c11',5,'경상남도 김해시 어방동','경남 김해시 어방동 483-11'),(400,72,'67b728221ee4415cb6250c12',6,'경상남도 김해시 삼방동','경남 김해시 삼방동 273'),(401,72,'67b728221ee4415cb6250c13',7,'경상남도 김해시 삼방동','경남 김해시 삼방동 308'),(402,72,'67b728231ee4415cb6250c14',8,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-12'),(403,72,'67b728231ee4415cb6250c15',9,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-6'),(404,72,'67b728231ee4415cb6250c16',10,'경상남도 김해시 삼방동','경남 김해시 삼방동 308'),(405,72,'67b728231ee4415cb6250c17',11,'경상남도 김해시 삼방동','경남 김해시 삼방동 267'),(406,73,'67b728741ee4415cb6250c19',1,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(407,73,'67b728741ee4415cb6250c1a',2,'경상남도 김해시 삼방동','경남 김해시 삼방동 595'),(408,73,'67b728741ee4415cb6250c1b',3,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-7'),(409,73,'67b728741ee4415cb6250c1c',4,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-7'),(410,73,'67b728741ee4415cb6250c1d',5,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-6'),(411,73,'67b728741ee4415cb6250c1e',6,'경상남도 김해시 삼방동','경남 김해시 삼방동 308'),(412,73,'67b728741ee4415cb6250c1f',7,'경상남도 김해시 삼방동','경남 김해시 삼방동 248'),(413,74,'67b729a61ee4415cb6250c22',1,'경상남도 김해시 어방동','경상남도 김해시 인제로 167'),(414,74,'67b729a61ee4415cb6250c23',2,'경상남도 김해시 어방동','경남 김해시 어방동 산 21'),(415,74,'67b729a61ee4415cb6250c24',3,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-15'),(416,74,'67b729a61ee4415cb6250c25',4,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-7'),(417,74,'67b729a61ee4415cb6250c26',5,'경상남도 김해시 삼방동','경남 김해시 삼방동 694-6'),(418,74,'67b729a71ee4415cb6250c27',6,'경상남도 김해시 삼방동','경남 김해시 삼방동 313'),(419,74,'67b729a71ee4415cb6250c28',7,'경상남도 김해시 삼방동','경남 김해시 삼방동 313'),(420,74,'67b729a71ee4415cb6250c29',8,'경상남도 김해시 삼방동','경남 김해시 삼방동 308'),(421,74,'67b729a71ee4415cb6250c2a',9,'경상남도 김해시 삼방동','경남 김해시 삼방동 400'),(422,74,'67b729a71ee4415cb6250c2b',10,'경상남도 김해시 삼방동','경남 김해시 삼방동 272'),(423,74,'67b729a71ee4415cb6250c2c',11,'경상남도 김해시 어방동','경남 김해시 어방동 537'),(424,74,'67b729a71ee4415cb6250c2d',12,'경상남도 김해시 어방동','경남 김해시 어방동 483-11'),(425,75,'67b734d21c47754af04c0824',1,'부산광역시 연제구 거제동','부산 연제구 거제동 203-8'),(426,75,'67b734d21c47754af04c0825',2,'부산광역시 동래구 온천동','부산 동래구 온천동 1486'),(427,75,'67b734d21c47754af04c0826',3,'부산광역시 동래구 수안동','부산 동래구 수안동 28'),(428,75,'67b734d21c47754af04c0827',4,'부산광역시 동래구 수안동','부산 동래구 수안동 28'),(429,75,'67b734d21c47754af04c0828',5,'부산광역시 동래구 온천동','부산광역시 동래구 충렬대로 지하 147'),(430,76,'67b737211504d1495e88eb30',1,'부산광역시 연제구 거제동','부산 연제구 거제동 203-8'),(431,76,'67b737221504d1495e88eb31',2,'부산광역시 동래구 온천동','부산 동래구 온천동 1486'),(432,76,'67b737221504d1495e88eb32',3,'부산광역시 동래구 명륜동','부산 동래구 명륜동 429-14'),(433,76,'67b737221504d1495e88eb33',4,'부산광역시 동래구 명륜동','부산 동래구 명륜동 506-12'),(434,76,'67b737221504d1495e88eb34',5,'부산광역시 동래구 수안동','부산 동래구 수안동 28'),(435,76,'67b737221504d1495e88eb35',6,'부산광역시 동래구 명륜동','부산 동래구 명륜동 429-14'),(436,76,'67b737221504d1495e88eb36',7,'부산광역시 연제구 거제동','부산 연제구 거제동 1191-1'),(437,76,'67b737221504d1495e88eb37',8,'부산광역시 동래구 온천동','부산 동래구 온천동 1486'),(438,76,'67b737221504d1495e88eb38',9,'부산광역시 동래구 온천동','부산 동래구 온천동 1486'),(439,76,'67b737221504d1495e88eb39',10,'부산광역시 동래구 사직동','부산 동래구 사직동 161-1'),(440,77,'67b7921371e94f7685baf69c',1,'부산광역시 강서구 송정동','부산 강서구 송정동 1630'),(441,77,'67b7921371e94f7685baf69d',2,'부산광역시 강서구 송정동','부산 강서구 송정동 1634'),(442,77,'67b7921371e94f7685baf69e',3,'부산광역시 강서구 송정동','부산 강서구 송정동 1632'),(443,77,'67b7921371e94f7685baf69f',4,'부산광역시 강서구 송정동','부산 강서구 송정동 1628');
/*!40000 ALTER TABLE `masterpiece_seg` ENABLE KEYS */;
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
