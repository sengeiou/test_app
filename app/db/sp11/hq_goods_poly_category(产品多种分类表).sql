CREATE TABLE `hq_goods_poly_category` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`goods_id` int(11) NOT NULL,
`category_id` int(4) NOT NULL,
`category_ids` text COLLATE utf8_unicode_ci,
`exist_category_ids` tinyint(1) NOT NULL DEFAULT '0',
`update_time` int(11) DEFAULT NULL,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci |