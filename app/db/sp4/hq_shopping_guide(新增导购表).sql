CREATE TABLE `hq_shopping_guide` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`goods_id` INT NOT NULL DEFAULT '0',
	`type` INT NOT NULL DEFAULT '0',
	`url` TEXT NOT NULL,
	`hidden_status` INT NOT NULL DEFAULT '0',
	`delete_status` INT NOT NULL DEFAULT '0',
	`create_time` INT NOT NULL DEFAULT '0',
	`update_time` INT NOT NULL DEFAULT '0',
	`other` VARCHAR(50) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)
COMMENT='导购表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;