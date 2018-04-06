/*
Navicat MySQL Data Transfer

Source Server         : 测试机
Source Server Version : 50521
Source Host           : 121.199.78.245:3306
Source Database       : bevol

Target Server Type    : MYSQL
Target Server Version : 50521
File Encoding         : 65001

Date: 2016-08-10 14:49:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_composition_rule
-- ----------------------------
DROP TABLE IF EXISTS `hq_composition_rule`;
CREATE TABLE `hq_composition_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `composition_ids` text COLLATE utf8_unicode_ci NOT NULL,
  `create_time` int(11) NOT NULL,
  `update_time` int(11) NOT NULL,
  `delete_status` int(11) NOT NULL DEFAULT '0',
  `hidden_status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='成分搜索页面显示的规则';
