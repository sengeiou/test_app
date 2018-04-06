package cn.bevol.search.web;

import cn.bevol.util.ConfUtils;
import cn.bevol.util.DataInfo;
import cn.bevol.util.Desc;
import cn.bevol.util.SearchAnalysis;
import flexjson.JSONDeserializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ConfController extends BaseController{
	/**
	 * 数据类型
	 * {'/a/b/c':{'sort':{'mx':'-cps;+create'},mustField:['categroy','keywords'],'feild':{'cps':{relation:'AND','type':'a'},'categroy':{'type':'i'},'notcps':{'relation':'AND','type':'a'}},'relation':'cps_sa AND bps_i AND ms_s&&sort=-crate'}}
	 */
	
 	@RequestMapping(value={"/c/{url}"})
	@ResponseBody
	public Map<String, Object> c(HttpServletRequest request,
			@PathVariable String url,
			@RequestParam Map<String,String> params,
			@RequestParam(defaultValue="1") int p,
			@RequestParam(defaultValue="20",required=false) int rows){
 		String cstring= ConfUtils.getResourceString("search_"+url);
 		DataInfo dinfo=  new JSONDeserializer<DataInfo>().use("sort", HashMap.class).use("feild", Desc.class).use("relation", String.class) .deserialize(cstring, DataInfo.class);
		String sql= SearchAnalysis.createSearch(dinfo, params);
		Map m=this.returnAjax(dinfo.getIndexName(), sql, p, rows,0);
		return m;
	}
	
	/**
	 * 后台搜索接口
	 * @param request
	 * @param tname
	 * @param sql
	 * @param fields
	 * @param p
	 * @param rows
	 * @return
	 */
 	@RequestMapping(value={"/inner/{tname}"})
	@ResponseBody
	public Map<String, Object> c(HttpServletRequest request,
			@PathVariable String tname,
			@RequestParam String sql,
			@RequestParam String fields,
			@RequestParam(defaultValue="1") int p,
			@RequestParam(defaultValue="20",required=false) int rows){
		Map m=this.returnAjax(tname, sql, p, rows,fields.split(","));
		return m;
	}
	


}
