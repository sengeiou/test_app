CREATE PROCEDURE `pro_goods_type12`()
	COMMENT '1 2号库分类存储过程'
BEGIN

DECLARE ids INT(11);
SELECT MIN(id) INTO ids FROM hq_goods WHERE  (data_type =1  or `data_type` =2) and category=0 ;

update hq_goods set category=17 where categoryStr="普通类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=40 where categoryStr="染发类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=40 where categoryStr="烫发类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=41 where categoryStr="育发类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=36 where categoryStr="除臭型"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=35 where categoryStr="健美类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=33 where categoryStr="脱毛类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=34 where categoryStr="美乳类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=14 where categoryStr="祛斑类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=13 where categoryStr="防晒类"  and category =0 and  id > ids and (data_type =1  or data_type =2);
update hq_goods set category=36 where categoryStr="除臭类、祛斑类"  and category =0 and  id > ids and (data_type =1  or data_type =2);

update hq_goods set category=13 where (
trim(categoryStr)="祛斑类、防晒类" or
categoryStr="祛斑类（仅具物理遮盖作用）" or categoryStr='祛斑类（仅具物理遮盖作用）、防晒类'
or categoryStr="防晒类、祛斑类" or categoryStr='防晒类、祛斑类（仅具物理遮盖作用）' )
  and category =0 and  id > ids and (data_type =1  or data_type =2);



# 条件归类
UPDATE hq_goods SET category = 138
WHERE
category = 17 AND
(title LIKE '%发%' AND title LIKE '%洗发%')
   and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 139
WHERE
category = 17 AND
(title LIKE '%发%' OR title LIKE '%定型%' OR title LIKE '%摩丝%') AND title NOT LIKE '%洗发%' AND title NOT LIKE '%面%' AND title NOT LIKE '%颜%' AND title NOT LIKE '%剃须%' AND title NOT LIKE '%卸妆%'
   and  id > ids and (data_type =1  or data_type =2);




UPDATE hq_goods SET category = 106
WHERE
category = 17 AND
(title LIKE '%洁面%' OR title LIKE '%洁颜%' OR title LIKE '%洗面%' OR title LIKE '%洗颜%' OR title LIKE '%面部清洁%' OR title LIKE '%洁肤%') and title NOT LIKE '%洁肤水%' and title NOT LIKE '%洁肤液%'
   and  id > ids and (data_type =1  or data_type =2);



UPDATE hq_goods SET category = 107
WHERE
category = 17 AND
(title LIKE '%化妆水%' OR title LIKE '%保湿水%' OR title LIKE '%保湿喷雾%' OR title LIKE '%纯露%' OR title LIKE '%花水%' OR title LIKE '%肤水%' OR title LIKE '%肤液%' OR title LIKE '%保湿液%' OR title LIKE '%调理水%') AND title NOT LIKE '%霜%' AND title NOT LIKE '%乳液%' AND title NOT LIKE '%面膜%' AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
  and  id > ids and (data_type =1  or data_type =2);



UPDATE hq_goods SET category = 108
WHERE
category = 17 AND
(title LIKE '%面霜%' OR title LIKE '%乳液%' OR title LIKE '%晚霜%' OR title LIKE '%日霜%' OR title LIKE '%美白霜%' OR title LIKE '%肤霜%' OR title LIKE '%保湿霜%' OR title LIKE '%乳霜%' OR title LIKE '%夜霜%' OR title LIKE '%颜霜%' OR title LIKE '%凝霜%' OR title LIKE '%护霜%' OR title LIKE '%修复霜%' OR title LIKE '%弹力霜%' OR title LIKE '%美白乳%' OR title LIKE '%保湿乳%' OR title LIKE '%肤乳%' OR title LIKE '%爽乳%' OR title LIKE '%凝胶%' OR title LIKE '%芦荟胶%' OR title LIKE '%啫喱%' OR title LIKE '%肤露%' OR title LIKE '%护露%') AND title NOT LIKE '%洁面%' AND title NOT LIKE '%洁颜%' AND title NOT LIKE '%洗面%' AND title NOT LIKE '%洗颜%' AND title NOT LIKE '%防晒%' AND title NOT LIKE '%身%' AND title NOT LIKE '%手%' AND title NOT LIKE '%足%' AND title NOT LIKE '%颈%' AND title NOT LIKE '%臀%' AND title NOT LIKE '%脚%' AND title NOT LIKE '%腿%' AND title NOT LIKE '%卸妆%' AND title NOT LIKE '%隔离%' AND title NOT LIKE '%须%' AND title NOT LIKE '%精华%' AND title NOT LIKE '%洁肤%' AND title NOT LIKE '%沐浴%' AND title NOT LIKE '%发%' AND title NOT LIKE '%眼%' AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
  and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 109
WHERE
category = 17 AND
(title LIKE '%精华%' OR title LIKE '%肌底液%' OR title LIKE '%原液%' OR title LIKE '%原生液%') AND title NOT LIKE '%原野之谜%' AND title NOT LIKE '%洁面%' AND title NOT LIKE '%洁颜%' AND title NOT LIKE '%洗面%' AND title NOT LIKE '%洗颜%' AND title NOT LIKE '%防晒%' AND title NOT LIKE '%身%' AND title NOT LIKE '%手%' AND title NOT LIKE '%足%' AND title NOT LIKE '%颈%' AND title NOT LIKE '%睫%' AND title NOT LIKE '%眉%' AND title NOT LIKE '%臀%' AND title NOT LIKE '%脚%' AND title NOT LIKE '%腿%' AND title NOT LIKE '%卸妆%' AND title NOT LIKE '%隔离%' AND title NOT LIKE '%男%' AND title NOT LIKE '%须%' AND title NOT LIKE '%洁肤%' AND title NOT LIKE '%沐浴%' AND title NOT LIKE '%发%' AND title NOT LIKE '%面膜%' AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 110
WHERE
category = 17 AND
(title LIKE '%眼部%' OR title LIKE '%眼霜%' OR title LIKE '%眼精华%' OR title LIKE '%眸%' OR title LIKE '%眼胶%' OR title LIKE '%眼角%' OR title LIKE '%美目%' OR title LIKE '%眼膜%' OR title LIKE '%眼唇%') AND title NOT LIKE '%眼线%' AND title NOT LIKE '%眼影%' AND title NOT LIKE '%睫%' AND title NOT LIKE '%暗疮%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 111
WHERE
category = 17 AND
(title LIKE '%面膜%' OR title LIKE '%泥浆%' OR title LIKE '%冻膜%' OR title LIKE '%面贴膜%' OR title LIKE '%软膜%') AND title NOT LIKE '%身%' AND title NOT LIKE '%体%' AND title NOT LIKE '%手%' AND title NOT LIKE '%足%' AND title NOT LIKE '%颈%' AND title NOT LIKE '%脚%' AND title NOT LIKE '%腿%' AND title NOT LIKE '%臀%' AND title NOT LIKE '%发%' AND title NOT LIKE '%眼%' AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 112
WHERE
category = 17 AND
(title LIKE '%卸妆%' OR title LIKE '%洁肤水%' OR title LIKE '%洁肤液%' OR title LIKE '%洁肤油%' OR title LIKE '%洁颜油%' OR title LIKE '%清洁乳%' OR title LIKE '%清洁霜%' OR title LIKE '%洁面乳%' OR title LIKE '%洁面油%') AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 113
WHERE
category = 17 AND
(title LIKE '%防晒%') AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 115
WHERE
category = 17 AND
(title LIKE '%角质%' OR title LIKE '%磨砂%') AND title NOT LIKE '%身%' AND title NOT LIKE '%体%' AND title NOT LIKE '%手%' AND title NOT LIKE '%足%' AND title NOT LIKE '%臀%' AND title NOT LIKE '%脚%' AND title NOT LIKE '%腿%' AND title NOT LIKE '%颈%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 116
WHERE
category = 17 AND
(title LIKE '%痘%' OR title LIKE '%粉刺%' OR title LIKE '%痤疮%' OR title LIKE '%暗疮%' OR title LIKE '%黑头%') AND title NOT LIKE '%洁面%' AND title NOT LIKE '%洁颜%' AND title NOT LIKE '%洗面%' AND title NOT LIKE '%洗颜%' AND title NOT LIKE '%面部清洁%' AND title NOT LIKE '%洁肤%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 118
WHERE
category = 17 AND
(title LIKE '%粉底%' OR title LIKE '%粉饼%' OR title LIKE '%蜜粉%' OR title LIKE '%矿物质粉%' OR title LIKE '%散粉%' OR title LIKE '%粉霜%' OR title LIKE '%粉膏%' OR title LIKE '%粉凝霜%' OR title LIKE '%修颜%' OR title LIKE '%底霜%' OR title LIKE '%修容%' OR title LIKE '%粉球%' OR title LIKE '%嫩肤粉%' OR title LIKE '%粉蜜%') AND title NOT LIKE '%BB%' AND title NOT LIKE '%CC%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 119
WHERE
category = 17 AND
(title LIKE '%BB%' OR title LIKE '%CC%' OR title LIKE '%调色霜%' OR title LIKE '%气垫%') AND title NOT LIKE '%洁面%' AND title NOT LIKE '%洁颜%' AND title NOT LIKE '%洗面%' AND title NOT LIKE '%洗颜%' AND title NOT LIKE '%面部清洁%' AND title NOT LIKE '%洁肤%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 120
WHERE
category = 17 AND
(title LIKE '%隔离%' OR title LIKE '%打底%' OR title LIKE '%妆前%') AND title NOT LIKE '%CC%' AND title NOT LIKE '%BB%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 121
WHERE
category = 17 AND
(title LIKE '%唇%' OR title LIKE '%口红%') AND title NOT LIKE '%眼%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 122
WHERE
category = 17 AND
(title LIKE '%睫毛%' OR title LIKE '%眉睫%' OR title LIKE '%美睫%') AND title NOT LIKE '%卸妆%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 123
WHERE
category = 17 AND
(title LIKE '%眉笔%' OR title LIKE '%眉彩%' OR title LIKE '%修眉%' OR title LIKE '%眉部%' OR title LIKE '%眉粉%' OR title LIKE '%眉膏%' OR title LIKE '%眉胶%' OR title LIKE '%眉毛%') AND title NOT LIKE '%卸妆%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 124
WHERE
category = 17 AND
(title LIKE '%腮红%' OR title LIKE '%胭脂%' OR title LIKE '%颊彩%') AND title NOT LIKE '%唇%' AND title NOT LIKE '%眼%' AND title NOT LIKE '%眉%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 125
WHERE
category = 17 AND
(title LIKE '%眼线%') AND title NOT LIKE '%卸妆%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 126
WHERE
category = 17 AND
(title LIKE '%眼影%' OR title LIKE '%眼彩%') AND title NOT LIKE '%卸妆%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 127
WHERE
category = 17 AND
(title LIKE '%遮瑕%' OR title LIKE '%明彩笔%') AND title NOT LIKE '%粉底%' AND title NOT LIKE '%粉饼%' AND title NOT LIKE '%蜜粉%' AND title NOT LIKE '%矿物质粉%' AND title NOT LIKE '%散粉%' AND title NOT LIKE '%粉霜%' AND title NOT LIKE '%粉膏%' AND title NOT LIKE '%粉凝霜%' AND title NOT LIKE '%修颜%' AND title NOT LIKE '%底霜%' AND title NOT LIKE '%修容%' AND title NOT LIKE '%粉球%' AND title NOT LIKE '%嫩肤粉%' AND title NOT LIKE '%粉蜜%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 129
WHERE
category = 17 AND
(title LIKE '%沐浴%' OR title LIKE '%皂%' OR title LIKE '%浴盐%' OR title LIKE '%浴宝%' OR title LIKE '%浴球%' OR title LIKE '%泡泡浴%' OR title LIKE '%泡澡%' OR title LIKE '%洗手%') AND title NOT LIKE '%洁面%' AND title NOT LIKE '%洁颜%' AND title NOT LIKE '%洗面%' AND title NOT LIKE '%洗颜%' AND title NOT LIKE '%面部清洁%' AND title NOT LIKE '%洁肤%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 130
WHERE
category = 17 AND
(title LIKE '%体乳%' OR title LIKE '%体霜%' OR title LIKE '%浴后%' OR title LIKE '%腿%' OR title LIKE '%足%' OR title LIKE '颈%' OR title LIKE '%脚%') AND title NOT LIKE '%防晒%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 131
WHERE
category = 17 AND
(title LIKE '%手%') AND title NOT LIKE '%洗手%'
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 132
WHERE
category = 17 AND
(title LIKE '%甲%')
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 133
WHERE
category = 17 AND
(title LIKE '%脱毛%' OR title LIKE '%毛后%')
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 134
WHERE
category = 17 AND
(title LIKE '%胸%')
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 137
WHERE
category = 17 AND
(title LIKE '%花露水%' OR title LIKE '%爽身粉%')
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 142
WHERE
category = 17 AND
(title LIKE '%香水%' OR title LIKE '%香氛%' OR title LIKE '%古龙水%' OR title LIKE '%男香%' OR title LIKE '%香膏%')
 and  id > ids and (data_type =1  or data_type =2);

UPDATE hq_goods SET category = 8
WHERE
category = 17 AND
(title LIKE '%润霜%' OR title LIKE '%护乳%') AND title NOT LIKE '%洁面%' AND title NOT LIKE '%洁颜%' AND title NOT LIKE '%洗面%' AND title NOT LIKE '洗颜' AND title NOT LIKE '%防晒%' AND title NOT LIKE '%身%' AND title NOT LIKE '%手%' AND title NOT LIKE '%足%' AND title NOT LIKE '%颈%' AND title NOT LIKE '%臀%' AND title NOT LIKE '%脚%' AND title NOT LIKE '%腿%' AND title NOT LIKE '%卸妆%' AND title NOT LIKE '%隔离%' AND title NOT LIKE '%须%' AND title NOT LIKE '%精华%' AND title NOT LIKE '%洁肤%' AND title NOT LIKE '%沐浴%' AND title NOT LIKE '%发%' AND title NOT LIKE '%眼%' AND title NOT LIKE '%痘%' AND title NOT LIKE '%粉刺%' AND title NOT LIKE '%痤疮%' AND title NOT LIKE '%角质%' AND title NOT LIKE '%暗疮%'
 and  id > ids and (data_type =1  or data_type =2);

# 剩下未分类处理
UPDATE hq_goods SET category = 44 where category = 17
 and  id > ids and (data_type =1  or data_type =2);

create table d1_cg
select * from hq_goods where 1=1
 and  id > ids and (data_type =1  or data_type =2);

create table d1_category
select h.id,d.categoryStr,d.category from (select id from d1_cg) h join hq_goods g join hq_goods d on h.id=g.id and g.processid=d.processid
and  g.id > ids and (g.data_type =1  or g.data_type =2);

update  hq_goods h,d1_category c set h.category=c.category where c.id=h.id
 and  h.id > ids and (h.data_type =1  or h.data_type =2);

drop table d1_category;
drop table d1_cg;

UPDATE hq_goods SET category = 47
WHERE
((title LIKE '%润唇%') or  (title LIKE '%护唇%') or  (title LIKE '%唇膜%'))
 and  id > ids and (data_type =1  or data_type =2);



update hq_goods set category=category-100 where category>=100 and category<1700
 and  id > ids and (data_type =1  or data_type =2);

END