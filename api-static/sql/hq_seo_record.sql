/*
Navicat MySQL Data Transfer

Source Server         : 测试机
Source Server Version : 50173
Source Host           : 121.199.78.245:3306
Source Database       : bevol_test

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2016-12-09 16:46:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_seo_record
-- ----------------------------
CREATE TABLE `hq_seo_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `urls` varchar(255) DEFAULT NULL,
  `operate_type` varchar(255) DEFAULT NULL COMMENT '操作',
  `results` varchar(255) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb  DEFAULT CHARSET=utf8;
