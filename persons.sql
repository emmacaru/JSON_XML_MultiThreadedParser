-- MySQL dump 10.13  Distrib 5.1.61, for debian-linux-gnu (i486)
--
-- Host: localhost    Database: population
-- ------------------------------------------------------
-- Server version	5.1.61-0ubuntu0.10.04.1


DROP DATABASE IF EXISTS `persons`;
CREATE DATABASE `persons`;

use  `persons`;
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `person_data`
--

DROP TABLE IF EXISTS `person_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_data` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `processed_file_id` int(10) unsigned NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `location` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `new_fk_constraint` (`processed_file_id`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`processed_file_id`) REFERENCES `processed_files` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=936 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_data`
--

LOCK TABLES `person_data` WRITE;
/*!40000 ALTER TABLE `person_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `person_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processed_files`
--

DROP TABLE IF EXISTS `processed_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processed_files` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) NOT NULL,
  `filetype` tinyint(8) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=430 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processed_files`
--

LOCK TABLES `processed_files` WRITE;
/*!40000 ALTER TABLE `processed_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `processed_files` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-04-25 22:56:45
