/*
Navicat MySQL Data Transfer

Source Server         : 测试机
Source Server Version : 50173
Source Host           : 121.199.78.245:3306
Source Database       : bevol_test

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2016-12-16 17:31:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hq_seo_await
-- ----------------------------
CREATE TABLE `hq_seo_await` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mid` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL COMMENT '位置',
  `data_type` varchar(255) DEFAULT NULL COMMENT '数据类型：find、goods等',
  `operate_type` varchar(255) DEFAULT NULL COMMENT ' 操作方式：add、update、delete',
  `data_source` varchar(255) DEFAULT NULL COMMENT '数据来源：pc 、mobile',
  `state` int(11) DEFAULT '0' COMMENT '状态',
  `create_time` int(11) DEFAULT NULL COMMENT '添加时间',
  `update_time` int(11) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
