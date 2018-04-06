//v3.1精选点评排序
function type2SortUserPart() {
	db["entity_user_part_lists"].find().forEach(
			function(o) {
				var type2Sort = parseInt(((o.likeNum - o.notLikeNum) / 5)
						+ o.commentNum);
				if (!o.type2Sort || type2Sort != o.type2Sort) {
					db["entity_user_part_lists"].update({
						id : o.id
					}, {
						"$set" : {
							type2Sort : type2Sort
						}
					});
				}
			});
}
type2SortUserPart();