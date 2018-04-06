CREATE TABLE `hq_goods_tag_composition` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `tag_id` int(11) DEFAULT NULL,
 `composition_id` int(11) DEFAULT NULL,
 `status` tinyint(1) DEFAULT '0',
 `create_stamp` int(11) NOT NULL,
 `is_main` tinyint(1) NOT NULL DEFAULT '0' ,
 `main_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
 PRIMARY KEY (`id`)
 )
 ENGINE=InnoDB AUTO_INCREMENT=17492 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci