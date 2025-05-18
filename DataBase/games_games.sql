-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: games
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `games`
--

DROP TABLE IF EXISTS `games`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `games` (
  `GameID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(255) NOT NULL,
  `Description` text,
  `Price` decimal(10,2) NOT NULL,
  `Category` varchar(100) DEFAULT NULL,
  `ReleaseDate` date DEFAULT NULL,
  `Rating` decimal(3,2) DEFAULT NULL,
  `SystemRequirements` text,
  `ImagePath` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`GameID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `games`
--

LOCK TABLES `games` WRITE;
/*!40000 ALTER TABLE `games` DISABLE KEYS */;
INSERT INTO `games` VALUES (1,'Elder Scrolls V: Skyrim','An open-world RPG set in the land of Skyrim.',29.99,'RPG','2011-11-11',9.20,'Intel Core i5, 8GB RAM, GTX 960','images/976e2c6b7ae325572b5cb20f0ca6e3ca.jpg'),(2,'Hades','A rogue-like dungeon crawler where you battle out of the Underworld.',19.99,'Action','2020-09-17',9.00,'Intel Core i3, 4GB RAM, Intel HD Graphics','images/b71029d7d28e76f40c30102c28c6fa9b.jpg'),(3,'Stardew Valley','A relaxing farming simulator with RPG elements.',14.99,'Simulation','2016-02-26',8.80,'2 GHz CPU, 2GB RAM, 256MB Graphics','images/bac05f8c7e006f178e60a06be11daca2.jpg'),(4,'Cyberpunk 2077','Futuristic open-world RPG with heavy storytelling.',39.99,'Action RPG','2020-12-10',7.50,'Intel Core i7, 16GB RAM, GTX 1060','images/a8b39de7f7dfb247a30276b5f790ead4.jpg'),(5,'Minecraft','A sandbox game where you build, explore, and survive.',26.95,'Sandbox','2011-11-18',9.50,'Intel Core i3, 4GB RAM, Intel HD Graphics','images/00a8e4a3edf9937c186dbecd009fd48d.jpg');
/*!40000 ALTER TABLE `games` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-15 17:55:16
