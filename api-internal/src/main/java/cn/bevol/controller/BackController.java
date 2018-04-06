package cn.bevol.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.bevol.entity.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.io97.cache.redis.RedisCacheProvider;

import cn.bevol.conf.client.ConfUtils;
import cn.bevol.model.entity.EntityApplyGoods;
import cn.bevol.model.user.UserInfo;
import cn.bevol.mybatis.model.GoodsUserSubmit;
import cn.bevol.internal.service.BackIndexRecommendService;
import cn.bevol.internal.service.BackMBannerService;
import cn.bevol.internal.service.BackGoodsService;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;


/**
 * 提供给后台的接口
 * @author hualong
 *
 */
@Controller
public class BackController extends BaseController {
	@Autowired
	private BackGoodsService backGoodsService;

	

	@Autowired
	private EntityService entityService;

	@Autowired
	private CommentService commentService;


	@Autowired
	private UserService userService;

	@Autowired
	private FeedBackService feedBackService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private FindService findService;

	@Autowired
	private BackIndexRecommendService backIndexRecommendService;

	@Autowired
	private UserPartService userPartService;


	@Autowired
	private ApplyGoodsService applyGoodsService;


	@Autowired
	private CacheService cacheService;

	@Autowired
	private AdvertisementLogClientService advertisementLogClientService;
	@Autowired
	private BaseService baseService;
	
	@Autowired
    RedisCacheProvider cacheProvider;

	@Autowired
	BackMBannerService backMBannerService;
	/**
	 * 反馈回复
	 * @param id: 反馈的id
	 * @param content: 回复内容
	 * @param redirect_type: 跳转方式1 app页面内
	 * @param redirect_page: 跳转对应的页面
	 * @param redirect_params: 跳转页面对应的参数
	 * @param new_type: 新的页面跳转参数
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/feedback/reply"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData feedbackReply(HttpServletRequest request,@RequestParam int id,@RequestParam String content,
									@RequestParam(defaultValue="-1",required=false) String redirect_type,
									//跳转的页面
									@RequestParam(defaultValue="",required=false) String redirect_page,
									@RequestParam(defaultValue="",required=false) String redirect_params,@RequestParam(required=false,name="new_type") Integer newType
	) throws Exception{
		long userId=this.getUserId(request);
		long replyUserId=ConfUtils.getResourceNum("mangeUserId");
		ReturnData rd=feedBackService.backReply(id,content,replyUserId,redirect_type,redirect_page,redirect_params,newType);
		//重新缓存user
		return rd;
	}

	@Deprecated
	@RequestMapping(value={"/back/entity/comment/reply/{entityname}"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData addHit(HttpServletRequest request,HttpServletResponse response,@RequestParam long commentid,@RequestParam String	content,@PathVariable String entityname,
							 @RequestParam(defaultValue="-1",required=false) String redirect_type,
							 //跳转的页面
							 @RequestParam(defaultValue="",required=false) String redirect_page,
							 @RequestParam(defaultValue="",required=false) String redirect_params,@RequestParam(required=false,name="new_type") Integer newType
	) throws Exception{
		long userId=this.getUserId(request);
		//总的喜欢数
		//总的收藏数
		//总的访问数
		ReturnData cmt=commentService.backReplyComment(entityname, commentid, 0, content,redirect_type,redirect_page,redirect_params,newType);

		return cmt;

	}

	/**
	 * 后台发送修修酱消息
	 * @param request
	 * @param type common 普通系统消息  mange 修修酱类似的消息
	 * @param userids:	消息接受者(用户)的id,逗号分隔
	 * @param content:	消息内容
	 * @param title:	消息标题
	 * @param redirect_type: 跳转方式1 app页面内
	 * @param redirect_page: 跳转对应的页面
	 * @param redirect_params: 跳转页面对应的参数
	 * @param new_type: 新的页面跳转参数
	 * @return
	 */
	@RequestMapping(value={"/back/xxj_send/msg"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData msg(HttpServletRequest request,@RequestParam String userids,@RequestParam(defaultValue="",required=false) String title,@RequestParam String content,
						  @RequestParam(defaultValue="-1",required=false) String redirect_type,
						  //跳转的页面
						  @RequestParam(defaultValue="",required=false) String redirect_page,
						  @RequestParam(defaultValue="",required=false) String redirect_params,@RequestParam(required=false,name="new_type") Integer newType){
		UserInfo userInfo=this.getUser(request);
		long replyUserId=ConfUtils.getResourceNum("mangeUserId");
		ReturnData cols=messageService.sendMsgByXxj(replyUserId, userids,title, content,redirect_type,redirect_page,redirect_params,newType);
		return cols;
	}

	/**
	 * 最新活动/系统消息
	 * @param type common 普通系统消息  new_active 最新活动的消息
	 * @param content:	消息内容
	 * @param title:	消息标题
	 * @param redirect_type: 跳转方式1 app页面内
	 * @param redirect_page: 跳转对应的页面
	 * @param redirect_params: 跳转页面对应的参数
	 * @return
	 */
	@RequestMapping(value={"/back/send/sysmsg"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData sysmsg(HttpServletRequest request,@RequestParam(defaultValue="sys",required=false) String type,
							 @RequestParam(defaultValue="",required=false) String title,
							 @RequestParam String content,
							 @RequestParam(defaultValue="-1",required=false) String redirect_type,
							 //跳转的页面
							 @RequestParam(defaultValue="",required=false) String redirect_page,
							 @RequestParam(defaultValue="",required=false) String redirect_params
	){
		UserInfo userInfo=this.getUser(request);
		ReturnData cols=messageService.sendSysmMessage(type,title, content,redirect_type,redirect_page,redirect_params);
		return cols;
	}

	/**
	 * 后台添加精选点评
	 * @param request
	 * @param type: 1修行说 2精选点评
	 * @param type_id:	根据type值变化,type=1:修行说id,type=2:评论的id
	 * @param publish_time:	发布时间
	 * @return
	 */
	@RequestMapping(value={"/back/essence_comment/add"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addEssenceComment(HttpServletRequest request,@RequestParam int type,@RequestParam long type_id,
										@RequestParam long publish_time){
		return findService.addEssenceComment(type, type_id, publish_time);
	}


	/**
	 * 后台评论回复
	 * 带子评论发送功能
	 * @param tname
	 *            类型:goods,composition,find,lists(清单),user_part_lists
	 * @param entityId
	 *            实体id
	 * @param pid
	 *            父评论id
	 * @param content
	 *            内容
	 * @param score
	 *            评分
	 * @param image
	 *            图片
	 * createStamp
	 * 			发送时间
	 * updateStamp
	 * 			修改时间
	 */
	@RequestMapping(value={"/back/entity/comment2/send/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData replySend(HttpServletRequest request,@RequestParam long id,@RequestParam String content,@RequestParam(defaultValue="0") int score,@RequestParam(required=false) String image,@RequestParam(required=false,defaultValue="0") Long comment_pid,@PathVariable String tname
			,@RequestParam(required=false) Long createStamp
			,@RequestParam(required=false) Long updateStamp
	) throws Exception{
			/*记录访问日志*/
		//Map mp=entityService.addHit(tname, entityid);
		//获取管理员(回复者)信息
		long replyUserId=ConfUtils.getResourceNum("mangeUserId");
		ReturnData<UserInfo> rtu= userService.getUserById(replyUserId);
		UserInfo userInfo=rtu.TResult();
		ReturnData rd2=commentService.replySend(tname, id,comment_pid, userInfo, content, score,image,false);
		if(rd2.getResult()!=null){
			commentService.replyTpag(id,tname);
		}
		return rd2;
	}

	/**
	 * v3.1
	 * 对实体的操作
	 * 暂时只支持评论的操作
	 * @param state: 实体的属性,是否可以评论/喜欢等
	 * @param val: 1不允许
	 * @param id: 实体id
	 */
	@RequestMapping(value={"/back/entity/change/{state}/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData allow(HttpServletRequest request,@PathVariable String  state,@PathVariable String  tname,@RequestParam long  id,@RequestParam int val){
		return entityService.entityChangeState(tname,state,id,val);
	}

	/**
	 * 批量禁止产品被评论
	 * @param ids: 实体id,逗号分隔
	 * @param state: 实体的属性,是否可以评论/喜欢等
	 * @param val: 1不允许
	 */
	@RequestMapping(value={"/back/entity/batch_change/{state}/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData allow(HttpServletRequest request,@PathVariable String  state,@PathVariable String  tname,@RequestParam String  ids,@RequestParam int val){
		if(StringUtils.isNotBlank(ids)) {
			String idss[]=ids.split(",");
			for(int i=0;i<idss.length;i++) {
				entityService.entityChangeState(tname,state,Long.parseLong(idss[i]),val);
			}
		}
		return ReturnData.SUCCESS;
	}


	/**
	 * 心得根据id进行排序
	 * @param ids: 心得id,逗号分隔
	 */
	@RequestMapping(value={"/back/sort/user_part/lists"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData editFind(HttpServletRequest request,@RequestParam String ids){
		return userPartService.editSort(ids);
	}

	/**
	 * 清除心得排序
	 */
	@RequestMapping(value={"/back/clear/sort/user_part/lists"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData clearFind(HttpServletRequest request){
		return userPartService.clearSort();
	}

	/**
	 * 心得重置24小时排序规则
	 */
	@RequestMapping(value={"/back/reset_day/sort/user_part/lists"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData resetUserPartDay(HttpServletRequest request){
		return userPartService.resetDaySort();
	}

	/**
	 * 福利社设置用户中奖,发送消息
	 * @param request
	 * @param id: 活动id
	 * @param user_ids: 中奖用户id,逗号分隔
	 * @param title: 信息标题
	 * @param redirect_type: 跳转方式1 app页面内
	 * @param new_type: 新的页面跳转参数
	 * @param msgContent: 发送的消息内容
	 * @return
	 */
	@RequestMapping(value={"/back/apply_goods/used/setings"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData setApplyGoods(HttpServletRequest request,@RequestParam long id,@RequestParam String user_ids
			,@RequestParam(defaultValue="",required=false) String title
			,@RequestParam(name="msg_content")String msgContent,@RequestParam(name="redirect_type") String redirectType,@RequestParam(required=false,name="new_type") Integer newType
	){
		return applyGoodsService.usedGoodsByUserIds(id, user_ids,title,msgContent,redirectType,newType);
	}

	/**
	 * 添加福利社文章/活动
	 title:		文章标题
	 image:		文章图片
	 tag:		文章标签
	 descp:		活动描述
	 goodsIds:	活动提供的产品
	 startTime:	活动开始时间
	 lastTime:	活动结束时间
	 doyenScore:	参与活动所需的修行值
	 goodsNum:	活动提供的产品数量
	 type:		文章类型 0普通、1长久性、2实效性
	 */
	@RequestMapping(value={"/back/apply_goods/add"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addApplyGoods(HttpServletRequest request, EntityApplyGoods entityApplyGoods) throws Exception {
		return applyGoodsService.addEntityApplyGoods(entityApplyGoods);
	}

	/**
	 * 后台添加试用报告
	 * @param request
	 * @param tname: lists
	 * @param exFeilds: 迁移的来源(comment_find/goods)
	 * @param type: 心得的类型(默认试用),前端不传
	 * @param title: 心得标题
	 * @param pEntityId: 实体id
	 * @param userId: 发试用的用户id
	 * @param details: 试用内容
	 * @param createStamp: 试用发布发布时间
	 * @param updateStamp: 试用修改时间
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/user_apply/add/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addUserApply(HttpServletRequest request,
								   @PathVariable String tname,
								   @RequestParam(defaultValue="",required=false) String exFeilds,
								   @RequestParam(defaultValue="2",required=false) Integer type,
								   @RequestParam(defaultValue="",required=false) String title,
								   @RequestParam Long userId,
								   @RequestParam(required=false) String details,
								   @RequestParam(required=false) Long createStamp,
								   @RequestParam(required=false) Long updateStamp,
								   @RequestParam(required=false) String image
	) throws Exception {
		return userPartService.addUserApply(userId,type,title,tname,exFeilds,details,createStamp,updateStamp,image);
	}

	/**
	 * 修改福利社试用报告
	 * @param request
	 * @param id: 试用心得的id
	 * @param hidden: 1隐藏 0非隐藏
	 * @param tname: lists
	 * @param srcTname: 迁移的来源(comment_find/goods)
	 * @param title: 心得标题
	 * @param userId: 发试用的用户id
	 * @param details: 试用内容
	 * @param createStamp: 试用发布发布时间
	 * @param updateStamp: 试用修改时间
	 */
	@RequestMapping(value={"/back/user_apply/update"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addUserApply(HttpServletRequest request,
								   @RequestParam(required=false) Integer hidden,
								   @RequestParam(required=false) Long id,
								   @RequestParam(defaultValue="comment_find") String srcTname,
								   @RequestParam(defaultValue="",required=false) String title,
								   @RequestParam(required=false) Long userId,
								   @RequestParam(required=false) String details,
								   @RequestParam(required=false) Long createStamp,
								   @RequestParam(required=false) Long updateStamp
	) throws Exception {
		return userPartService.updateUserApply(id,hidden,userId,title,srcTname,details,createStamp,updateStamp);
	}

	/**
	 * 修改单个福利社活动内容
	 *
	 title:		文章标题
	 image:		文章图片
	 tag:		文章标签
	 descp:		活动描述
	 goodsIds:	活动提供的产品
	 startTime:	活动开始时间
	 lastTime:	活动结束时间
	 doyenScore:	参与活动所需的修行值
	 goodsNum:	活动提供的产品数量
	 type:		文章类型 0普通、1长久性、2实效性
	 */
	@RequestMapping(value={"/back/apply_goods/update"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData updateApplyGoods(HttpServletRequest request, EntityApplyGoods entityApplyGoods) throws Exception {
		return applyGoodsService.updateEntityApplyGoods(entityApplyGoods);
	}

	/**
	 * todo,hidden
	 * 后台福利社全部活动列表
	 *
	 * @param request
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/apply_goods/list"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData	listApplyGoods(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
		return applyGoodsService.list(null,pager, pageSize);
	}


	/**
	 * 福利社某个活动的所有试用/心得
	 * @param entity_id: 活动id
	 * @param type: 1普通心得 2福利社心得 3自由发布心得
	 * @param request
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/user_part/list2/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData	applyUserPartLists(HttpServletRequest request, @PathVariable String tname,@RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(name="entity_id") long pEntityId,@RequestParam int type) throws Exception {
		return new ReturnListData(userPartService.findUserPartListByApply(tname,type,pEntityId,pager,pageSize), userPartService.findUserPartCount2(tname,type,pEntityId));
		//return userPartService.findUserPartListByApply(tname,entityId,pager, pageSize);
	}


	/**
	 * 获取人工排序的心得
	 * @param entity_id: 活动id 没有查询所有
	 * @param type: 1普通心得 2福利社心得 3自由发布心得
	 * @param request
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/user_part/list2/goods_sort"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData	goodsSort(HttpServletRequest request, @PathVariable String tname,@RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(name="entity_id") long pEntityId,@RequestParam int type) throws Exception {
		return new ReturnListData(userPartService.findUserPartListByApply(tname,type,pEntityId,pager,pageSize), userPartService.findUserPartCount2(tname,type,pEntityId));
		//return userPartService.findUserPartListByApply(tname,entityId,pager, pageSize);
	}

	/**
	 * 编辑心得排序
	 * @param id: 心得id
	 * @param sortField:排序字段
	 * @param sort 序列
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/back/user_part/edit/goods_sort", method = RequestMethod.POST)
	@ResponseBody
	public ReturnData userPartListSort(@RequestParam Integer id,
									   @RequestParam Integer sort,
									   @RequestParam String sortField){
		return userPartService.userPartListSort(id, sort, sortField);
	}


	/**
	 * 根据state,福利社参与申请的用户信息
	 * @param request
	 * @param id: 申请的活动id
	 * @param state:* 1参与中 ----活动正在进行中
	 * 4参与中		活动已结束
	 * 2中奖了 没发过该活动的心得
	 * 3中奖了 发过该活动的心得
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/apply_goods/userlist"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData	userlist(HttpServletRequest request, @RequestParam int id, @RequestParam(defaultValue = "0") int state, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
		return applyGoodsService.findApplyUserList(id,state,pager, pageSize);
	}

	/**
	 * 某个话题的心得列表
	 * @param request
	 * @param type: 0是所有心得 1普通心得 2试用心得 3自由发布的心得
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/user_part/lists"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData userPartLists(HttpServletRequest request,@RequestParam(defaultValue = "0") int type,@RequestParam(defaultValue = "0") int pager,@RequestParam(defaultValue = "10") int pageSize) throws Exception {
		return userPartService.userPartLists(type,pager,pageSize);
	}


	/**
	 * 用于后台展示
	 * rides缓存的key列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/redis/cache/list"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addRedisCache(HttpServletRequest request,@RequestParam(defaultValue="0") int pager,@RequestParam(defaultValue="10") int pageSize) throws Exception {
		return cacheService.redisCacheList(pager,pageSize);
	}


	/**
	 * todo 去除多余参数
	 * 清除单个和列表的缓存
	 * @param key: 要清除的key
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/redis/cache/clean"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addRedisCache(HttpServletRequest request,@RequestParam(name="redis_cache",required=false) String key,@RequestParam(name="entity_id",required=false) Long entityId,@RequestParam(name="entity_type",required=false) String entityType
			,@RequestParam(name="comment_mainId",required=false) Long commentId,@RequestParam(name="cache_type",required=false) Integer cacheType) throws Exception {
		return cacheService.cleanRedisCacheList(key);
	}

	/**
	 * init版本号++
	 * init接口内容有变更后,调用该接口
	 * 前端接口判断是否调用init接口
	 */
	@RequestMapping(value={"/back/init_v/update"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData initV() throws Exception {
		//init版本号++
		ReturnData rd=entityService.colCofValue();
		return rd;
	}


	/**
	 * 广告统计
	 * @param ad_id: 广告id
	 * @param positionType: 1app 2移动 3pc
	 */
	@RequestMapping(value={"/back/AD/log"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addRedisCache(HttpServletRequest request,@RequestParam(name="ad_id") String adId,@RequestParam(required=false) String positionType) throws Exception {
		return advertisementLogClientService.adLog(adId,positionType);
	}

	/**
	 * 已经上传的图片 通过图片名和类型拼接完整路径
	 * @param request
	 * @param type: 1评论 2举报/纠错/反馈 3心得 4用户上传的产品临时图片
	 * @param image: 图片名
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/upload/imageUrl"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addRedisCache(HttpServletRequest request,@RequestParam Integer type,@RequestParam String image) throws Exception {
		return entityService.getImageUrl(type,image);
	}


	/**
	 * 唯一id生成
	 * 用于搜索合并(find,lists等)到opensearch,生成id
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/back/unique/id"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData uniqueId(HttpServletRequest request) throws Exception{
		Map m=new HashMap();
		m.put("id", baseService.getUniqueId());
		return new ReturnData(m);
	}

	
		/**
		 * 分页缓存首页瀑布流到mongo
		 * @param request
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/mongo/recommend"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData recommendToMongo(HttpServletRequest request
				,@RequestParam(defaultValue="20",required=false) Integer pager
				,@RequestParam(defaultValue="0",required=false) Long searchTime
				,@RequestParam(defaultValue="1",required=false) Integer state
				) throws Exception{
			return backIndexRecommendService.recommendToMongo(pager,searchTime,state);
		}
		/**
		 * 瀑布流发送200
		 * @return
		 * @throws Exception
		 */
		
		@RequestMapping(value={"/back/recommend/create"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData sendRecommend(HttpServletRequest request
				,@RequestParam(defaultValue="20",required=false) Long searchTime
				) throws Exception{
			return backIndexRecommendService.sendRecommend(searchTime);
		}
		/**
		 * 根据id获得实体
		 * @param request
		 * @param tname
		 * @param id
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/recommend/{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData getEntityById(HttpServletRequest request
				,@PathVariable String tname
				,@RequestParam(defaultValue="0",required=false) Long id
				) throws Exception{
			return backIndexRecommendService.getEntity(tname,id);
		}
		/**
		 * 保存更新推荐实体
		 * @param request
		 * @param createTime
		 * @param content
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/recommend/save_update"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData saveEntity(HttpServletRequest request
				,@RequestParam(defaultValue="0",required=false) Long createStamp
				,@RequestParam(defaultValue="0",required=false) String content
				,@RequestParam(defaultValue="0",required=false) String pageName
				) throws Exception{
			return backIndexRecommendService.saveEntity(createStamp,content,pageName);
		}
		/**
		 * 发布
		 * @param request
		 * @param createTime
		 * @param state
		 * @param pulishTime
		 * @param pagerName
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/recommend/publish"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData publish(HttpServletRequest request
				,@RequestParam(defaultValue="0",required=false) Long createStamp
				,@RequestParam(defaultValue="0",required=false) int state
				,@RequestParam(defaultValue="0",required=false) Long publishStamp
				) throws Exception{
			return backIndexRecommendService.publishEntity(createStamp,state,publishStamp);
		}
		
		/**
		 * 推荐列表
		 * @param request
		 * @param pager
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/recommend/list"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData dataList(HttpServletRequest request
				,@RequestParam(defaultValue="0",required=false) int pager
				,@RequestParam(defaultValue="0",required=false) String pageName
				) throws Exception{
			return backIndexRecommendService.findRecommendList(pager,pageName);
		}
		/**
		 * 获取单条推荐
		 * @param request
		 * @param createTime
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"back/recommend/one"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData getOne(HttpServletRequest request
				,@RequestParam(defaultValue="0",required=false) Long createStamp
				) throws Exception{
			return backIndexRecommendService.findOneRecommend(createStamp);
		}


		/**
		 * 首页瀑布流同步阅读数
		 * @param request
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/recommend/update_recommend"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData getOne(HttpServletRequest request
				) throws Exception{
			return backIndexRecommendService.updateRecommend();
		}
		
		
		/**
		 * 后台批量隐藏试用报告
		 * @param request
		 * @param hidden
		 * @param ids
		 * @return
		 * @throws Exception
		 */
		/*@RequestMapping(value={"/back/user_apply/hidden"},method={RequestMethod.POST})
	    @ResponseBody
	    public ReturnData addUserApply(HttpServletRequest request,
	    		@RequestParam(required=false) Integer hidden,
				@RequestParam(required=false) String ids
				) throws Exception {
	        return userPartService.hiddenUserApply(ids,hidden);
	    }*/

	/**
	 * 心得反垃圾数据接口
	 * @return
	 */
	@RequestMapping(value = "/user_part/anti/spam")
	@ResponseBody
	public ReturnData antiSpamController(){
		try {
			return userPartService.userPartAntiSpam();
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnData.ERROR;
		}
	}

	/**
	 * 评论反垃圾数据接口
	 * @return
	 */
	@RequestMapping(value = "/comment/spam/{tname}")
	@ResponseBody
	public ReturnData antiSpamController(@PathVariable String tname){
		try {
			return commentService.commentAntiSpam(tname);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnData.ERROR;
		}
	}
	

	/**
	 * 小程序后台设置添加banner
	 * @param request
	 * @param List<map>: banner信息列表
	 */
	@RequestMapping(value={"/back/addOrUpdate/mBanner1"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addOrUpdateMBanner1(HttpServletRequest request,@RequestBody List<Map> bannerList ){
		return backMBannerService.addOrUpdateMBanner(bannerList);
	}

}
