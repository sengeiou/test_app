/*
Navicat MySQL Data Transfer

Source Server         : 正式
Source Server Version : 50629
Source Host           : rdsoxdt0k8m3y0561p1fo.mysql.rds.aliyuncs.com:3306
Source Database       : bevol_online

Target Server Type    : MYSQL
Target Server Version : 50629
File Encoding         : 65001

Date: 2016-12-27 16:34:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_goods_search
-- ----------------------------
DROP TABLE IF EXISTS `hq_goods_search`;
CREATE TABLE `hq_goods_search` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `category` int(11) NOT NULL DEFAULT '0',
  `safety_1_num` float NOT NULL,
  `cps_search` text,
  `cps` text,
  `tag_ids` varchar(255) DEFAULT NULL,
  `update_time` int(11) NOT NULL DEFAULT '0',
  `like_num` int(11) NOT NULL DEFAULT '0',
  `collection_num` int(11) NOT NULL DEFAULT '0',
  `comment_num` int(11) NOT NULL DEFAULT '0',
  `notlike_num` int(11) NOT NULL DEFAULT '0',
  `hit_num` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=139931 DEFAULT CHARSET=utf8;
