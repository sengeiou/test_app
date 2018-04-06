CREATE TABLE `hq_lists` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(50) NOT NULL DEFAULT '0',
	`image` VARCHAR(50) NOT NULL DEFAULT '0',
	`tag` VARCHAR(50) NOT NULL DEFAULT '',
	`descp` VARCHAR (800) NOT NULL,
	`goods_ids` VARCHAR(255) NOT NULL DEFAULT '0',
	`create_time` INT NOT NULL DEFAULT '0',
	`update_time` INT NOT NULL DEFAULT '0',
	`hide_status` INT NOT NULL DEFAULT '0',
	`delete_status` INT NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)
COMMENT='清单表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;