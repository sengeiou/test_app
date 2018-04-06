package cn.bevol.app.controller;

import cn.bevol.app.entity.vo.UserEntityAction;
import cn.bevol.app.service.BaseService;
import cn.bevol.app.service.EntityService;
import cn.bevol.app.service.GoodsService;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class EntityController extends BaseController {

		@Autowired
		private EntityService entityService;
		
		@Autowired
	    private GoodsService goodsService;
		
		@Autowired
	    private BaseService baseService;
		

		/*******2.0**********
		/**
		 *  实体名称tname
		 *   goods  
		 *   composition
		 *   find
		 *   list 清单
		 *  实体对应的id  
		 */
		@Deprecated
		@RequestMapping(value={"/entity/status/{tname}"},method={RequestMethod.GET})
		@ResponseBody
		public Map status(HttpServletRequest request, @RequestParam long id, @PathVariable String tname) throws Exception {
			/*记录访问日志*/
			//Map mp=entityService.addHit(tname, entityid);
			long userId=this.getUserId(request);
			//总的喜欢数 
			//总的收藏数
			//总的访问数
			UserEntityAction eb=entityService.entityInfo(tname, id,userId);
            if (eb == null) {
                return errorAjax();
            }
            return returnAjax(eb, 0);
		}

		
		
		
		/**
		 * 获取实体状态,评论数/点赞数/点击数等信息
		 *	   查询实体状态
		 *   实体名称tname 状态
		 *   goods
		 *   composition
		 *   find
		 *  实体对应的id
		 */
		@RequestMapping(value={"/entity/states/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public Map info(HttpServletRequest request, @RequestParam String ids, @PathVariable String tname) throws Exception {
			String[] idsStr=ids.split(",");
			List<Long> listIds=new ArrayList<Long>();
			int num=10;
			if(idsStr.length<10){
				num=idsStr.length;
			}
			for(int i=0;i<num;i++) {
				if(!StringUtils.isBlank(idsStr[i])){
					listIds.add(Long.parseLong(idsStr[i]));
				}
			}
			List<EntityBase> eb=entityService.getStates(tname, listIds);
            if (eb == null||eb.size()==0) {
                return errorAjax();
            }  
            
            for(int i=0;i<listIds.size();i++){
            	entityService.objectIncById(listIds.get(i), "entity_"+tname, "hitNum", 1);
            }
            
            //todo 解决 ios 获取属性不对的问题
            if("apply_goods".equals(tname))  {
                ObjectMapper mapper = new ObjectMapper();
                String json=mapper.writeValueAsString(returnlistAjax(eb, eb.size()));
        		String str= StringUtils.replace(json, "\"image\":\"", "\"image\":\"https://img1.bevol.cn/apply_goods/");
        		Map m=mapper.readValue(str, Map.class);
                return m;
            }
            return returnlistAjax(eb, eb.size());

		}

		
   
		/**
		 *	   查询实体状态
		 *   实体名称tname 状态
		 *   goods
		 *   composition
		 *   find
		 *  实体对应的id
		 */
		@Deprecated
		@RequestMapping(value={"/entity/state/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData info(HttpServletRequest request, @RequestParam long id, @PathVariable String tname) throws Exception {
 			//总的喜欢数 
			//总的收藏数
			//总的访问数
			ReturnData eb=entityService.entityState(tname, id);
            return eb;
		}
		
		@RequestMapping(value={"/entity/state3/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData entityState(HttpServletRequest request, @RequestParam(required=false,defaultValue="0") Long id, @RequestParam(required=false,defaultValue="") String mid, @PathVariable String tname) throws Exception {
 			//总的喜欢数 
			//总的收藏数
			//总的访问数
			ReturnData eb=entityService.entityStateByIdOrMid(tname, id,mid);
            return eb;
		}

		
		/*******2.0**********  
		/**
		 *   查询用户实体关系
		 *   goods
		 *   composition
		 *   find
		 *  实体对应的id
		 */
		@RequestMapping(value={"/auth/entity/relation/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public Map relation(HttpServletRequest request, @RequestParam long id, @PathVariable String tname) throws Exception {
			/*记录访问日志*/
			//Map mp=entityService.addHit(tname, entityid);
			
			
			//todo android 修复实体阅读统计
            String o=request.getParameter("o");
            String v=request.getParameter("v");
            if(StringUtils.isNotBlank(o)&& StringUtils.isNotBlank(v)) {
            	if(o.toLowerCase().trim().equals("android")&&(v.trim().equals("3.0.2")||v.trim().equals("3.0.0")||v.trim().equals("3.0.1"))&& StringUtils.isNotBlank(tname)) {
            		baseService.objectIncById(id, "entity_"+tname, "hitNum", 1);
            	}
            }

            
			long userId=this.getUserId(request);
			
			if(userId>0)  {
				//总的喜欢数 
				//总的收藏数
				//总的访问数
				UserEntityAction eb=entityService.entityRelation(tname, id,userId);
	            if (eb == null) {
	                return errorAjax();
	            }
	            return returnAjax(eb, 0);
			} else {
				Map map=new HashMap();
				map.put("ret", -5);
	            return map;

			}
				
		}

		/**
		 * 旧版本喜欢不喜欢
 		 *
		 */
		//lists_apply_goods
		@RequestMapping(value={"/auth/entity/like/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData like(HttpServletRequest request, @RequestParam long id, @RequestParam int type, @PathVariable String tname) throws Exception {
			/*记录访问日志*/
			//Map mp=entityService.addHit(tname, entityid);
			UserInfo userInfo=this.getUser(request);
			//总的喜欢数
			//总的收藏数
			//总的访问数
			long userId=this.getUserId(request);
			return entityService.actionLike(tname, id, userInfo, type);
 		}

		
		/**
		 * 2017喜欢/不喜欢
 		 *
		 */
		@RequestMapping(value={"/auth/entity/like2/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData like2(HttpServletRequest request, @RequestParam long id, @RequestParam int type, @PathVariable String tname) throws Exception {
			/*记录访问日志*/
			//Map mp=entityService.addHit(tname, entityid);
			UserInfo userInfo=this.getUser(request);
			//总的喜欢数
			//总的收藏数
			//总的访问数
			long userId=this.getUserId(request);
			return entityService.actionLike2(tname, id, userInfo, type);
 		}

		/**
		 * 收藏
 		 *
		 */
		@Deprecated
		@RequestMapping(value={"/auth/entity/collection/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData collection(HttpServletRequest request, @RequestParam long id, @RequestParam int type, @PathVariable String tname) throws Exception {
			/*记录访问日志*/
			//Map mp=entityService.addHit(tname, entityid);
			UserInfo userInfo=this.getUser(request);
 			//总的喜欢数
			//总的收藏数
			//总的访问数

            return entityService.actionCollection(tname, id, userInfo, type);
 		}

		
 
 		/**
		 * 批量取消喜欢
		 * @param request
		 * @param ids: 实体id,逗号分隔
		 * @param tname
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/auth/entity/cancel_like/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData cancelLike(HttpServletRequest request, @RequestParam String ids, @PathVariable String tname) throws Exception {
			UserInfo userInfo=this.getUser(request);
			long userId=this.getUserId(request);
			return entityService.cancelLike(tname, ids, userInfo);
		}
		
 		
		/**
		 * 产品纠错(php)
		 * @param request
		 * @param tname
		 * @return
		 */
		@RequestMapping(value={"/auth/entity/recovery/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public Map<String, Object> recovery(HttpServletRequest request, @PathVariable String tname, @RequestParam long id, @RequestParam String content){
			Map<String, Object> map=new HashMap<String, Object>();
			Map state=new HashMap();
			long userId=this.getUserId(request);
			if(entityService.saveRecovery(tname, id,userId,content)!=null){
                return defaultAjax();
            }else {
                return errorAjax();
            }
		}
		
		
		
		/**
		 *  实体列表tname
		 *   goods  
		 *   composition
		 *   find
		 *  实体对应的id  
		 */
		@Deprecated
		@RequestMapping(value={"/entity/list/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnListData list(HttpServletRequest request, @RequestParam(defaultValue="0") int type, @PathVariable String tname, @RequestParam(defaultValue="0") int pager, @RequestParam(defaultValue="10") int pageSize) throws Exception {
			/*记录访问日志*/
			//总的喜欢数 
			//总的收藏数
			//总的访问数
			if(pageSize>10){
				pageSize=20;
			}
             return entityService.entityList(tname, type,pager,pageSize);
		}
		



	/**
	 * 小程序产品详情页用接口  产品功效关系列表
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/widget/goods/info/{mid}"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData goodsEffectUsedWidget(HttpServletRequest request, @PathVariable String mid) throws Exception {
		return goodsEffectUsed(request, mid);
	}

	/**
		 * 产品功效关系列表
		 * 产品详细
		 * @param mid: 产品mid
		 * @return
		 * @throws Exception
		 */      
		@RequestMapping(value={"/goods/info/{mid}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData goodsEffectUsed(HttpServletRequest request
			,@PathVariable String mid) throws Exception {
			UserInfo userInfo=this.getUser(request);
			return goodsService.getGoodsExplain(mid,userInfo);
		}   
		
		
		/**
		 * oss静态化 产品功效关系列表
		 * 
		 * @param request
		 * @return
		 * @throws Exception
		 */      
		@RequestMapping(value={"/goods/info/mid"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData goodsDetail(HttpServletRequest request
			,@RequestParam String mids) throws Exception {
			return goodsService.goodsStatic(mids);
		}   

		
		/**
		 * oss静态化 产品功效关系列表
		 * 
		 * @param request
		 * @return
		 * @throws Exception
		 */      
		@RequestMapping(value={"/goods/info/mid/{mid}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData goodsStatic(HttpServletRequest request
			,@PathVariable String mid) throws Exception {
			return goodsService.goodsStatic(mid);
		}   

		/**
		 *  安全更新
		 *  
		 * 
		 * @param request
		 * @param  mids 以逗号隔开
		 * @return
		 * @throws Exception
		 */
		@Deprecated
		@RequestMapping(value={"/goods/update/{safter}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData updateGoodsSafter(HttpServletRequest request
			, @PathVariable String safter, @RequestParam String mids) throws Exception {
			return goodsService.updateGoodsSafter(mids);
		}   


		/**
		 * 产品对比
		 * 
		 * @param mids: 产品mid,逗号分隔
		 * @return
		 * @throws Exception
		 */      
		@RequestMapping(value={"/goods/compare"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData goodsCompare(HttpServletRequest request
			,@RequestParam String mids) throws Exception {
			UserInfo userInfo=this.getUser(request);
			return goodsService.getCompare(mids,userInfo);
		}   
		
		
		/**
		 * 得到产品安全星级
		 * 
		 * @param ids: 产品id,逗号分隔
		 * @return
		 * @throws Exception
		 */
		@Deprecated
		@RequestMapping(value={"/goods/safter"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData goodsSafter(HttpServletRequest request
			,@RequestParam String ids) throws Exception {
			UserInfo userInfo=this.getUser(request);
			return goodsService.getSafter(ids);
		}   
		
		
		
		
		/**
		 * v3.1
		 * 用户和实体的关系+实体状态+实体详情
		 * 获取实体信息 
		 * @param request
		 * @param id: 实体id
		 * @param tname
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/entity/info/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData entityInfo(HttpServletRequest request, @RequestParam(required=false) Long id, @PathVariable String tname) throws Exception {
			/*记录访问日志*/
			//Map mp=entityService.addHit(tname, entityid);
			//long userId=this.getUserId(request);
			UserInfo userInfo=this.getUser(request);
			//总的喜欢数 
			//总的收藏数
			//总的访问数
			ReturnData eb=entityService.entityInfo2(tname, id,userInfo);
            return eb;
		}	
		
		
		/**
		 * v3.2
		 * 用户和实体的关系+实体状态+实体详情
		 * 产品和成分用mid
		 * 获取实体信息 
		 * @param request
		 * @param id: 实体id
		 * @param tname
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/entity/info2/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData entityInfo3(HttpServletRequest request, @RequestParam(required=false) Long id, @PathVariable String tname, @RequestParam(required=false) String mid) throws Exception {
			UserInfo userInfo=this.getUser(request);
			ReturnData eb=entityService.entityInfo3(tname, id,userInfo,mid);
            return eb;
		}

		/**
		 * 获取实体状态(评论数,点击数等数值)
		 * 五分钟缓存
		 * @param request
		 * @param ids
		 * @param tname
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/entity/state2/{tname}"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData entityState(HttpServletRequest request, @RequestParam String ids, @PathVariable String tname) throws Exception {
 			//总的喜欢数 
			//总的收藏数
			//总的访问数
			ReturnData eb=entityService.entityState2(tname, ids);
            return eb;
		}
}
