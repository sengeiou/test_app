CREATE TABLE `hq_user_black_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `start_time` int(11) NOT NULL DEFAULT '0' COMMENT '禁言开始时间',
  `end_time` int(11) NOT NULL DEFAULT '0' COMMENT '禁言结束时间',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '数据生成时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '数据修改时间',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '被禁言原因描述',
  `state` tinyint(1) NOT NULL COMMENT '禁言类型:1永久 2时效性的',
  `delete_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除状态:默认0 没有删除 1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
