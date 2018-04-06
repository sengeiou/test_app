/*
Navicat MySQL Data Transfer

Source Server         : 测试机
Source Server Version : 50173
Source Host           : 121.199.78.245:3306
Source Database       : bevol_test

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2016-12-08 18:52:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_static_record
-- ----------------------------
CREATE TABLE `hq_static_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mid` varchar(255) DEFAULT NULL,
  `data_type` varchar(255) DEFAULT NULL COMMENT '数据类型：find、goods等',
  `path` varchar(255) DEFAULT NULL COMMENT '位置',
  `data_source` varchar(255) DEFAULT NULL COMMENT '数据来源：PC 、mobile',
  `state` int(11) DEFAULT NULL COMMENT '状态',
  `create_time` int(11) DEFAULT NULL COMMENT '添加时间',
  `update_time` int(11) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
