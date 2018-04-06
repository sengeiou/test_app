添加清单管理菜单
INSERT INTO `bevol`.`hq_menu` (`id`, `sorting`, `app`, `title`, `icon`, `type`, `model`, `action`, `target`) VALUES (439, 16, 'Admin', '清单管理', 'icon-file-text', 1, 'Lists', 'index', '_self');
添加最受欢迎产品管理
INSERT INTO `bevol`.`hq_menu` (`id`, `sorting`, `parent_id`, `app`, `title`, `model`, `action`, `target`) VALUES (440, 419, 416, 'Admin', '最受欢迎产品', 'Goods', 'pop', '_self');
UPDATE `bevol`.`hq_menu` SET `type`=1 WHERE  `id`=440;
添加标签管理
INSERT INTO `bevol`.`hq_menu` (`id`, `sorting`, `app`, `title`, `icon`, `model`, `action`, `target`) VALUES (441, 17, 'Admin', '标签管理', 'icon-file-text', 'Tags', 'index', '_self');
添加标签管理发现子菜单
INSERT INTO `bevol`.`hq_menu` (`id`, `sorting`, `parent_id`, `app`, `title`, `type`, `model`, `action`, `param`, `target`) VALUES (442, 1, 441, 'Admin', '发现管理', 1, 'Tags', 'index', 'type=find', '_self');
