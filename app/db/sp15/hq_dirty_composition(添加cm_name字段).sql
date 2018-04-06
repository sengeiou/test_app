

alter  table  hq_dirty_composition  add  cm_name  varchar(255);
alter  table  hq_dirty_composition  add  name1  varchar(255);
update hq_dirty_composition set name1=`name`;
