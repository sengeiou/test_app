//实体每天信息统计--2018-1-31以前的信息
function beforeEntityStatistics(){
    var prefix="entity_";
    var tbs=["user_part_lists"];
    //输出表
    var outCollection="day_entity_statistics";
    //2018-1-31凌晨1517328180
    //var createStamp=1517328180;
    //当前时间
    var createStamp=parseInt(new Date()/1000);
    var dayStamp=60*60*24;
    var day=60;
    for(var d=1;d<=day;d++) {
        var gt = createStamp - dayStamp * d;
        var lt = createStamp - dayStamp * (d - 1);
        for(var i=0;i<tbs.length;i++){
            var entityTable=prefix+tbs[i];
            //当天的新增数,包括被隐藏或者用户自己删除的
            var dayAddNum=db[entityTable].count({id:{$exists:true},userPartDetails:{$exists:true},createStamp:{$gt:gt,$lt:lt}});
            //到当天为止的总的心得数,包括被隐藏的
            var allNum=db[entityTable].count({id:{$exists:true},userPartDetails:{$exists:true}});
            //到当天为止的总的被隐藏的心得数
            var allHiddenNum=db[entityTable].count({id:{$exists:true},userPartDetails:{$exists:true},$or:[{hidden:1},{deleted:1}]});
            //到当天为止的总的没有被隐藏的心得数
            var allNotHiddenNum=db[entityTable].count({id:{$exists:true},userPartDetails:{$exists:true},hidden:0,deleted:0});
            db[outCollection].save({"dayAddNum":NumberLong(dayAddNum),"allNum":NumberLong(allNum),"allHiddenNum":NumberLong(allHiddenNum),"allNotHiddenNum":NumberLong(allNotHiddenNum),"tname":entityTable,"gtStamp":NumberLong(gt),"ltStamp":NumberLong(lt)})
        }
    }
}
beforeEntityStatistics();
