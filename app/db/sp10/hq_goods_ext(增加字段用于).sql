ALTER TABLE `hq_goods_ext`
	ADD COLUMN `allow_comment` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许发送产品评论'
