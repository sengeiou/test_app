	function likeGoodsSkinStatistics() {
		var outlike='entity_like2_goods_type';
		var likeskin='entity_like2_goods_skin'
		db['entity_like2_goods'].mapReduce(function(){
			if(this.type>0&&this.skin&&this.entityId>0) 
			emit({k:this.entityId+'-'+this.skin+'-'+this.type,entityId:this.entityId,type:this.type,skin:this.skin},1); 
		},function(key,values){ 
					return Array.sum(values)
		},{
			out:outlike
		});
		db[outlike].ensureIndex({'_id.k':1});
		db[likeskin].remove({});
		var ic=0;
		db[outlike].find({'_id.type':2}).forEach(function(item) {
			var itm=item._id;
			var gys=db[outlike].find({'_id.k':itm.entityId+'-'+itm.skin+'-1'})[0];
			if(gys&&gys._id) {
				var itm2=gys._id;
				var num=gys.value-item.value;
				if(num<0) num=0;
				db[likeskin].insert({id:ic++,type:1,likeNum:gys.value,notlikeNum:item.value,num:num,entityId:itm.entityId,skin:itm.skin});
			}
		});
		 db[likeskin].ensureIndex({'entityId':1});
		 db[outlike].remove({});
	}
