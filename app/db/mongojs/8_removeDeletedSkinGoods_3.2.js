//3.2之前的护肤方案中,由于用户删除方案后,方案下的产品没有删除,因此写下此js删除那部分的产品
function removeDeletedSkinGoods(){
	var total=0;
	db["user_skin_protection"].find({},{"id":1,"categoryPid":1}).forEach(function(g){
		var obj={};
		obj= db["user_goods_category"].findOne({"id":NumberLong(g.categoryPid)},{"id":1});
		if(null==obj){
			db["user_skin_protection"].update({"id":NumberLong(g.id)},{$set:{"hidden":1}});
			total++;
		}
	
	})
	print(total+"----total");
}

removeDeletedSkinGoods();