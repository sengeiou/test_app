CREATE TABLE `hq_goods_tag_result` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`goods_id` int(11) DEFAULT NULL,
`auto_tag_ids` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`auto_tag_names` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`made_tag_ids` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`made_tag_names` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`tag_ids` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`tag_names` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`create_stamp` int(11) DEFAULT NULL,
`status` tinyint(1) DEFAULT '0',
`hidden` tinyint(1) NOT NULL DEFAULT '0',
`update_time` int(11) DEFAULT NULL,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB AUTO_INCREMENT=343 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci |