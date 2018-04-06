/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 50096
Source Host           : localhost:3306
Source Database       : bevol_spider

Target Server Type    : MYSQL
Target Server Version : 50096
File Encoding         : 65001

Date: 2016-11-01 14:35:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id` int(11) NOT NULL auto_increment,
  `keyword` varchar(50) collate utf8_bin NOT NULL,
  `work_status` int(11) default NULL,
  `page_count` int(11) default NULL,
  `total_count` int(11) default NULL,
  `spider_count` int(11) NOT NULL,
  `update_time` int(11) NOT NULL,
  `work_time` int(11) default NULL,
  `data_type` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
