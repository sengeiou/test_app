CREATE TABLE `hq_goods_special_category` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`goods_categoryStr` varchar(255) COLLATE utf8_unicode_ci NOT NULL ,
`category_id` int(11) DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci