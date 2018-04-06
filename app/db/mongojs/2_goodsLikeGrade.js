	function goodsBestLike() {
		var t_like="entity_goods";
		db[t_like].find({'hidden':0,'deleted':0}).forEach(function(item) {
			var commentNum=item.commentNum?item.commentNum:0;
			var skinTestNum=item.skinTestNum?item.skinTestNum:0;
			var scoreGt3=item.scoreGt3?item.scoreGt3:0;
			var scoreLt3=item.scoreLt3?item.scoreLt3:0;
			var contentGt3Num=item.contentGt3Num?item.contentGt3Num:0;
			var contentLt3Num=item.contentLt3Num?item.contentLt3Num:0;
			
			var rnum=((item.likeNum+item.notLikeNum)/3)+commentNum;
			
			if(rnum>=6&&item.skinTestNum>=2) {
				var radio=(item.likeNum*5+item.scoreLt3*20+scoreGt3)/(item.likeNum+item.notLikeNum+contentLt3Num*20+contentGt3Num);
				var grade=Math.round(radio*2)/2;
				var om={"radio":parseFloat(radio),"grade":parseFloat(grade.toFixed(1))};
				db[t_like].update({id:item.id},{$set:om});
			} else {
				db[t_like].update({id:item.id},{$set:{"radio":0,"grade":0,"rnum":rnum}});
			}
 	 	});
	}
	goodsBestLike();