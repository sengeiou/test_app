/*
Navicat MySQL Data Transfer

Source Server         : 测试机
Source Server Version : 50521
Source Host           : 121.199.78.245:3306
Source Database       : bevol

Target Server Type    : MYSQL
Target Server Version : 50521
File Encoding         : 65001

Date: 2016-08-10 15:00:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_like_goods_mong
-- ----------------------------
DROP TABLE IF EXISTS `hq_like_goods_mong`;
CREATE TABLE `hq_like_goods_mong` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entityid` int(11) DEFAULT NULL COMMENT '对应产品id',
  `skin` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '对应肤质类型',
  `entityid_skin` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品id+肤质类型 分组使用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
