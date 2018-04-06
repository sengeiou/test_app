CREATE TABLE `hq_doyen` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`goods_id` INT(11) NOT NULL DEFAULT '0',
	`user_id` INT(11) NOT NULL DEFAULT '0',
	`doyen_comment` TEXT NOT NULL,
	`hidden_status` INT(11) NOT NULL DEFAULT '0',
	`delete_status` INT(11) NOT NULL DEFAULT '0',
	`create_time` INT(11) NOT NULL DEFAULT '0',
	`update_time` INT(11) NOT NULL DEFAULT '0',
	`other` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)
COMMENT='修行说表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE `hq_doyen`
ADD INDEX `goods_id` (`goods_id`);
