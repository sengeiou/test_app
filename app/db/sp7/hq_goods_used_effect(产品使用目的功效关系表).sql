CREATE TABLE `hq_goods_used_effect` (
`id` INT(11) NOT NULL AUTO_INCREMENT,
`name` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`pid` INT(11) DEFAULT '0',
`display_compare_sort` INT(11) DEFAULT NULL,
`display_compare` TINYINT(1) DEFAULT '0',
`display_compare_name` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
PRIMARY KEY (`id`)
ENGINE=INNODB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;