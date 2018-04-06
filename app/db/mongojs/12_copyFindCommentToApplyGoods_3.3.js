//文章的评论复制到福利社活动中
function copyFindCommentToApplyGodos(){
	//var findIds=[103];
	//var applyGoodsIds=[29];
	var findIds=[497,484,450,443];
	var applyGoodsIds=[82,81,80,79];
	for(var i=0;i<findIds.length;i++){
		var findId=findIds[i];
		var applyGoodsId=applyGoodsIds[i];
		db["entity_comment_find"].find({hidden:0,entityId:NumberLong(findId)}).forEach(function(c){
			c["entityId"]=applyGoodsId;
			//c["_id"]=null;
			var obj=db["entity_comment_apply_goods2_inc"].findAndModify({update:{$inc:{id:NumberLong(1)}},$upsert:true,$new:true});
			c["id"]=obj.id+1;
			db["entity_comment_apply_goods2"].save(c);
		})
	}
}
copyFindCommentToApplyGodos();