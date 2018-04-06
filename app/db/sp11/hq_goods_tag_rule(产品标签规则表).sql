CREATE TABLE `hq_goods_tag_rule` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `val_json` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
 `create_stamp` int(11) DEFAULT NULL,
 `status` tinyint(1) DEFAULT '0',
 `tag_id` int(11) DEFAULT NULL,
 `rule_1` text COLLATE utf8_unicode_ci,
 PRIMARY KEY (`id`)
 )
 ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci