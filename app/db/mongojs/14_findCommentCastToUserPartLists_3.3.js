//查找文章中的产品评论和福利社的信息
function goodsComment(){
	//var array=[{findId:497,applyId:82,image:"247362_b15dd8d7-2e86-435a-a8e8-651d91fcdd2c.jpg",goodsId:[859330],title:"花皙蔻氨基酸泡泡刷 100份正装免费申请"},{findId:484,applyId:81,image:"247362_247e3562-c544-4b43-884a-8ac8e0f2a0f8.jpg",goodsId:[987812],title:"HR赫莲娜全新至美溯颜精萃露30份 免费申请"},{findId:450,applyId:80,image:"247362_2e7a1ccb-6037-47f3-8b01-1a1e285bd603.jpg",goodsId:[789365,783458,777287],title:"优质国货——溪上秘罐150份 免费申请"},{findId:443,applyId:79,image:"247362_2b1e847f-00e4-49c5-bbfe-8d12b78d85f0.jpg",goodsId:[741860],title:"HR赫莲娜全新第三代绿宝瓶城市防御精华50份免费申请"}];
	var array=[{findId:495,applyId:85,image:"247362_f6a7beef-73cb-4a88-b59c-d6cdc9081f27.png",goodsId:[987481,987488],title:"免费申请奥斯卡伴手礼|美丽修行x蔚丽莱联合定制精华/眼霜试用"}];
	for(var i=0;i<array.length;i++){
		var obj=array[i];
		var goodsIds=obj.goodsId;
		for(var j=0;j<goodsIds.length;j++){
			var id=goodsIds[j];
			var goods=db["entity_goods"].findOne({id:NumberLong(id)},{id:1,title:1});
			var commentList=db["entity_comment_goods"].find({entityId:NumberLong(id),content:{$exists:true,$regex:"试用报告"},hidden:0,$or:[{pid:0},{pid:{$exists:false}}]},{id:1,content:1,skin:1,userId:1,createStamp:1,updateStamp:1});
			for(var k=0;k<commentList.length();k++){
				var cmt=commentList[k];
				//导入临时表temp_goods_cmt
				db["temp_goods_cmt"].insert({image:obj.image,applyId:obj.applyId,findId:obj.findId,findTitle:obj.title,goodsId:id,goodsTitle:goods.title,userId:cmt.userId,content:cmt.content,cmtId:cmt.id,createStamp:cmt.createStamp,updateStamp:cmt.updateStamp,skin:cmt.skin});
			}
		}
	}
}
goodsComment();


//文章中的产品评论编辑成福利社的心得
function findCommentToUserPartList(){
	db["temp_goods_cmt"].find().forEach(function(o){
		var obj={};
		obj["_class"]="cn.bevol.model.entity.EntityUserPart";
		obj["type"]=NumberLong(2);
		obj["userId"]=NumberLong(o.userId);
		var userBaseInfo={};
		var userInfo=db["user_info"].findOne({id:NumberLong(o.userId)},{headimgurl:1,skin:1,skinResults:1,nickname:1});
		userBaseInfo["userId"]=NumberLong(o.userId);
		if(undefined!=userInfo.headimgurl && null!=userInfo.headimgurl){
			userBaseInfo["headimgurl"]=userInfo.headimgurl;
		}
		if(undefined!=userInfo.skin && null!=userInfo.skin){
			userBaseInfo["skin"]=o.skin;
		}
		if(undefined!=userInfo.skinResults && null!=userInfo.skinResults){
			userBaseInfo["skinResults"]=userInfo.skinResults;
		}
		if(undefined!=userInfo.nickname && null!=userInfo.nickname){
			userBaseInfo["nickname"]=userInfo.nickname;
		}
		obj["userBaseInfo"]=userBaseInfo;
		var userPartDetails=[];
		var detail={};
		detail["content"]=o.content;
		detail["_class"]="cn.bevol.model.entity.UserPartDetailText";
		detail["type"]=2;
		userPartDetails[0]=detail;
		obj["userPartDetails"]=userPartDetails;
		obj["pEntityId"]=NumberLong(o.applyId);
		obj["likeNum"]=NumberLong(0);
		obj["notLikeNum"]=NumberLong(0);
		obj["collectionNum"]=NumberLong(0);
		obj["commentNum"]=NumberLong(0);
		obj["hitNum"]=NumberLong(0);
		obj["hidden"]=NumberLong(0);
		obj["deleted"]=NumberLong(0);
		obj["hitNum"]=NumberLong(0);
		obj["title"]="[试用报告] "+o.goodsTitle;
		obj["updateStamp"]=NumberLong(o.updateStamp);
		obj["createStamp"]=NumberLong(o.createStamp);
		obj["image"]=o.image;
		var exFeilds={};
		exFeilds["id"]=o.cmtId;
		exFeilds["tname"]="comment_goods";
		exFeilds["entityId"]=o.goodsId;
		obj["exFeilds"]=exFeilds;
		var inc=db["entity_user_part_lists_inc"].findAndModify({update:{$inc:{id:NumberLong(1)}},$upsert:true,$new:true});
		obj["id"]=NumberLong(inc.id+1);
		print(obj.id);
		db["entity_user_part_lists"].insert(obj);
	})
}
findCommentToUserPartList();

//文章的评论复制到福利社活动中
function copyFindCommentToApplyGodos(){
	var findIds=[495];
	var applyGoodsIds=[85];
	//var findIds=[497,484,450,443];
	//var applyGoodsIds=[82,81,80,79];
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