
//统计平均值
function commentAvgScort() {
	
	db["entity_goods"].find({"commentNum":{"$gt":0},"hidden":0,"deleted":0}).forEach(function(o){
		var commentAvgScore=o.commentSumScore/o.commentNum;
		if(commentAvgScore<=5) {
			commentAvgScore=Math.round(commentAvgScore);
			db["entity_goods"].update({id:o.id},{commentAvgScore:NumberInt(commentAvgScore)});
		}
	});
}

function updateComment(){
	var as = ["goods","user_part_lists"];
	for(var i = 0; i < as.length; i++) {
		var cc=as[i];
		var like2="entity_comment_"+cc; 
		var likeColtype="entity_commentcount_type_"+cc; 
		db[likeColtype].remove({});
		//mapreduece 统计
		if(cc=="goods") {
			db[like2].mapReduce(function(){
					if((!this.pid||this.pid==0)&&(!this.hidden||this.hidden==0)&&this.entityId>0&&(!this.deleted||this.deleted==0)) {
						if(this.score>0) {
							if(this.content)
								emit({"hascontent":this.entityId,entityId:this.entityId},1); 
							
							emit({"allcontent":this.entityId,entityId:this.entityId},1); 
								emit({"score":this.entityId,entityId:this.entityId},this.score); 
								
							if(!this.version) {
								emit({"score_lt3":this.entityId,entityId:this.entityId},this.score); 
							}else {
								emit({"score_gt3":this.entityId,entityId:this.entityId},this.score); 
							}
							
							if(!this.version) {
								emit({"content_lt3":this.entityId,entityId:this.entityId},1); 
							}else {
								emit({"content_gt3":this.entityId,entityId:this.entityId},1); 
							}
							
							if(this.skin) {
								emit({"skinTest":this.entityId,entityId:this.entityId},1); 
							}
						}
						if((!this.hidden||this.hidden==0)&&this.entityId>0&&(!this.deleted||this.deleted==0)) {
							emit({"allCommentNum":this.entityId,entityId:this.entityId},1); 
						}
						if(this.pid>0&&(!this.hidden||this.hidden==0)&&this.entityId>0&&(!this.deleted||this.deleted==0)) {
							emit({"twoLevelCommentNum":this.entityId,entityId:this.entityId},1); 
						}
					}
				},function(key,values){ 
					return Array.sum(values)
				},{
				out:likeColtype
				}
			);
		} else {
		db[like2].mapReduce(function(){
		if((!this.pid||this.pid==0)&&(!this.hidden||this.hidden==0)&&this.entityId>0&&(!this.deleted||this.deleted==0)) {
			
			emit({"allcontent":this.entityId,entityId:this.entityId},1); 
			
		}
		if((!this.hidden||this.hidden==0)&&this.entityId>0&&(!this.deleted||this.deleted==0)) {
		emit({"allCommentNum":this.entityId,entityId:this.entityId},1); 
		}
		if(this.pid>0&&(!this.hidden||this.hidden==0)&&this.entityId>0&&(!this.deleted||this.deleted==0)) {
		emit({"twoLevelCommentNum":this.entityId,entityId:this.entityId},1); 
		}
		},function(key,values){ 
		return Array.sum(values)
		},{
		out:likeColtype
		});

		}
		//更新评论数量
			var entity="entity_"+cc;
		db[likeColtype].find().forEach(function(d){
			var entityId=d["_id"]["entityId"];
			var hascontent=d["_id"]["hascontent"];//包括评分
			var allcontent=d["_id"]["allcontent"];//一级评论数量（不包括评分）
			var score=d["_id"]["score"];// 评论总评分
			var allCommentNum=d["_id"]["allCommentNum"];//所有评论数量
			var twoLevelCommentNum=d["_id"]["twoLevelCommentNum"];//二级评论数量
			var skinTest=d["_id"]["skinTest"];//测试人数
			var score_lt3=d["_id"]["score_lt3"];//非原生评论总数量
			var score_gt3=d["_id"]["score_gt3"];//原生评论总分数
			var content_lt3=d["_id"]["content_lt3"];//非原生评论数量
			var content_gt3=d["_id"]["content_gt3"];//原生评论数量
			var v=d.value;
			if(score_lt3)
				db[entity].update({id:entityId},{$set:{"scoreLt3":NumberLong(v)}});

			if(score_gt3)
				db[entity].update({id:entityId},{$set:{"scoreGt3":NumberLong(v)}});

			if(content_lt3)
				db[entity].update({id:entityId},{$set:{"contentLt3Num":NumberLong(v)}});

			if(content_gt3)
				db[entity].update({id:entityId},{$set:{"contentGt3Num":NumberLong(v)}});

			if(hascontent)
				db[entity].update({id:entityId},{$set:{"commentContentNum":NumberLong(v)}});

			if(allcontent) 
				db[entity].update({id:entityId},{$set:{"commentNum":NumberLong(v)}});
			if(skinTest)
					db[entity].update({id:entityId},{$set:{"skinTestNum":NumberLong(v)}});
			if(score)
			db[entity].update({id:entityId},{$set:{"commentSumScore":NumberLong(v)}});

			if(allCommentNum)
			db[entity].update({id:entityId},{$set:{"allCommentNum":NumberLong(v)}});

			if(twoLevelCommentNum)
				db[entity].update({id:entityId},{$set:{"twoLevelCommentNum":NumberLong(v)}});

		});   
		db[likeColtype].remove({});
			print("评论的更新完毕");
	}
	commentAvgScort();
}

updateComment();