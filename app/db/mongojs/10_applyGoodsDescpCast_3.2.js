//护肤方案中的产品添加过期时间字段
function addExpireTimeToSkinFlow(){
	db["user_skin_protection"].find({"open":1},{"id":1,"open":1,"openTime":1,"releaseDate":1}).forEach(function(s){
		var expireTime=s.openTime+s.releaseDate*30*24*60*60;
		db["user_skin_protection"].update({"id":NumberLong(s.id)},{$set:{"expireTime":NumberLong(expireTime)}});
	})
}
addExpireTimeToSkinFlow();