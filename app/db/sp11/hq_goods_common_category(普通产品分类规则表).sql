CREATE TABLE `hq_goods_common_category` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`rule_1` text COLLATE utf8_unicode_ci ,
`rule_2` text COLLATE utf8_unicode_ci ,
`category_id` int(11) DEFAULT NULL,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci