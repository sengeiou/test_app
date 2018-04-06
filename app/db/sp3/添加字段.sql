alter table hq_find_type add sort int(4) default '0';
ALTER TABLE `hq_lists`
	ADD COLUMN `sort` INT NOT NULL DEFAULT '0' AFTER `goods_ids`,
	ADD COLUMN `publish_time` INT NOT NULL DEFAULT '0' AFTER `sort`;
ALTER TABLE `hq_tags`
	ADD COLUMN `top` INT(11) NOT NULL DEFAULT '0' AFTER `update_time`;
