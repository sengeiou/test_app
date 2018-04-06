//产品分类的排序
function goodsHitNumSortStatistics(){
    //查找的表(临时表)
    var entityTal="tmpl_entity_goods";
    //更新的表
    var updateTal="entity_goods";
    
    //复制表数据
    db.runCommand({
        mapreduce:"entity_goods",
        query: {hidden:0,deleted:0},
        map:function(){
                emit({id:this.id,commentNum:this.commentNum,hitNum:this.hitNum,grade:this.grade}, 1);
        },
        reduce: function (key, values) { return key},  
        out:entityTal
        }
    )
    
    db[entityTal].ensureIndex({"_id.grade":-1,"_id.hitNum":-1});
    db[entityTal].ensureIndex({"_id.grade":-1});
    db[entityTal].ensureIndex({"_id.hitNum":-1});
    //1、如果点击数大于20w，按照点击量倒叙排序;
    //条件1的数量
    var condition_1=db[entityTal].count({"_id.hitNum":{$gte:200000}});
    var total=10000000;
    //分页查询
    var start=0;
    var limit=10000;
    var maxPager=(condition_1%limit)==0?parseInt(condition_1/limit):(parseInt(condition_1/limit))+1;
    print(maxPager);
    for(var i=0;i<maxPager;i++) {
        if(i>0) {
            start=start+limit;
        }
        print("start1:"+i+"  "+start+"  limit:"+limit);
        db[entityTal].find({"_id.hitNum":{$gte:200000}}).sort({"_id.hitNum":-1}).skip(start).limit(limit).forEach(function(g)
                {
                    //条件1倒叙循环
                    total--;
                    db[updateTal].update({id:g._id.id},{$set:{csort:total}});

                }
        );
    }
     print("total"+total);
    print("end_1 total:"+total);
    //条件2的数量
    //2、如果点击数小于20w，评论数大于50的排序权重靠前，且按照用户评分倒叙排序，同时如果相同评分按照点击数倒叙排序；
    var condition_2=db[entityTal].count({ "_id.hitNum":{$lt:200000},"_id.commentNum":{$gte:50}});
    print("condition_2:"+condition_2);
    maxPager=(condition_2%limit)==0?parseInt(condition_2/limit):(parseInt(condition_2/limit))+1;
    print(maxPager);
    start=0;
    for(var i=0;i<maxPager;i++) {
        if(i>0) {
            start=start+limit;
        }
        print("start2:"+i+"  "+start+"  limit:"+limit);
        db[entityTal].find({"_id.hitNum":{$lt:200000},"_id.commentNum":{$gte:50}}).sort({"_id.grade":-1,"_id.hitNum":-1}).skip(start).limit(limit).forEach(function(g)
                {
                    total--;
                    db[updateTal].update({id:g._id.id},{$set:{csort:total}});
                    
                });
    }
     print("total"+total);
    print("end_2 total:"+total);
    //条件3的数量
    //3、如果点击数小于20w，评论数小于50的排序权重低于条件1和2，且按照用户评分倒叙排序，同时如果相同评分按照点击数倒叙排序；
    var condition_3=db[entityTal].count({"_id.hitNum":{$lt:200000},"_id.commentNum":{$lt:50}});
    print("condition_3:"+condition_3);
    var maxPager=(condition_3%limit)==0?parseInt(condition_3/limit):(parseInt(condition_3/limit))+1;
    print(maxPager);
    start=0;
    for(var i=0;i<maxPager;i++) {
        if(i>0) {
            start=start+limit;
        }
        print("start3:"+i+"  "+start+"  limit:"+limit);
        db[entityTal].find({"_id.hitNum":{$lt:200000},"_id.commentNum":{$lt:50}}).sort({"_id.grade":-1,"_id.hitNum":-1}).skip(start).limit(limit).forEach(function(g)
        {
            total--;
            db[updateTal].update({id:g._id.id},{$set:{csort:total}});
            
        });
    }
     print("total"+total);
     print("end_3 total:"+total);
    //删除索引
    db[entityTal].dropIndex({"_id.grade":-1,"_id.hitNum":-1});
    db[entityTal].dropIndex({"_id.grade":-1});
    db[entityTal].dropIndex({"_id.hitNum":-1});
}
goodsHitNumSortStatistics();