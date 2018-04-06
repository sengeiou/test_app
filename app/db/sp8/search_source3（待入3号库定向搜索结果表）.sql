/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 50096
Source Host           : localhost:3306
Source Database       : bevol_spider

Target Server Type    : MYSQL
Target Server Version : 50096
File Encoding         : 65001

Date: 2016-11-01 14:35:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for search_source3
-- ----------------------------
DROP TABLE IF EXISTS `search_source3`;
CREATE TABLE `search_source3` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(150) default NULL,
  `process_id` varchar(40) default NULL,
  `apply_sn` varchar(100) default NULL,
  `enterprise_name` varchar(100) default NULL,
  `product_name` varchar(100) default NULL,
  `province_confirm` varchar(100) default NULL,
  `page_process_id` varchar(40) default NULL,
  `url` varchar(256) default NULL,
  `spider_id` int(11) default NULL,
  `update_time` int(11) NOT NULL,
  `work_status` int(2) default '0',
  `work_result` int(2) default '0',
  `work_time` int(11) default '0',
  `work_weight` int(2) default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `source_list3_pid` USING BTREE (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=460149 DEFAULT CHARSET=utf8;
