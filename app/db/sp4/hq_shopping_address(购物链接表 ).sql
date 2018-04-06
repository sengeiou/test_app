CREATE TABLE `hq_shopping_address` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(50) NOT NULL,
	`logo` varchar(225) NOT NULL DEFAULT '',
	`url` varchar(225) NOT NULL DEFAULT '',
	`hidden` tinyint(1) NOT NULL DEFAULT '0' COMMENT '隐藏',
	`deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
	`crdate` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
	`tstamp` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
	PRIMARY KEY (`id`)
)
COMMENT='购物链接表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;