	function commentColStatistics() {
		//计算得分情况
		var outlike='entity_comment_goods_col';
		db['entity_comment_goods'].mapReduce(function(){
		if((!this.pid||this.pid==0)&&this.score!=3&&this.score>0&&this.hidden==0&&this.content)  {
				var mls={"scoreLikeNum":{"gte":4,"lte":5},"scoreNotLikeNum":{"gte":1,"lte":2}}
				var ojb={"scoreLikeNum":0,"scoreNotLikeNum":0,"scoreLikeNumAdd":0,"scoreNotLikeNumAdd":0};
				for(var key in mls){
					var gj=mls[key];
					if(gj["gte"]<=this.score&&gj["lte"]>=this.score) {
						ojb[key]=ojb[key]+1;
						ojb[key+"Add"]=ojb[key+"Add"]+this.score;
					}
				}

				emit(this.entityId,ojb); 
			}
		},function(k,values){ 
				var ojb={"scoreLikeNum":0,"scoreNotLikeNum":0,"scoreLikeNumAdd":0,"scoreNotLikeNumAdd":0};
				for(var i=0;i<values.length;i++) {
					var v=values[i];
					for(var key in ojb){
						ojb[key]=ojb[key]+v[key];
					}
				}
			return ojb;
		},{
			out:outlike
		});
		
		//添加喜欢数
		db[outlike].find().forEach(function(o) {
			var so={scoreLikeNum:NumberInt(o.value.scoreLikeNum),scoreNotLikeNum:NumberInt(o.value.scoreNotLikeNum)};
			db["entity_goods"].update({"id":o._id},{"$set":so})
		});
  	}
	commentColStatistics();

	function goodsBestLike() {
		var mls={1:{"lte":-0.161},
		1.5:{"gt":-0.161,"lte":0.151},
		2:{"gt":0.151,"lte":0.424},2.5:{"gt":0.424,"lte":0.619},3:{"gt":0.619,"lte":0.775},3.5:{"gt":0.775,"lte":0.883},4:{"gt":0.883,"lte":0.937},4:{"gt":0.937,"lte":0.955},5:{"gt":0.955}}
		var t_like="entity_goods";
		db[t_like].find({'likeNum':{"$gt":0}}).forEach(function(item) {
			   var scoreLikeNum=item.scoreLikeNum?item.scoreLikeNum:0;
			   	item.likeNum=item.likeNum+scoreLikeNum;
				var scoreNotLikeNum=item.scoreNotLikeNum?item.scoreNotLikeNum:0;
				item.notLikeNum=item.notLikeNum+scoreNotLikeNum;
				var ratio=(item.likeNum-3*item.notLikeNum)/(item.likeNum+item.notLikeNum);
				var grade=0;
				for(var key in mls) {
					var rval=mls[key];
					if(!rval["gt"]&&rval["lte"]&&ratio<=rval["lte"]) {
						grade=key;
						break;
					}  else if(!rval["lte"]&&rval["gt"]&&ratio>rval["gt"]) {
						grade=key;
						break;
					} else if(rval["lte"]&&rval["gt"]&&ratio>rval["gt"]&&ratio<=rval["lte"]) {
						grade=key;;
						break;
					}
				}
				db[t_like].update({id:item.id},{$set:{"ratio":parseFloat(ratio.toFixed(3)),"grade":parseFloat(grade)}});
	 	});
	}
	goodsBestLike();
