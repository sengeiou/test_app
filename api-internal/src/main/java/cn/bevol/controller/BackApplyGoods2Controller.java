package cn.bevol.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.model.entity.EntityApplyGoods2;
import cn.bevol.entity.service.ApplyGoods2Service;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

/**
 * 提供给后台的接口
 *
 * @author hualong
 *
 */
@Controller
public class BackApplyGoods2Controller extends BaseController {


	@Autowired
	private ApplyGoods2Service applyGoods2Service;

	/**
	 * 福利社设置用户中奖,发送消息
	 *
	 * @param id:
	 *            活动id
	 * @param user_ids:
	 *            中奖用户id,逗号分隔
	 * @param title:
	 *            信息标题
	 * @return
	 */
	@RequestMapping(value = { "/back/apply_goods2/used/setings" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData setApplyGoods(@RequestParam long id,
									@RequestParam String user_ids,
									@RequestParam(defaultValue = "", required = false) String title,
									@RequestParam(required = false, name = "msg_content") String msgContent,
									@RequestParam(required = false, name = "redirect_type") String redirectType,
									@RequestParam(required = false, name = "new_type") Integer newType,
									@RequestParam(required = false, defaultValue = "1") Integer state) {
		return applyGoods2Service.usedGoodsByUserIds(id, user_ids, state, title, msgContent, redirectType, newType);
	}



	/**
	 * 添加福利社文章/活动 title: 文章标题 image: 文章图片 tag: 文章内容 descp: 活动描述 goodsIds:
	 * 活动提供的产品 startTime: 活动开始时间 lastTime: 活动结束时间 doyenScore: 参与活动所需的修行值
	 * goodsNum: 活动提供的产品数量 type: 文章类型 0普通、1长久性、2实效性
	 */
	@RequestMapping(value = { "/back/apply_goods2/add" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData addApplyGoods(HttpServletRequest request, EntityApplyGoods2 entityApplyGoods) throws Exception {
		return applyGoods2Service.addEntityApplyGoods(entityApplyGoods);
	}



	/**
	 * 修改单个福利社活动内容
	 *
	 * title: 文章标题 image: 文章图片 tag: 文章标签 descp: 活动描述 goodsIds: 活动提供的产品
	 * startTime: 活动开始时间 lastTime: 活动结束时间 doyenScore: 参与活动所需的修行值 goodsNum:
	 * 活动提供的产品数量 type: 文章类型 0普通、1长久性、2实效性
	 */
	@RequestMapping(value = { "/back/apply_goods2/update" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData updateApplyGoods(HttpServletRequest request, EntityApplyGoods2 entityApplyGoods) throws Exception {
		return applyGoods2Service.updateEntityApplyGoods(entityApplyGoods);
	}

	/**
	 * 后台福利社列表接口
	 *
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/back/apply_goods2/list" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnListData listApplyGoods(@RequestParam(defaultValue = "0") int pager,
										 @RequestParam(defaultValue = "10") int pageSize,
										 @RequestParam(required = false) Integer id,
										 @RequestParam(required = false) Integer activeState,
										 @RequestParam(required = false) Integer isHidden,
										 @RequestParam(required = false) Integer shareState,
										 @RequestParam(required = false) String title) throws Exception {
		String fields[]=new String[]{"id","userPartNum", "commentNum", "partNum", "prizeNum","notLikeNum","likeNum","hitNum","title","image","tag","tagIds","startTime","lastTime","curTime","activeState","activeStateDesc","goodsNum","applyNum","type","doyenScore","shareState","applyEndTime","prizeEndTime","lastUserPartTime","price","publishTime","hidden"};
		return applyGoods2Service.list(null, id, shareState, title, activeState, isHidden, false, pager, pageSize, fields);
	}


	/**
	 * /**
	 * 根据state,福利社参与申请的用户信息
	 *
	 * @param id:
	 *            申请的活动id
	 * @param state:*
	 *            1参与中 ----活动正在进行中 4参与中 活动已结束 2中奖了 没发过该活动的心得 3中奖了 发过该活动的心得
	 * @param pager
	 * @param pageSize
	 * @param userPartState　是否写了试用报告
	 * @param hasContent　是否有申请内容
	 * @param hasPrized   是否在prizedTime时间内中奖
	 * @param prizedTime  默认90天
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/back/apply_goods2/userlist" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnListData userlist(@RequestParam Long id,
								   @RequestParam(required=false) Long userId,
								   @RequestParam(defaultValue = "0") int state,
								   @RequestParam(required=false) Integer userPartState,
								   @RequestParam(defaultValue = "1") int pager,
								   @RequestParam(defaultValue = "10") int pageSize,
								   @RequestParam(defaultValue = "0") int hasContent,
								   @RequestParam(defaultValue = "0") int hasPrized,
								   @RequestParam(defaultValue = "7776000") int prizedTime,
								   @RequestParam(required = false, defaultValue = "0") int minScore,
								   @RequestParam(required = false, defaultValue = "0") int maxScore) throws Exception {
		return applyGoods2Service.findApplyUserList(id, userId, state,userPartState, hasContent, hasPrized, prizedTime, minScore, maxScore, pager, pageSize);
	}
	/**
	 * 设置用户快递单号
	 *
	 * @param id:   申请的活动id
	 * @param express: 快递公司
	 * @param number 编号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/back/apply_goods2/user_express/setting" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData expressSetting(@RequestParam Long id, @RequestParam String express, @RequestParam String number) throws Exception {
		return applyGoods2Service.expressSetting(id, express,number);
	}

	/**
	 * 同步中奖用户最终地址
	 *
	 * @param id:   申请的活动id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/back/apply_goods2/sync/user_desc" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData syncUserDesc(@RequestParam Long id) throws Exception {
		return applyGoods2Service.syncUserDesc(id);
	}

	/**
	 * 所有需要同步实体信息状态
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/back/apply_goods2/sysn/state" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData sysnState(HttpServletRequest request) throws Exception {
		return applyGoods2Service.syncState();
	}
}
