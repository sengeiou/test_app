ALTER TABLE `hq_user`
	ADD COLUMN `skin_test_time` int(11) NOT NULL DEFAULT '0' COMMENT '用户肤质测试时间'
ALTER TABLE `hq_user`
	ADD COLUMN `first_skin_test_time` int(11) NOT NULL DEFAULT '0' COMMENT '用户第一次肤质测试时间'
