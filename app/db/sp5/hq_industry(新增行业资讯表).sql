CREATE TABLE `hq_industry` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`descp` LONGTEXT NULL COLLATE 'utf8_general_ci',
	`type` TINYINT(1) UNSIGNED ZEROFILL NOT NULL DEFAULT '1',
    `keyword` VARCHAR(225) NULL DEFAULT NULL COLLATE 'utf8_unicode_ci',
    `create_time` BIGINT(11) NOT NULL DEFAULT '0',
	`update_time` BIGINT(11) NOT NULL DEFAULT '0',
	`hidden_status` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0显示，1删除',
	`deleted_status` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0不删、1删除',
	`image` VARCHAR(255) NULL DEFAULT NULL COMMENT 'banner图' COLLATE 'utf8_general_ci',
	`header_image` VARCHAR(255) NOT NULL DEFAULT '0' COMMENT '内页图' COLLATE 'utf8_unicode_ci',
	`subhead` TEXT NULL COLLATE 'utf8_general_ci',
	`sort` INT(11) NOT NULL DEFAULT '100' COMMENT '排序',
	`tag` VARCHAR(500) NULL DEFAULT NULL COLLATE 'utf8_unicode_ci',
	`publish_time` INT(11) NULL DEFAULT NULL,
	`author` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_unicode_ci',
	`author_id` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;