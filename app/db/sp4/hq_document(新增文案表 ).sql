CREATE TABLE `hq_document` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`type` VARCHAR(50) NOT NULL,
	`text` TEXT NOT NULL,
	`hidden_status` INT NOT NULL DEFAULT '0',
	`delete_status` INT NOT NULL DEFAULT '0',
	`create_status` INT NOT NULL DEFAULT '0',
	`update_status` INT NOT NULL DEFAULT '0',
	`other` VARCHAR(50) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)
COMMENT='文案表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;