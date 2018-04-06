//所有实体的日统计数量
function copyEntityInfo(host){
	var tabs=["entity_composition","entity_find","entity_user_part_lists","entity_lists"];
	//var tabs=["entity_goods","entity_composition","entity_find","entity_user_part_lists","entity_lists"];
	var str="tmpl_";
	var conn=new Mongo(host);
	var toDb=conn.getDB("data_statistics");
	//var oDb=conn.getDB("bevol_test");
	for(var i=0;i<tabs.length;i++){
		var tab=tabs[i];
		var outTab=str+tab;
		//删除昨天的
		toDb[outTab].remove({});
		db[tab].find({hidden:0,deleted:0},{id:1,commentNum:1,hitNum:1,likeNum:1,title:1}).forEach(function(o){
			toDb[outTab].save({id:NumberLong(o.id),commentNum:NumberLong(o.commentNum),hitNum:NumberLong(o.hitNum),likeNum:NumberLong(o.likeNum),title:o.title});
		})
	}
}
copyEntityInfo("mongodb://chj:chj@localhost:27017/test");