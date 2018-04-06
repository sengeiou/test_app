	
	function likeGoodsSkinStatistics() {
		var outlike="entity_like2_goods_type";
		var likeskin="entity_like2_goods_skin";
	
		db["entity_like2_goods"].mapReduce(function(){
			if(this.type>0&&this.skin&&this.entityId>0) 
			emit({k:this.entityId+"-"+this.skin+"-"+this.type,entityId:this.entityId,type:this.type,skin:this.skin},1); 
		},function(key,values){ 
					return Array.sum(values);
		},{
			out:outlike
		});
		db[outlike].ensureIndex({"_id.k":1});
			var outcoment="entity_comment_goods_type";
		db["entity_comment_goods"].mapReduce(function(){
			if((!this.pid||this.pid==0)&&(!this.hidden||this.hidden==0)&&this.skin&&this.entityId>0&&(!this.deleted||this.deleted==0)){
				emit({k:this.entityId+"-"+this.skin,entityId:this.entityId,skin:this.skin},{comment_num:1,comment_sum_score:this.score}); 
			}
		},function(key,values){
		         var vss={comment_num:0,comment_sum_score:0};
				for(var i=0;i<values.length;i++) {
					vss.comment_sum_score+=values[i].comment_sum_score;
				}
				vss.comment_num=values.length;
				vss.comment_sum_score=NumberLong(vss.comment_sum_score);
				return vss;
		},{
			out:outcoment
		});
		db[outcoment].ensureIndex({"_id.k":1});
		db[likeskin].remove({});
		db[outlike].ensureIndex({'_id.k':1});
		var ic=0;
		db[outlike].find({'_id.type':1}).forEach(function(item) {
			var itm2=item._id;
			var gys=db[outlike].find({'_id.k':itm2.entityId+'-'+itm2.skin+'-2'})[0];   ic++;
			var cskin=db[outcoment].find({'_id.k':itm2.entityId+'-'+itm2.skin})[0];   
			var goods=db["entity_goods"].find({'id':itm2.entityId})[0];   
			if(gys&&gys._id) {
				var itm=gys._id;
				//var num=item.value-gys.value;
				var isrt={id:NumberLong(ic),type:NumberInt(1),likeNum:NumberLong(item.value),notlikeNum:NumberLong(gys.value),entityId:NumberLong(itm2.entityId),skin:itm2.skin};
					if(cskin&&cskin._id) {
						isrt.commentNum=cskin.value.comment_num;
						isrt.commentSumScore=cskin.value.comment_sum_score;
						var num=(item.value*5+gys.value*0)/5+isrt.commentSumScore;
						if(num<0) num=0;
						isrt["num"]=Math.round(num);
					}
				if(goods) {
					isrt["title"]=goods.title;
					isrt["entityId"]=itm2.entityId;
				}
				db[likeskin].insert(isrt);
			} else {
				if(item.value>1) {
					var isrt={id:NumberLong(ic),type:NumberInt(1),likeNum:NumberLong(item.value),notlikeNum:NumberLong(0),num:NumberLong(item.value),entityId:NumberLong(itm2.entityId),skin:itm2.skin};
					if(cskin&&cskin._id) {
						isrt.commentNum=cskin.value.comment_num;
						isrt.commentSumScore=cskin.value.comment_sum_score;
					}
					if(goods) {
						isrt["title"]=goods.title;
						isrt["entityId"]=itm2.entityId;
					}
					db[likeskin].insert(isrt);
				}
			}
		});
		 db[likeskin].ensureIndex({'entityId':1});
		 db[likeskin].ensureIndex({'num':-1,'skin':-1});
	}
	likeGoodsSkinStatistics();

	
 