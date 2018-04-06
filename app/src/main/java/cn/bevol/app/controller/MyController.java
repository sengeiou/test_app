package cn.bevol.app.controller;

import cn.bevol.app.service.*;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.user.UserLocation;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.IPUtils;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MyController extends BaseController {
	
	@Resource
	private FeedBackService feedBackService;
	
	
	@Resource
	private UserService userService;
	@Resource
	private MyService myService;

	@Resource
	private EntityService entityService;
	@Resource
	private CommentService commentService;

	@Resource
	MessageService messageService;
	
	@Resource
	AliyunService aliyunService;
	@Resource
	private UploadGoodsService uploadGoodsService;
	
	@Resource
	private SkinProtectionService skinProtectionService;
	@Resource
	private SkinFlowService skinFlowService;

	@Resource
	private UserPartService userPartService;

    @Autowired
    private ApplyGoodsService applyGoodsService;

    
    @Autowired
    private ApplyGoods2Service applyGoods2Service;

	/*******2.0********/
	
	
	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value={"/userinfo"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData userinfo(HttpServletRequest request,@RequestParam String id){
		ReturnData user= userService.findUserinfoByIds(id);
		return user;
	} 

 	/**
 	 * 注册后第一次登录调用
	 * 用户初始化信息
	*/
	@RequestMapping(value={"/auth/my/init"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> myinit(HttpServletRequest request){
		UserInfo userInfo=this.getUser(request);
		UserInfo user= userService.userInitMsg(userInfo.getId());
		return returnAjax(user, 0);
	} 
 	
 	/**
 	 * 我的评论列表
 	 * @param request
 	 * @param tname: goods/composition/user_part_lists/find/lists
 	 * @param startId: 第一页为0,大于0时当前列表评论的最小id
 	 * @param pageSize: 页码
 	 * @return
 	 */
	@RequestMapping(value={"/auth/my/comment/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> mycomment(HttpServletRequest request, @PathVariable String tname, @RequestParam(defaultValue="0") long startId
			, @RequestParam(defaultValue="10") int pageSize){
		long userId=this.getUserId(request);
		Map cols=commentService.findCommentByUserId(tname,userId,startId,pageSize);
		if(cols==null) {
			return errorAjax();
		}else {
			return cols;
		}
	}
	
	
 	
 	/**
 	 * 修改评论
 	 * @param request
 	 * @param tname: goods/composition/user_part_lists/find/lists
 	 * @param commentId: 评论的id
 	 * @param content: 新的评论内容
 	 * @param image: 新的评论图片
 	 * @return
 	 */
	@RequestMapping(value={"/auth/my/comment/update/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData ucommentz(HttpServletRequest request, @PathVariable String tname, @RequestParam long commentId, @RequestParam String content, @RequestParam(required=false) String image, @RequestParam(required=false) String images){
		//我的收藏
		long userId=this.getUserId(request);
		String[] imagess=null;
		if(null!=images){
			imagess=new String[]{};
		}
		
		if(StringUtils.isNotBlank(images)){
				//完整路径处理,保存图片名
			images= CommonUtils.getImages(images);
			imagess=images.split(",");
			image=imagess[0];
		}
		return commentService.updateComment(tname,userId,commentId,content,image,imagess,null);
 	}
	
	
	/**
	 * v3.2
 	 * 修改评论
 	 * 新增特性:带有实名认证,三张图片
 	 * @param request
 	 * @param tname: goods/composition/user_part_lists/find/lists
 	 * @param commentId: 评论的id
 	 * @param content: 新的评论内容
 	 * @param image: 新的评论图片
 	 * @return
 	 */
	@RequestMapping(value={"/auth/my/comment/update2/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData updateComment2(HttpServletRequest request, @PathVariable String tname, @RequestParam long commentId, @RequestParam String content, @RequestParam(required=false) String image, @RequestParam(required=false) String images){
		//我的收藏
		long userId=this.getUserId(request);
		String[] imagess=null;
		if(null!=images){
			imagess=new String[]{};
		}
		
		if(StringUtils.isNotBlank(images)){
				//完整路径处理,保存图片名
			images=CommonUtils.getImages(images);
			imagess=images.split(",");
			image=imagess[0];
		}
		return commentService.updateComment2(tname,userId,commentId,content,image,imagess,null);
 	}
	/**
	 * 删除用户评论(hidden=2)
	 * @param request
	 * @param tname
	 * @param commentId
	 * @return
	 */
	@RequestMapping(value={"/auth/my/comment/delete/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData hiddenComment(HttpServletRequest request, @PathVariable String tname, @RequestParam long commentId){
		//我的收藏
		long userId=this.getUserId(request);
		return commentService.hiddenComment(tname,userId,commentId);
 	}
	
 	/**
	 * 收藏列表
	 *tname goods 产品 composition成分 goods
	 */
	@Deprecated
	@RequestMapping(value={"/auth/my/collection/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> collectionz(HttpServletRequest request, @PathVariable String tname, @RequestParam(defaultValue="0") long startId, @RequestParam(defaultValue="10") int pageSize){
		//我的收藏
		long userId=this.getUserId(request);
		Map cols=entityService.findCollectionByUserId(tname,userId,startId,pageSize);
		if(cols==null) {
			return errorAjax();
		}else {
			return cols;
		}
	}
	
 	/**
 	 * v3.1
 	 * 我的喜欢,3.0之后为我的收藏
 	 * @param request
 	 * @param tname: 实体名称
 	 * @param type: 类型 1喜欢  2不喜欢 0取消/没有点击过
 	 * @param startId: 第一页为0,大于0时当前列表喜欢/心碎的最小id
 	 * @param pageSize 分页最大值 
 	 * @return
 	 */
	@RequestMapping(value={"/auth/my/like/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> like(HttpServletRequest request, @PathVariable String tname, @RequestParam(defaultValue="1") Integer type, @RequestParam(defaultValue="0") long startId, @RequestParam(defaultValue="10") int pageSize){
		//我的收藏
		long userId=this.getUserId(request);
		Map cols=entityService.findLikeByUserId(tname,type,userId,startId,pageSize);
		if(cols==null) {
			return errorAjax();
		}else {
			return cols;
		}
	}




 	/**
	 * 我的消息-最新活动(一条信息)
	 */
	@RequestMapping(value={"/auth/my/new_actvie"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData newActvie(HttpServletRequest request,@RequestParam(defaultValue="0") long startId
			,@RequestParam(defaultValue="10") int pageSize){
		long userId=this.getUserId(request);
		ReturnData cols=messageService.newActive(userId);
		return cols;
 	}
	
	/**
	 * v2.9之前
	 * 我的信息列表 为兼容老版本
	 */
	@RequestMapping(value={"/auth/my/message"},method={RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public ReturnListData msg(HttpServletRequest request, @RequestParam(defaultValue="1") int type, @RequestParam(defaultValue="0") long startId, @RequestParam(defaultValue="10")  int pageSize){
		UserInfo userInfo=this.getUser(request);
		ReturnListData map=messageService.getMymsg(userInfo,startId,pageSize);
		if(map==null) {
			return map;
		}
		return map;
	}
	
	/**
	 *  我的信息列表
	 *  进入单个消息列表
	 * @param request
	 * @param type
 				1    系统消息
				2、评论消息
				3、修修酱消息
				4、表示点赞
	 * @param startId: 第一页为0,大于0时当前列表消息的最小id
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value={"/auth/my/message2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData msg2(HttpServletRequest request, @RequestParam(defaultValue="1") int type, @RequestParam(defaultValue="0") long startId, @RequestParam(defaultValue="10")  int pageSize){
		UserInfo userInfo=this.getUser(request);
		ReturnListData map=messageService.getMymsg(userInfo,type,startId,pageSize);
		if(map==null) {
			return map;
		}
		return map;
	}

	
	
	/**
	 * v3.0
	 * 源生
	 * 消息主页
	 * startId 当前列表最后一位消息的id
	 */
	@RequestMapping(value={"/auth/my/message/index"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData msg3(HttpServletRequest request, @RequestParam(defaultValue="0") long startId, @RequestParam(defaultValue="10")  int pageSize){
		UserInfo userInfo=this.getUser(request);
		ReturnData map=messageService.messageIndex(userInfo,startId,pageSize);
		return map;
	}

	/**
	 * 用户消息清理
	 * 隐藏
	 * @param request
	 * @param ids: 消息的id,逗号分隔
	 * @return
	 */
	@RequestMapping(value={"/auth/my/message/clear"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData del(HttpServletRequest request,@RequestParam String ids){
		return messageService.hiddenUserMessage(ids);
	}
	

	/**
	 * 打开消息 
	 * 清空新消息数量
	 */
	@RequestMapping(value={"/auth/my/message/open"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> msgopen(HttpServletRequest request){
		UserInfo userInfo=this.getUser(request);
		 if(messageService.msgAllOpen(userInfo)) {
			return defaultAjax();
		 }else {
			return errorAjax();
		 }
 	}
	
	/**
	 * 新消息数量
	 * 从用户信息中获取
	 */
	@RequestMapping(value={"/auth/my/message/new/num"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> newcount(HttpServletRequest request){
		UserInfo userInfo=this.getUser(request);
		 Integer count=userService.newMsgCount(userInfo);
		 if(count==null) {
			return errorAjax(); 
		 }
		 Map map=new HashMap();
		 map.put("newMsgNum", count);
		 return returnAjax(map, 0);
 	}
	
 

 	/**
 	 * 修改用户资料
 	 * @param request
 	 * @param nickname: 昵称
 	 * @param headimgurl: 头像
 	 * @param age: 年龄
 	 * @param sex: 性别
 	 * @param yunfu: 孕妇
 	 * @param province: 省
 	 * @param city: 城市
 	 * @return
 	 */
	@RequestMapping(value={"/auth/my/update/info"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData update(HttpServletRequest request,@RequestParam(required=false) String nickname,
			@RequestParam(defaultValue="",required=false) String headimgurl,
			@RequestParam(defaultValue="0",required=false) int age,
			@RequestParam(defaultValue="0",required=false) int sex,
			@RequestParam(defaultValue="0",required=false) int yunfu,
			@RequestParam(defaultValue="",required=false) String province,
			@RequestParam(defaultValue="",required=false) String city){
		long userId=this.getUserId(request);
		ReturnData cols=userService.updateUser(userId,nickname,headimgurl,age,sex,province,city,yunfu);
		//重新加载user
		if(cols.getRet()!=0){
			return cols;
		}
		ReturnData ui=this.reloadUser(request);
		return ui;
 	}
	
	/**
 	 * 用户添加地址
 	 * @param request
 	 * @param province: 省
 	 * @param city: 城市
 	 * @return
 	 */
	@RequestMapping(value={"/auth/my/update2/info"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData updateUserInfo2(HttpServletRequest request,
			@RequestParam(required=false,defaultValue="") String province,
			@RequestParam String city,
			@RequestParam String district,
			@RequestParam String detail,
			@RequestParam(required=false,defaultValue="") String zip,
			@RequestParam String phone,
			@RequestParam String receiver){
		long userId=this.getUserId(request);
		ReturnData cols=userService.addOrUpdateAddress(userId,detail,province,city,district,phone,zip,receiver);
		//重新加载user
		if(cols.getRet()!=0){
			return cols;
		}
		ReturnData ui=this.reloadUser(request);
		return ui;
 	}
 
	/**
	 * 初始化达人信息主页
	 * @param request
	 * @param userid: 达人id
	 * @return
	 */
	@RequestMapping(value={"/user/index"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData expertInit(HttpServletRequest request,@RequestParam String userid){
		ReturnData user= userService.expert(userid);
		return user;
	}

	/**
	 * 获取达人文章
	 * @param request
	 * @param userid: 达人id
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value={"/user/find"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData expertFind(HttpServletRequest request, @RequestParam String userid, @RequestParam(defaultValue = "1") int pager, @RequestParam(defaultValue = "10") int pageSize){
		if (pageSize > 10){
			pageSize = 20;
		}
		ReturnListData user= userService.findByUserIdCache(userid,pager,pageSize);
		return user;
	}

	/**
	 * 获取达人修行说
	 * @param request
	 * @param userid: 达人id
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value={"/user/doyen"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData expertDoyen(HttpServletRequest request, @RequestParam String userid, @RequestParam(defaultValue = "1") int pager, @RequestParam(defaultValue = "10") int pageSize){
		if (pageSize > 10){
			pageSize = 20;
		}
		ReturnListData user= userService.doyenListCache(userid,pager,pageSize);
		return user;
	}
	
	
 	/**
	 * 用户提交反馈
	 * 1、actionname(实体名称 必须)
		  val:
		        skin_test(肤质测试)
		        goods(产品)
		        find(发现反馈)
		        suggestion(意见)
		        comment_goods（产品评论）
		        comment_composition(成分评论)
		        comment_find(发现评论)
		        comment_lists（清单评论）
		        comment_user_part_lists（心得评论）
		        comment_apply_goods(福利社评论)
		        discuss_compare_goods(对比讨论)
		    页面对应
		2、action(操作 必须):
		    val:
		        1 纠错: 需要actionId
		        2 反馈: 不需要actionId
		        3 举报: 需要actionId
		    页面对应    
		3、ationtype(操作说明)
		    val:
		        随着actionname和action而变化
		        用户选择:选项/直接输入
		4、actionid(操作id)
		    val:
		        随着entityname和action而变化
		        代码获取
		5、content(描述)
		    val:
		        用户输入
		6、source(来源)
		    val:
		        ios、android、web、h5、weixin
		7、v(version版本號)
			val:
		8、o(platform用户的操作系统)
			val:ios、android
		9、model(机型)
			val:
		10、uuid(uuid)
			val:
	 *
	 */
	@RequestMapping(value={"/auth/feedback"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData feedback(HttpServletRequest request, @RequestParam String actionname,
                               @RequestParam int action,
                               @RequestParam(defaultValue="0",required=false) int actiontype,
                               @RequestParam(defaultValue="0",required=false) long actionid,
                               @RequestParam(defaultValue="",required=false) String content,
                               @RequestParam(defaultValue="",required=false) String images,
                               @RequestParam(defaultValue="",required=false) String source,
                               @RequestParam(defaultValue="2.0",required=false) String v,
                               @RequestParam(defaultValue="ios",required=false) String o,
                               @RequestParam(defaultValue="00",required=false) String model, @RequestParam(defaultValue="00",required=false) String uuid,
                               @RequestParam(defaultValue="",required=false,name="sys_v") String sysV, @RequestParam(defaultValue="",required=false,name="fields_1") String fields1
			, @RequestParam(defaultValue="",required=false,name="fields_2") String fields2, @RequestParam(defaultValue="",required=false,name="fields_3") String fields3){
		UserInfo userInfo=this.getUser(request); 
		ReturnData cols=feedBackService.saveFeedBack(actionname,userInfo,action,actiontype,actionid,content,images,source,v,o,model,uuid,sysV,fields1,fields2,fields3);
		return cols;
 	}
	
	
	
	/**
	 * 文件上传
	 * @param request
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	@RequestMapping(value={"/auth/upfile/{dir}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData upfiledir(HttpServletRequest request, @PathVariable String dir, @RequestParam("file") MultipartFile file) throws IOException {
		Long userId=this.getUserId(request);
		return aliyunService.upFile(dir,file.getOriginalFilename(),file.getInputStream(),userId);
	}

	/**
	 * 文件上传,需要登录
	 * @param request
	 * @param dir: 目录
	 * @param file: 上传的文件
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/auth/upfile2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData upfiledir2(HttpServletRequest request, @RequestParam String dir, @RequestParam(required=false) MultipartFile file, @RequestParam(required=false) MultipartFile image) throws IOException {
		Long userId=this.getUserId(request);
		MultipartFile ifi=null;
		if(file==null) {
			ifi=image;
		} else {
			ifi=file;
		}
		if(ifi!=null) {
			return aliyunService.upFile(dir,ifi.getOriginalFilename(),ifi.getInputStream(),userId);
		} else {
			return ReturnData.ERROR;
		}
	}
	
	/**
	 * 不需要登录
	 * 文件上传 临时目录
	 * 目前仅作用于微信注册后的第一次登录头像上传
	 * @param request
	 * @param dir: 目录
	 * @param file: 文件
	 * @param image: 图片
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/upfile3"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData upfiledir3(HttpServletRequest request, @RequestParam String dir, @RequestParam(required=false) MultipartFile file, @RequestParam(required=false) MultipartFile image) throws IOException {
		MultipartFile ifi=null;
		if(file==null) {
			ifi=image;
		} else {
			ifi=file;
		}
		if(ifi!=null) {
			return aliyunService.upFile2(dir,ifi.getOriginalFilename(),ifi.getInputStream(),null);
		} else {
			return ReturnData.ERROR;
		}
	}
	
	/**
	 * 需要登录
	 * 文件上传 临时目录
	 * @param dir: 目录
	 * @param file: 文件
	 * @param image: 图片
	 * @throws IOException
	 */
	@RequestMapping(value={"/auth/upfile3"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData upfiledir4(HttpServletRequest request, @RequestParam String dir, @RequestParam(required=false) MultipartFile file, @RequestParam(required=false) MultipartFile image) throws IOException {
		Long userId=this.getUserId(request);
		MultipartFile ifi=null;
		if(file==null) {
			ifi=image;
		} else {
			ifi=file;
		}
		if(ifi!=null) {
			return aliyunService.upFile2(dir,ifi.getOriginalFilename(),ifi.getInputStream(),userId);
		} else {
			return ReturnData.ERROR;
		}
	}

	
	/**
	 * copy临时目录中的数据到正式目录
	 * @param request
	 * @param sourceDir: 正式目录路径
	 * @param sourceKey: 文件名
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/auth/move/upfile3"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData doUpfiledir3(HttpServletRequest request, @RequestParam(name="souece_dir") String sourceDir, @RequestParam(name="source_key") String sourceKey) throws IOException {
		return aliyunService.upOss2(sourceDir,sourceKey);
	}
	
	/**
	 * 文件上传
	 * 上传base64文件到 oss
	 * @param request
	 * @param dir: 目录
	 * @param image: 文件
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/auth/base64_upfile2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData base64Upfile2(HttpServletRequest request, @RequestParam String dir, String image) throws IOException {
		Long userId=this.getUserId(request);
		if(!StringUtils.isBlank(image)) {
			return aliyunService.upOssBase64FileByDir(userId,dir,image);
		} else {
			return ReturnData.ERROR;
		}
	}


	/**
	 * pc文件上传获取文件名
	 * @param request
	 * @throws IOException
	 */
	@Deprecated
	@RequestMapping(value={"/auth/uuid/filename"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData upfiledir2(HttpServletRequest request) throws IOException {
		Long userId=this.getUserId(request);
		return uploadGoodsService.getUUIDFileName(userId);
	}

	/**
	 * pc文件上传获取文件名
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/auth/pc/upfile2"},method={RequestMethod.POST})
	@ResponseBody
	public Map upfiledir2(HttpServletRequest request, @RequestParam String petname, @RequestParam String pic, @RequestParam String pic1) throws IOException {
		Long userId=this.getUserId(request);
		String ossDir="Goods/userupload";
		 Map<String,String> map= aliyunService.upOssBase64FileByDir(ossDir, petname, pic);
		 Map<String,String> map1= aliyunService.upOssBase64FileByDir(ossDir,"cut_" +petname, pic1);
		 Map rt=new HashMap();
		 rt.put("result", map);
		 rt.put("ret", 0);
		 rt.put("status", 1);
		 return rt;
	}
	
	/**
	 * flash跨域请求
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/crossdomain.xml"},method={RequestMethod.GET})
	@ResponseBody
	public String crossdomain(HttpServletRequest request) throws IOException {
		 return  "<?xml version='1.0' encoding='UTF-8'?><cross-domain-policy><allow-access-from domain='*' to-ports='*'/></cross-domain-policy>";
	}

	
	/**
	 * pc
	 * 我的 上传的图片
	 * @param request
	 * @param used: 已经上传的图片的状态,0正在审核 1成功 2失败
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value={"/auth/my/upload/goods"})
	@ResponseBody
	public ReturnListData submitGoods(HttpServletRequest request, @RequestParam(defaultValue="0") int used, @RequestParam(defaultValue="0") int pager
			, @RequestParam(defaultValue="10") int pageSize){
		if(pageSize>40){
			pageSize=40;
		} 
		long userId=this.getUserId(request);
		return uploadGoodsService.mySubmitGoods(used,pager,pageSize,userId);
	}   
	
	/**
	 * 3.0我的上传的产品列表 状态(审核中  成功 失败)
	 * @return
	 *//*
	@RequestMapping(value={"/auth/my/submit/list/goods"})
	@ResponseBody
	public ReturnListData submitGoodsList(HttpServletRequest request,@RequestParam(defaultValue="0") int state,@RequestParam(defaultValue="0") int pager
			,@RequestParam(defaultValue="10") int pageSize){
		if(pageSize>40){
			pageSize=40;
		} 
		long userId=this.getUserId(request);
		return uploadGoodsService.mySubmitGoodsList(state,pager,pageSize,userId);
	}   */
	

	/**
	 * 提交图片上传审核 ,只支持单张图片
	 * @param request
	 * @param goods_id: 产品id
	 * @param image: 上传的图片
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/temp/goods"})
	@ResponseBody
	public ReturnData fileUpload(HttpServletRequest request, @RequestParam String goods_id, @RequestParam String image
		) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return uploadGoodsService.uploadGoods(goods_id, image, userInfo);
	}  
	
	/**
	 * v2.6
	 * 更换/改绑账号
	 * @param response
	 * @param request
	 * @param new_account: 新的账号
	 * @param vcode: 验证码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/modify/account"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData modifyAccount(HttpServletResponse response,
                                    HttpServletRequest request,
                                    @RequestParam String new_account, @RequestParam String vcode) throws Exception {
		UserInfo userInfo=this.getUser(request);
		ReturnData rd= myService.modifyAccount(userInfo,new_account,vcode);
	    if(rd.getRet()==0) {
	    	//重新加载用户
		    this.reloadUser(request);
	    }
	    return rd;

	}

	/**
	 * v2.6
	 * 修改密码
	 * @param request
	 * @param old_password: 旧的密码
	 * @param new_password: 新的密码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/modify/password"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData modifyPassword(HttpServletRequest request,
                                     @RequestParam String old_password, @RequestParam String new_password) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return myService.modifyPassword(userInfo,old_password, new_password);
	}
	
	
	/**
	 * 我的肤质方案列表
	 * @return
	 * @throws Exception
	 */      
	@RequestMapping(value={"/auth/my/skin_protection_category"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionCategory(HttpServletRequest request) throws Exception {
		UserInfo userInfo=this.getUser(request);
		ReturnData rd=new ReturnData(skinProtectionService.getUserGoodsCategorys(userInfo.getId()));
		return rd;
	}   


	/**
	 *  添加或者修改某个肤质方案/分类名
	 *  category_name
		id: 
		pid: 用于新增分类的时候
	 * @param request
	 * @param id: 修改的时候id是通用的	修改分类名/方案名 
	 * @param pid: 用于新增分类的时候
	 * @param category_name(必须参数):方案名/分类名
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/skin_protection_category/add_update"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionCategoryAdd(HttpServletRequest request
			, @RequestParam(defaultValue="0",required=false) Long id, @RequestParam(defaultValue="0",required=false) Long pid , @RequestParam String category_name) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return skinProtectionService.addOrUpdateSkinProtectionCategory(id,pid,category_name,userInfo.getId());
	}   
	
	
	/**
	 * 删除肤质方案或者分类    
	 * @param request
	 * @param category_id: 分类id
	 * @param category_pid: 方案id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/skin_protection_category/delete"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionCategoryDelete(HttpServletRequest request
			, @RequestParam(defaultValue="0",required=false) Long category_id, @RequestParam(defaultValue="0",required=false) Long category_pid ) throws Exception {
		UserInfo userInfo=this.getUser(request);
		ReturnData rd=null;
		if(category_id!=null&&category_id>0){
			//删除分类与下面的产品
			rd= skinProtectionService.deleteSkinProtectionByCategoryId(category_id,userInfo,2); 
		}else{ 
			//删除方案与下面的产品
			rd= skinProtectionService.deleteSkinProtectionByCategoryId(category_pid,userInfo,1); 
		}
		//重新加载用户信息
		if(rd.getRet()==0){
			this.reloadUser(request);
		}
		return rd;
	}   
	
	
	/**
	 * 肤质方案分类排序
	 * 
	 * @param ids: 方案的id,逗号分隔
	 * @param sorts: 对应方案的排序值(正序),逗号分隔
	 * @return
	 * @throws Exception
	 */      
	@RequestMapping(value={"/auth/my/skin_protection_category/sort"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionCategorySort(HttpServletRequest request
			, @RequestParam String sorts , @RequestParam String ids) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return skinProtectionService.sortSkinProtectionCategory(ids,sorts,userInfo.getId());
	}  


	/**
	 * 护肤方案 修改产品,支持自定义的产品
	 * @param request
	 * @param id: 当前数据的自增id
	 * @param category_id: 分类id
	 * @param category_pid: 方案id
	 * @param open: 是否开封,1表示开封
	 * @param open_time: 开封日期
	 * @param release_date: 保质期
	 * @param used_type: 使用时段 1白天 2晚上 3全天
	 * @param image: 自定义的产品图片
	 * @param title: 自定义的产品名称
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/skin_protection_goods/update"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionGoodsAdd(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) Long id
			,@RequestParam(defaultValue="0",required=false) Long category_id
			,@RequestParam(defaultValue="0",required=false) Long category_pid
			,@RequestParam(required=false) Integer open
			,@RequestParam(required=false) Long open_time
			,@RequestParam(required=false) Integer release_date
			,@RequestParam(required=false) Integer used_type
			,@RequestParam(required=false) String image,
			@RequestParam(required=false) String title
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		String tname="goods";
		ReturnData rd=skinProtectionService.addOrUpdateSkinProtectionGoods(userInfo,tname,id,title,category_id,category_pid,open,open_time,release_date,used_type,image,null);
		if(rd.getRet()==0){
			//更新过期产品的信息
			this.reloadUser(request);
		}
		return rd;
	}   
	
	/**
	 * 添加产品
	 * @param request
	 * @param category_id: 分类id
	 * @param category_pid: 方案id
	 * @param image: 自定义产品图片
	 * @param title: 自定义产品名
	 * @param entity_id: 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/skin_protection_goods/add"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionGoodsAdd(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) Long category_id
			,@RequestParam(defaultValue="0",required=false) Long category_pid
			,@RequestParam(required=false) String image,
			@RequestParam(required=false) String title,
			@RequestParam(required=false) Long entity_id,
			
			@RequestParam(required=false) Integer open,
			@RequestParam(required=false) Long open_time,
			@RequestParam(required=false) Integer used_type,
			@RequestParam(required=false) Integer release_date
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		String tname="goods";
		return skinProtectionService.addOrUpdateSkinProtectionGoods(userInfo,tname,null,title,category_id,category_pid,open,open_time,release_date,used_type,image,entity_id);
	}   



	/**
	 * 删除肤质方案/分类,不删除下面的产品
	 * @param id: 护肤方案/分类id
	 * @return
	 * @throws Exception
	 */      
	@RequestMapping(value={"/auth/my/skin_protection_goods/delete"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionGoodsDelete(HttpServletRequest request
			,@RequestParam Long id ) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return skinProtectionService.deleteSkinProtectionGoods(id,userInfo.getId());
	}   
	
	/**
	 * 查询某个护肤方案下的分类和所有产品
	 * @param request
	 * @param category_pid: 方案id
	 * @param start_id: 产品的分页 
	 * @param page_size 
	 * @return
	 * @throws Exception
	 */
 	@RequestMapping(value={"/auth/my/skin_protection_goods"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData skinProtectionGoods(HttpServletRequest request
			, @RequestParam Long category_pid , @RequestParam(defaultValue = "0") long start_id, @RequestParam(defaultValue = "1000") int page_size) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return skinProtectionService.findSkinProtectionGoods(userInfo.getId(),category_pid,start_id,page_size);
	}   
	
	/**
	 * 我的心得列表
	 * @param request
	 * @param tname: lists
	 * @param start_id: 大于0时,start_id为当前列表最小的心得id
	 * @param page_size
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/user_part/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData myUserPartLists(HttpServletRequest request, @PathVariable String tname, @RequestParam(defaultValue = "0") long start_id, @RequestParam(defaultValue = "10") int page_size ) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return userPartService.myUserPartLists(tname,userInfo.getId(),start_id,page_size);
	}   
	
	
	/**
	 * 福利社-我的产品申请列表
	 * @param request
	 * @param startId: 大于0时,start_id为当前列表最小的id
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/apply_goods/list"},method={RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public ReturnListData applyGoodsList(HttpServletRequest request, @RequestParam(defaultValue = "0",name="start_id") long startId, @RequestParam(defaultValue = "10",name="page_size") int pageSize ) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return applyGoodsService.myApplyGoodsLists(userInfo.getId(),startId,pageSize);
	}   
	
	/**
	 * 福利社-我的产品申请列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/apply_goods2/list"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData applyGoods2List(HttpServletRequest request, @RequestParam(defaultValue = "0",name="start_id") long startId, @RequestParam(defaultValue = "10",name="page_size") int pageSize ) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return applyGoods2Service.myApplyGoodsLists(userInfo.getId(),startId,pageSize);
	}   
	

	
	/**
	 * 添加/修改用户感兴趣的标签
	 * @param request
	 * @param skinTags: 用户感兴趣的标签,json格式字符串
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/my/tags/add"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addOrUpdateTag(HttpServletRequest request
			,@RequestParam(name="skin_tags") String skinTags
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		ReturnData rd= skinFlowService.addOrUpdateSkinTags(userInfo,skinTags);
		if(rd.getRet()==0) {
			//重新加载user
			ReturnData ui=this.reloadUser(request);
			return ui;
		}
		return ReturnData.ERROR;
	}   
	/**
	 * 身份验证接口
	 * @param request
	 * @param real_name
	 * @param id_card
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/idcard/verifyIdcardv2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData proofOfIdentiy(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) String real_name
			,@RequestParam(defaultValue="0",required=false) String id_card
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		//String uuid = String.valueOf(ComReqModel.getReqComAgrs().get(StatisticsI.FIELD_UUID));
		String uuid = request.getParameter("uuid");
		ReturnData ru= myService.proofOfIdentiy(real_name,id_card,userInfo,uuid);
		if(ru.getRet()==0) {
			this.reloadUser(request);
		}
		return ru;
	}  
	
	
	/**
	 * 保存用户ip地址
	 * @param userId
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"app/addUserLocation"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addUserLocation(HttpServletResponse response, HttpServletRequest request,
			       @RequestParam(required=true) Double lng, @RequestParam(required=true) Double lat ) throws Exception {
		UserInfo userInfo=this.getUser(request);
		String guestIP = IPUtils.getIp(request);
		return  userService.addUserLocation(userInfo,guestIP,lng,lat);

	}
	 
}
