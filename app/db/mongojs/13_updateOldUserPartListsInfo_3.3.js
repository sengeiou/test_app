//旧的心得中的产品信息添加字段
function oldUserPartListUpdate(){
	db["entity_user_part_lists"].find({hidden:0,userPartDetails:{$exists:true}}).forEach(function(o){
		var details=o.userPartDetails;
		if(details.length>0){
			for(var i=0;i<details.length;i++){
				var detail=details[i];
				if(detail.type==1 && null!=detail._id){
					//print(o.id);
					//查找实体信息
					var goods=db["entity_goods"].findOne({id:NumberLong(detail._id)},{capacity:1,safety_1_num:1,price:1,grade:1,commentNum:1})
					if(null!=goods){
						if(undefined!=goods.capacity && null!=goods.capacity){
							detail["capacity"]=goods.capacity;
						}
						if(undefined!=goods.safety_1_num && null!=goods.safety_1_num){
							detail["safety_1_num"]=goods.safety_1_num;
						}
						if(undefined!=goods.price && null!=goods.price){
							detail["price"]=goods.price;
						}
						if(undefined!=goods.grade && null!=goods.grade){
							detail["grade"]=goods.grade;
						}
						if(undefined!=goods.commentNum && null!=goods.commentNum){
							detail["commentNum"]=goods.commentNum;
						}
					}
				}
				details[i]=detail;
			}
			db["entity_user_part_lists"].update({id:NumberLong(o.id)},{$set:{"userPartDetails":details}})
		}
	})
}
oldUserPartListUpdate();