ALTER TABLE `hq_goods_category` 
	ADD COLUMN `cate_effect_id` tinyint(1) NOT NULL DEFAULT '0' COMMENT '功效表id',
	ADD COLUMN `cate_effect_type_name` varchar(255)
	) 