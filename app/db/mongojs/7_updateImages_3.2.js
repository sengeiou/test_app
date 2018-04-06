function updateImages(){
	var tables=["goods","composition","find","lists","user_part_lists"];
	var commentStr="entity_comment_";
	for(var i=0;i<tables.length;i++){
		var table=commentStr+tables[i];
		var total=db[table].count({"image":{$exists:true,$ne:""},"images":{$exists:false}});
		print(table+":"+total);
		
		db[table].find({"image":{$exists:true,$ne:""},"images":{$exists:false}}).forEach(function(o){
			var images=[];
			if(o.image.length!=""){
				images[0]=o.image;
			}
			
			db[table].update({id:o.id},{$set:{"images":images}});
		});

	}
}
updateImages();