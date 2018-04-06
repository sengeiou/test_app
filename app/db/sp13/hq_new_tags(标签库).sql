CREATE TABLE `hq_new_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL COMMENT '标签名',
  `tabs` varchar(50) NOT NULL COMMENT '标签组名',
  `type` varchar(50) DEFAULT NULL COMMENT '标签分类',
  `hidden_status` int(11) NOT NULL DEFAULT '0',
  `delete_status` int(11) NOT NULL DEFAULT '0',
  `create_time` int(11) NOT NULL,
  `update_time` int(11) DEFAULT NULL,
  `top` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22238 DEFAULT CHARSET=utf8 COMMENT='标签库'