/*
Navicat MySQL Data Transfer

Source Server         : 正式
Source Server Version : 50629
Source Host           : rdsoxdt0k8m3y0561p1fo.mysql.rds.aliyuncs.com:3306
Source Database       : bevol_online

Target Server Type    : MYSQL
Target Server Version : 50629
File Encoding         : 65001

Date: 2016-12-27 16:32:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_statistics_find
-- ----------------------------
DROP TABLE IF EXISTS `hq_statistics_find`;
CREATE TABLE `hq_statistics_find` (
  `entity_id` int(11) NOT NULL DEFAULT '0',
  `like_num` int(12) DEFAULT '0',
  `notlike_num` int(12) DEFAULT '0',
  `hit_num` int(11) DEFAULT '0' COMMENT '访问次数',
  `comment_num` int(11) DEFAULT '0',
  `collection_num` int(11) DEFAULT '0',
  PRIMARY KEY (`entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
