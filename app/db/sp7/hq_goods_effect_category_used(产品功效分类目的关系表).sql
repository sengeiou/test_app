CREATE TABLE `hq_goods_effect_category_used` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`category_cate_id` int(11) DEFAULT NULL COMMENT '功效分类id',
`category_cate_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '功效名称',
`cps_used` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '使用功能目的id 以逗号隔开',
`cps_used_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '使用目的 以逗号隔开',
`effect_id` int(11) DEFAULT NULL COMMENT '功效id',
`effect_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '功效名称',
`cps_active` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`category_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`category_ids` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`cps_active_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`effect_pid` int(11) DEFAULT NULL,
`effect_pid_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
`display_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '显示的名称',
`display_type` tinyint(4) DEFAULT '0' COMMENT '0 所有位置都显示 1显示在详细 2显示在对比',
`dispaly_sort` tinyint(1) DEFAULT '0' COMMENT '显示的排序',
`display_compare_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '对比名称',
`display_compare_sort` int(11) DEFAULT NULL,
`display_compare` tinyint(1) DEFAULT '0',
PRIMARY KEY (`id`)
ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci |
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;