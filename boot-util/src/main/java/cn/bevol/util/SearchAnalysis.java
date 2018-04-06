package cn.bevol.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAnalysis {

	public static final String arrayType="sa";
	
	public static final String intType="i";

	public static final String stringType="s";

	
	
	/**
	 * opensearch 语句生成器
	 * @param params
	 * @return
	 */
	public static String createSearch(DataInfo sm,Map<String,String> params) {
		List<String> keys=new ArrayList<String>();
		List<String> vals=new ArrayList<String>();
		//排序字段
		Map<String,String> srt=sm.getSort();
		
		List<String> mlists=sm.getMustField();
		//取交集
		if(mlists!=null&&mlists.size()>0&& CollectionUtils.intersection(mlists, params.keySet()).size()==0) return null;
		//关系语句
		String relations=sm.getRelation();
		//relations=StringUtils.deleteWhitespace(relations);
		for(Map.Entry<String, Desc> entry : sm.getWhere().entrySet()){
			String ls=entry.getKey().substring(entry.getKey().length()-1);
			String obj=params.get(entry.getKey());
			
			//确定类型
			//确定关系
			if(!StringUtils.isBlank(obj)) {
				
				//必须包含
				if(mlists.contains(entry.getKey())) {
					
				}
				Desc d=entry.getValue();
				String dbField=d.getDbFeild();
				String fName=entry.getKey();

				//数组 需要 确定关系
				//key替换 val 替换
				keys.add("["+fName+"]");
				if(d.getType().equals(arrayType)) {
					String strs[]=((String) obj).split(",");
					StringBuffer m=new StringBuffer();
					for(int i=0;i<strs.length;i++) {
						m.append(" "+dbField).append(":'"+strs[i]+"' ");
						if(i<strs.length-1)
							m.append(d.getRelation());
					}
					vals.add(" ("+m.toString()+") ");
				} else if(dbField.indexOf("|")!=-1 ){
					String dbfs[]= StringUtils.split(dbField, "|");
					StringBuffer sb=new StringBuffer();
					for(int i=0;i<dbfs.length;i++) {
						sb.append("  "+dbfs[i]+":'"+obj.toString()+"' ");
						if(i<dbfs.length-1)
							sb.append("OR");
					}
					vals.add("("+sb+") ");
				} else if(dbField.indexOf("&")!=-1 ){
					String dbfs[]= StringUtils.split(dbField, "&");
					StringBuffer sb=new StringBuffer();
					for(int i=0;i<dbfs.length;i++) {
						sb.append("  "+dbfs[i]+":'"+obj.toString()+"' ");
						if(i<dbfs.length-1)
							sb.append("AND");
					}
					vals.add("("+sb+") ");
				}else {
					//非数字情况
					vals.add(" "+dbField+":'"+obj.toString()+"' ");
				}
			}
		}
		//排序
		String sortstring="";
		String cres=params.get("sort");
		if(StringUtils.isNotBlank(cres)) {
			sortstring=srt.get(params.get("sort"));
		}
 		//重新处理排序
		if(StringUtils.isNotBlank(sortstring)) {
			String nsort=relations.substring(0,relations.indexOf("&&sort="))+"&&sort="+sortstring;
			relations=nsort;
		}
		//替换
		String sstr= StringUtils.replaceEach(relations,(String[])keys.toArray(new String[0]),(String[])vals.toArray(new String[0]));
		String ms=sstr.replaceAll("NOTAND\\s*\\[[^]]*\\]", " ").replaceAll("\\[[^]]*\\]\\s*NOTAND", " ");
		 ms=ms.replaceAll("AND\\s*\\[[^]]*\\]", " ").replaceAll("\\[[^]]*\\]\\s*AND", " ");
		 ms=ms.replaceAll("OR\\s*\\[[^]]*\\]", " ").replaceAll("\\[[^]]*\\]\\s*OR", " ");
		//String ms=sstr.replaceAll("AND\\s+\\[[^]]*\\]\\s+AND", " AND ").replaceAll("AND\\s+\\[[^]]*\\]", " ").replaceAll("\\[[^]]*\\]\\s+AND", " ");
		 //ms=sstr.replaceAll("NOTAND\\s+\\[[^]]*\\]\\s+NOTAND", " NOTAND ").replaceAll("NOTAND\\s+\\[[^]]*\\]", " ").replaceAll("\\[[^]]*\\]\\s+NOTAND", " ");
		return ms;
	}
	
	public static void main(String[] args) {
		Map<String,String> ps=new HashMap<String,String>();
		ps.put("cps", "1,2,4,5");
		ps.put("categroy", "1");
		ps.put("notcps", "3");
		ps.put("sort", "mx");
		ps.put("keywords", "大是非得失");

	//	String sql=createSearchByUrl("/a/b/c",ps);
	//	System.out.println(sql);
	}
}
