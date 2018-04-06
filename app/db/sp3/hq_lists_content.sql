CREATE TABLE `hq_lists_content` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`lists_id` INT NOT NULL DEFAULT '0',
	`goods_id` INT NOT NULL DEFAULT '0',
	`content` TEXT NOT NULL,
	`other` TEXT NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `lists_id` (`lists_id`),
	INDEX `goods_id` (`goods_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
ALTER TABLE `hq_lists_content`
	CHANGE COLUMN `content` `content` VARCHAR(225) NOT NULL AFTER `goods_id`,
	CHANGE COLUMN `other` `other` VARCHAR(50) NOT NULL AFTER `content`;
ALTER TABLE `hq_lists_content`
	CHANGE COLUMN `other` `other` VARCHAR(50) NOT NULL DEFAULT '0' AFTER `content`;
ALTER TABLE `hq_lists_content`
	DROP INDEX `goods_id`,
	DROP INDEX `lists_id`,
ADD UNIQUE INDEX `lists_id` (`lists_id`, `goods_id`);
