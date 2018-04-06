update hq_composition set mid=md5(CONCAT("2016bevol",id,"composition"));

update hq_composition c,
(
select id,mid from 
(
SELECT pid FROM `hq_composition` h  where pid>0
) p join hq_composition q on p.pid=q.id
) q set c.mpid=q.mid where c.pid=q.id and c.pid>0;
