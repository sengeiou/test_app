/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : bevol

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-08-08 11:54:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_verification_code
-- ----------------------------
DROP TABLE IF EXISTS `hq_verification_code`;
CREATE TABLE `hq_verification_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(255) DEFAULT NULL,
  `vcode` varchar(255) DEFAULT NULL,
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `update_stamp` int(11) NOT NULL DEFAULT '0',
  `create_stamp` int(11) NOT NULL DEFAULT '0',
  `hidden` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1隐藏',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1;
