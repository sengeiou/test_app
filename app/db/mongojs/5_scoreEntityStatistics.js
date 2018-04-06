
function scoreEntityStatistics() {
	var tbs=["goods"];
	var gargeNums=[1,2,3,4,5];
	//结果表
	var grage="score_entity_statistics";
	//一天
	var createStamp=parseInt(new Date()/1000);
	var item={};
	var gt=createStamp-(60*60*24);
	var lt=createStamp;
	for(var i=0;i<tbs.length;i++) {
		var enty="entity_comment_"+tbs[i];
		for(var j=0;j<gargeNums.length;j++) {
			var curG=gargeNums[j];
			var excct=db[enty].count({"createStamp":{"$gt":gt,"$lt":lt},"score":curG,content:{"$exists":true}});
			var notexcct=db[enty].count({"createStamp":{"$gt":gt,"$lt":lt},"score":curG,content:{"$exists":false}});
			item[curG+"_content"]=parseInt(excct);
			item[curG+"_notcontent"]=parseInt(notexcct?notexcct:0);
		}
		item["createStamp"]=createStamp;
		item["tname"]=enty;
		db[grage].insert(item);
	}
}
scoreEntityStatistics();