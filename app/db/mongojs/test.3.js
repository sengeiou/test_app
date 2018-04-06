function commentEntityStatistics(){
    var tbs=["goods","composition","find","user_part_lists","lists"];
    //输出表
    var actionStatistics="action_entity_statistics";
    //1:一级评论 2:二级
    var commentType=[1,2];
    //1:有效 2:无效
    var efficientType=[1,2];
    //程序开始运行的时间
    //var createStamp=parseInt(new Date()/1000);
    //2018-1-31 23点 1517414280
    var createStamp=1517414280;
    //统计day天前到现在每天的数据 一次性程序
    var day=90;
    for(var d=1;d<=day;d++){
        var gt=createStamp-(60*60*24)*d;
        var lt=createStamp-(60*60*24)*(d-1);
        if(day==d){
            print("-----min----"+lt);
        }
        //print("gt"+d+":"+gt+"----lt"+d+":"+lt);
        //一级评论有效总数
        var efficientTotal_1=0;
        //二级评论有效总数
        var efficientTotal_2=0;
        //一级评论无效总数
        var notEefficientTotal_1=0;
        //二级评论无效总数
        var notEefficientTotal_2=0;
        //点赞
        var likeNumTotal_1=0;
        var likeNumTotal_2=0;
        //收藏总数
        var collectionTotal=0;
        for(var i=0;i<tbs.length;i++){
            //字段集合
            var iteam={};
            //评论
            var entityName="entity_comment_"+tbs[i];
            //收藏实体
            var collectionName="entity_collection_"+tbs[i];
            //喜欢实体
            var likeName="entity_like2_"+tbs[i];
            //评论数
            var efficientCount=0;
            //聚合变量
            var aggNum=[];
            //实体点赞数
            var entityLikeNum=0;
            //实体收藏数
            var collectionNum=0;
            iteam["tname"]=tbs[i];
            for(var j=0;j<commentType.length;j++){
                if(commentType[j]==1){
                    for(var k=0;k<efficientType.length;k++){
                        //评论是否有效
                        iteam["efficient"]=efficientType[k];
                        //评论一级/其它
                        iteam["comment"]=commentType[j];
                        if(efficientType[k]==1){
                            //有效一级
                            //评论
                            efficientCount=db[entityName].count({"content":{$exists:true},"hidden":0,"deleted":0,"createStamp":{$gt:gt,$lt:lt},$or:[{"pid":0},{"pid":{$exists:false}}]});
                            iteam["comment_count"]=efficientCount;
                            //点赞
                            aggNum=db[entityName].aggregate([{ $match : { $or:[{"pid":0},{"pid":{$exists:false}}],"hidden":0,"deleted":0,"content":{$exists:true},"createStamp":{$gt:gt,$lt:lt} } },{$group : {_id : null, total : {$sum : "$likeNum"}}}])
                            var likeNum=0;
                            iteam["comment_likeNum"] = likeNum;
                            if(null!=aggNum) {
                                var ba = aggNum["_batch"];
                                if (ba.length > 0) {
                                    likeNum = ba[0].total;
                                    iteam["comment_likeNum"] = likeNum;
                                }
                            }
                            iteam["createStamp"]=lt;
                            db[actionStatistics].insert(iteam);
                            //总数++
                            efficientTotal_1+=efficientCount;
                            likeNumTotal_1+=likeNum;
                        }
                        if(efficientType[k]==2){
                            //无效一级
                            efficientCount=db[entityName].count({"createStamp":{$gt:gt,$lt:lt},"$and":[{$or:[{"hidden":1},{"deleted":1},{"score":{$exists:false}}]},{$or:[{"pid":0},{"pid":{$exists:false}}]}]});
                            iteam["comment_count"]=efficientCount;
                            iteam["createStamp"]=lt;
                            iteam["comment_likeNum"] = 0;
                            db[actionStatistics].insert(iteam);
                            notEefficientTotal_1+=efficientCount;
                        }
                    }
                }else if(commentType[j]==2){
                    for(var k=0;k<efficientType.length;k++){
                        iteam["efficient"]=efficientType[k];
                        iteam["comment"]=commentType[j];
                        if(efficientType[k]==1){
                            //有效二级
                            efficientCount=db[entityName].count({"pid":{$gt:0},"content":{$exists:true},"hidden":0,"deleted":0,"createStamp":{$gt:gt,$lt:lt}});
                            iteam["comment_count"]=efficientCount;
                            aggNum=db[entityName].aggregate([{ $match : {"pid":{$gt:0},"content":{$exists:true},"createStamp":{$gt:gt,$lt:lt},"hidden":0,"deleted":0 } },{$group : {_id : null, total : {$sum : "$likeNum"}}}])
                            var likeNum=0;
                            iteam["comment_likeNum"] = likeNum;
                            if(null!=aggNum) {
                                var ba = aggNum["_batch"];
                                if (ba.length > 0) {
                                    likeNum = ba[0].total;
                                    iteam["comment_likeNum"] = likeNum;
                                }
                            }
                            iteam["createStamp"]=lt;
                            db[actionStatistics].insert(iteam);
                            efficientTotal_2+=efficientCount;
                            likeNumTotal_2+=likeNum;
                        }
                        if(efficientType[k]==2){
                            //无效二级
                            efficientCount=db[entityName].count({"pid":{$gt:0},"createStamp":{$gt:gt,$lt:lt},$or:[{hidden:1},{deleted:1},{"content":{$exists:false}}]});
                            iteam["comment_count"]=efficientCount;
                            iteam["createStamp"]=lt;
                            iteam["comment_likeNum"] = 0;
                            db[actionStatistics].insert(iteam);
                            notEefficientTotal_2+=efficientCount;
                        }
                    }
                }
            }
            iteam=null;
            iteam={};
            iteam["tname"]=tbs[i];
            //收藏
            collectionNum=db[collectionName].count({"type":1,"hidden":0,"deleted":0,"createStamp":{$gt:gt,$lt:lt}});
            //喜欢
            entityLikeNum=db[likeName].count({"type":1,"hidden":0,"deleted":0,"createStamp":{$gt:gt,$lt:lt}});
            collectionNum=entityLikeNum+collectionNum;
            //收藏数(喜欢+收藏)
            iteam["collectionNum"]=collectionNum;
            iteam["createStamp"]=lt;
            db[actionStatistics].insert(iteam);
            collectionTotal+=collectionNum;
        }
        iteam=null;
        iteam={};
        iteam["total"]="total";
        iteam["collectionTotal"]=collectionTotal;

        iteam["efficientTotal_2"]=efficientTotal_2;
        iteam["notEefficientTotal_2"]=notEefficientTotal_2;
        iteam["efficientTotal_1"]=efficientTotal_1;
        iteam["notEefficientTotal_1"]=notEefficientTotal_1;

        iteam["likeNumTotal_1"]=likeNumTotal_1;
        iteam["likeNumTotal_2"]=likeNumTotal_2;
        iteam["createStamp"]=lt;
        db[actionStatistics].insert(iteam);
    }
}
commentEntityStatistics();