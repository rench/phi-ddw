/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50716
Source Host           : localhost:3306
Source Database       : phi-ddw

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2018-04-20 16:25:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address` varchar(42) NOT NULL,
  `balance` decimal(50,0) DEFAULT NULL,
  `transaction_count` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`address`),
  KEY `IDXfar3kkwpeo6sogc6l2ukxh773` (`balance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for block
-- ----------------------------
DROP TABLE IF EXISTS `block`;
CREATE TABLE `block` (
  `number` decimal(19,0) NOT NULL,
  `difficulty` decimal(19,0) DEFAULT NULL,
  `gas_limit` decimal(19,0) DEFAULT NULL,
  `gas_used` decimal(19,0) DEFAULT NULL,
  `hash` varchar(66) DEFAULT NULL,
  `miner` varchar(42) DEFAULT NULL,
  `nonce` varchar(255) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `transaction_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`number`),
  KEY `IDXpdmh5w3m17qx9h68uknfui44t` (`hash`),
  KEY `IDXk6olajj8stcldhw1f8xf0w7qh` (`miner`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `block_hash` varchar(66) DEFAULT NULL,
  `block_number` decimal(19,0) DEFAULT NULL,
  `from_address` varchar(42) DEFAULT NULL,
  `gas` decimal(19,0) DEFAULT NULL,
  `gas_price` decimal(19,0) DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `input` varchar(255) DEFAULT NULL,
  `nonce` varchar(255) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `to_address` varchar(42) DEFAULT NULL,
  `value` decimal(50,0) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX35taisk3btc61543f84kb7h40` (`from_address`,`timestamp`),
  KEY `IDXhd6nih7t8aqgkajgk5ikvh03e` (`to_address`,`timestamp`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
