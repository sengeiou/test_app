package cn.bevol.app.entity.vo;

import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.app.entity.model.GoodsEffectUsed;
import cn.bevol.util.ConfUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成分说详细
 * @author Administrator
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsExplain implements Serializable {
	
	/**
	 * 产品
	 */
	private Goods goods;
	
	/**
	 * 成分排序说明
	 */
	private String cpsTypeDesc;
	/**
	 * 成分
	 */
	private List<Composition> composition;
	
	//产品大分类名称
	private String categoryCateName;
	//产品大分类名称
	private String appCategoryCateText;

	//产品大分类id
	/**
	 * 清洁解读、防嗮解读、功效说
	 */
	private Integer categoryCateId;

	/**
	 * 安全
	 */
	private List<Explain> safety;
	
	/**
	 * 功效
	 */
	private List<Explain> effect;
	
	/**
	 * 适合的成分
	 */
	private Explain   suit;
	
	/**
	 * 不适合的成分
	 */
	private Explain  noSuit;
	
	private Map<String,Integer> safetys=new HashMap<String,Integer>();
	
	private double safe;
	
	private String xinji;
	
	/**
	 * 含有风险的成分的数量
	 */
	private int cpsSize;
	
	/**
	 * 用户的肤质
	 */
	private String userSkin;
	
	public String getUserSkin() {
		return userSkin;
	}

	public void setUserSkin(String userSkin) {
		this.userSkin = userSkin;
	}

	public List<Composition> getComposition() {
		return composition;
	}

	public void setComposition(List<Composition> composition) {
		this.composition = composition;
	}
	public List<Long> getSrcCpsIds() {
		 List<Long> srcCpsIds=new ArrayList<Long>();
		for(int i=0;this.composition!=null&&i<this.composition.size();i++) {
			srcCpsIds.add(this.composition.get(i).getSrcId());
		}
		return srcCpsIds;
	}

 	
	//安全分析
	public void safterAnalysis2() {
		//星级
		safetys=new HashMap<String,Integer>();
			safetys.put("7_10", 0);
			safetys.put("3_6", 0);
			safetys.put("0_2", 0);
		for(int i=0;i<this.composition.size();i++) {
			String vals=this.composition.get(i).getSafety();
			if(!StringUtils.isBlank(vals)) {
				Integer val=Integer.parseInt(vals.split("-")[0]);
				if(val>=7&&val<=10) {
					safetys.put("7_10",safetys.get("7_10")+1); 
				}else if(val>=3&&val<=6) {
					safetys.put("3_6",safetys.get("3_6")+1);  
				} else {
					safetys.put("0_2",safetys.get("0_2")+1);
				}
			}
			
		}
		xinji="";
		//计算积分
		if(safetys.get("7_10")==0) {
			if(safetys.get("3_6")==0) {
				xinji="5";
			} else if(safetys.get("3_6")==1||safetys.get("3_6")==2) {
				xinji="4.5";
			} else if(safetys.get("3_6")>=3) {
				xinji="4";
			}
		} else if(safetys.get("7_10")==1) {
			 if(safetys.get("3_6")==0||safetys.get("3_6")==1||safetys.get("3_6")==2||safetys.get("3_6")==3) {
				xinji="4";
			} else if(safetys.get("3_6") >=4) {
				xinji="3.5";
			}
		}else if(safetys.get("7_10")==2) {// 2  1 11
			 if(safetys.get("3_6")==0||safetys.get("3_6")==1||safetys.get("3_6")==2||safetys.get("3_6")==3) {
					xinji="3.5";
				} else if(safetys.get("3_6")>=4) {
					xinji="3";
				}
		}else if(safetys.get("7_10")==3) {
				 if(safetys.get("3_6")<6) {
						xinji="3";
					} else if(safetys.get("3_6")>=6) {
						xinji="2.5";
					}
			}else if(safetys.get("7_10")==4) {
				 if(safetys.get("3_6")<6) {
						xinji="2.5";
					} else if(safetys.get("3_6")>=6) {
						xinji="2";
					}
			}else if(safetys.get("7_10")==5) {
				 if(safetys.get("3_6")<6) {
					 xinji="2";
					} else if(safetys.get("3_6")>=6) {
						xinji="1.5";
					}
			}else if(safetys.get("7_10")==6) {
				 if(safetys.get("3_6")<6) {
					 xinji="1.5";
				 } else if(safetys.get("3_6")>=6) {
						xinji="1";
				 }
			}else if(safetys.get("7_10")>=7) {
				xinji="1";
			}
		
		//高风险
		int h=this.safetys.get("7_10");
		//中等风险
		int m=this.safetys.get("3_6");
		//低风险
		int l=this.safetys.get("0_2");
		//安全值=（低风险成分数+中风险成分数*0.9+高风险成分数*0.7）／成分总数
		this.cpsSize=h+m+l;
		this.safe=(l+m*0.9+h*0.7)/this.cpsSize;
		
		
	}
	
	
	 /** 产品成分数低于20的时候，取a算法的星级；
	 * 产品成分数>=20且b算法与a算法的差值<1的时候，取b算法的星级，
	 * 产品成分数>=20且b算法与a算法的差值>=1，取a、b算法的平均值。
		平均值=round（算法a+算法b,0）/2 
	  */
	 public void safterAnalysis(){
		 if(null!=this.composition){
				/**
				 * 产品成分数低于20的时候，取a算法的星级；
				 * 产品成分数>=20且b算法与a算法的差值<1的时候，取b算法的星级，
				 * 产品成分数>=20且b算法与a算法的差值>=1，取a、b算法的平均值。
					平均值=round（算法a+算法b,0）/2 
				 */
				//int size=this.composition.size();
				//a算法
				safterAnalysis2();
				String oldXinji=this.xinji;
				if(this.cpsSize>=20){
					//b算法 计算星级
					proSafety(this.safe);
					//b算法的星级
					String bXinji=this.xinji;
					//System.out.println("oldXinji:"+oldXinji+"--bXinji:"+bXinji+"=====:"+Math.abs(Double.parseDouble(oldXinji)-Double.parseDouble(bXinji)));
					//b与a的星级的绝对值<1的时候，取b算法的星级
					if(Math.abs(Double.parseDouble(oldXinji)-Double.parseDouble(bXinji))<1){
						//b算法
						this.xinji=bXinji;
						
					}else if(Math.abs(Double.parseDouble(oldXinji)-Double.parseDouble(bXinji))>=1){
						//b与a的星级的绝对值>=1的时候，取a、b算法星级的平均值（算法a的星级+算法b的星级）/2
						double sum=Double.parseDouble(oldXinji)+Double.parseDouble(bXinji);
						//System.out.println(sum+"---");
						double avg=(double)Math.round(sum)/2;
						this.xinji=avg+"";
					}
					
				}
				//System.out.println(this.goods.getId()+"===:xinji:--"+this.xinji);
				Explain explain=new Explain();
				explain.setName("安全星级");
				explain.setUnit(1);
				explain.setId(1);
				explain.setNum(xinji);
				explain.setDisplayName("安全星级");
				NumberFormat numberFormat = NumberFormat.getInstance();
				// 设置精确到小数点后2位
				numberFormat.setMaximumFractionDigits(2);
				//百分比
				String precentSafety=numberFormat.format(Float.parseFloat(xinji)/Float.parseFloat(Explain.maxNum.get(explain.getId())+""));
				explain.setPercent(precentSafety);
				addSafety(explain);
				safty2();
			}
	}

 
	public void proSafety(double newSafe){
		//b算法
		if(newSafe<0.85){
			this.xinji="1";
		}else if(0.85<=newSafe && newSafe<0.9){
			this.xinji="1.5";
		}else if(0.9<=newSafe && newSafe<0.9125){
			this.xinji="2";
		}else if(0.9125<=newSafe && newSafe<0.925){
			this.xinji="2.5";
		}else if(0.925<=newSafe && newSafe<0.95){
			this.xinji="3";
		}else if(0.95<=newSafe && newSafe<0.97){
			this.xinji="3.5";
		}else if(0.97<=newSafe && newSafe<0.985){
			this.xinji="4";
		}else if(0.985<=newSafe && newSafe<0.9975){
			this.xinji="4.5";
		}else if(0.9975<=newSafe && newSafe<=1){
			this.xinji="5";
		}
	}
	
	/**
	 * 获取safetysql 
	 * @return
	 */
	public Map<String,Map<String,String>> createSafetySql() {
		List<Explain> eps=getSafety();
		String fileds="goods_id,";
		String vals=goods.getId()+",";
		int j=0;
		String updateStr="";
		for(Explain e:eps) {
			String filed_name="safety_"+e.getId()+"_name";
			String filed_name_val= e.getName();
			
			String filed_unit="safety_"+e.getId()+"_unit";
			int filed_unit_val=e.getUnit();
			
			String filed_num="safety_"+e.getId()+"_num";
			Float filed_num_val=0f;
			if(!StringUtils.isBlank(e.getNum())){
				filed_num_val= Float.parseFloat(e.getNum());
			}
			
			String filed_cps="safety_"+e.getId()+"_cps";
			String filed_cps_val="";
			String cpss="";
			if(null!=e.getCompositionIds() && e.getCompositionIds().size()>0){
				cpss=e.getCompositionIds().toString();
				cpss=cpss.substring(1);
				cpss=cpss.substring(0, cpss.length()-1);
				filed_cps_val= cpss;
			}else{
				filed_cps_val="";
			}
			
			fileds+=filed_name+","+filed_unit+","+filed_num+","+filed_cps;
			vals+="'"+filed_name_val+"'"+","+filed_unit_val+","+filed_num_val+","+"'"+filed_cps_val+"'";
			//set 
			updateStr+=filed_name+"="+"'"+filed_name_val+"'"+","+filed_unit+"="+filed_unit_val+","+filed_num+"="+filed_num_val+","+filed_cps+"="+"'"+filed_cps_val+"'";
			j++;
			if(j<eps.size()){
				fileds+=",";
    			vals+=","; 
    			updateStr+=",";
    		}
		}
		
		Map<String,Map<String,String>> sql=new HashMap<String,Map<String,String>>();
		Map<String,String> feilds1=new HashMap<String,String>();
		feilds1.put("fields", fileds);
		feilds1.put("vals", vals);
		Map<String,String> feilds2=new HashMap<String,String>();
		feilds2.put("fields", updateStr);
		sql.put("insert", feilds1);
		sql.put("update", feilds2);
		return sql;
	}
	
	/**
	 * 获取safetysql 
	 * @return
	 */
	public Map<String,Map<String,String>> createGoodsSkinSql(GoodsExplain goodsExplain) {
		List<Explain> eps=getSafety();
		String fileds="goods_id"+","+"mid"+","+"skin_match_num"+","+"skin_notmatch_num"+","+"skin_match_isexist"+","+"skin_match_cps"+","+"skin_notmatch_cps"+",";
		String vals="";
		int j=0;
		String updateStr="";
		for(Explain e:eps) {
			String filed_name="safety_"+e.getId()+"_name";
			String filed_name_val= e.getName();
			
			String filed_unit="safety_"+e.getId()+"_unit";
			int filed_unit_val=e.getUnit();
			
			String filed_num="safety_"+e.getId()+"_num";
			Float filed_num_val=0f;
			if(!StringUtils.isBlank(e.getNum())){
				filed_num_val= Float.parseFloat(e.getNum());
			}
			
			String filed_cps="safety_"+e.getId()+"_cps";
			String filed_cps_val="";
			String cpss="";
			if(null!=e.getCompositionIds() && e.getCompositionIds().size()>0){
				cpss=e.getCompositionIds().toString();
				cpss=cpss.substring(1);
				cpss=cpss.substring(0, cpss.length()-1);
				filed_cps_val= cpss;
			}else{
				filed_cps_val="";
			}
			
			fileds+=filed_name+","+filed_unit+","+filed_num+","+filed_cps;
			vals+="'"+filed_name_val+"'"+","+filed_unit_val+","+filed_num_val+","+"'"+filed_cps_val+"'";
			//set 
			updateStr+=filed_name+"="+"'"+filed_name_val+"'"+","+filed_unit+"="+filed_unit_val+","+filed_num+"="+filed_num_val+","+filed_cps+"="+"'"+filed_cps_val+"'";
			j++;
			if(j<eps.size()){
				fileds+=",";
    			vals+=","; 
    			updateStr+=",";
    		}
		}
		
		Map<String,Map<String,String>> sql=new HashMap<String,Map<String,String>>();
		Map<String,String> feilds1=new HashMap<String,String>();
		feilds1.put("fields", fileds);
		feilds1.put("vals", vals);
		Map<String,String> feilds2=new HashMap<String,String>();
		feilds2.put("fields", updateStr);
		sql.put("insert", feilds1);
		sql.put("update", feilds2);
		return sql;
	}
	
	
	
	/**
	 * 安全配置信息
	 * @return
	 */
	public static List<SafetyConfig> safetyConfigs() {
		SafetyConfig scfg=new SafetyConfig();
		List<SafetyConfig> ls=new ArrayList<SafetyConfig>();
		scfg.setId(2);
		scfg.setName("香精");
		scfg.setCpsUsed("70");
		scfg.setDesc("essence");
		ls.add(scfg);
		scfg=new SafetyConfig();
		scfg.setId(3);
		scfg.setName("防腐剂");
		scfg.setCpsUsed("69");
		scfg.setDesc("preservative");
		ls.add(scfg);
		scfg=new SafetyConfig();
		scfg.setId(4);
		scfg.setName("风险成分");
		scfg.setHighSafety("7-10");
		scfg.setDesc("risk");
		ls.add(scfg);
		scfg=new SafetyConfig();
		scfg.setId(5);
		scfg.setName("孕妇慎用");
		scfg.setShenyong("Y");
		scfg.setDesc("pregnant");
		ls.add(scfg);
		return ls;
	}
	
	/**
	 * 计算防腐剂
	 * 香精
	 * 孕妇慎用
	 */
	public void safty2() {
		List<SafetyConfig> ls= safetyConfigs();
		for(int i=0;i<composition.size();i++) {
			Composition cpn=composition.get(i);
			for(SafetyConfig sc:ls) {
				String used=cpn.getUsed();
				if(!StringUtils.isBlank(used)) {
					String useds[]=used.split(",");
					
					if(!StringUtils.isBlank(sc.getCpsUsed())&& ArrayUtils.contains(useds, sc.getCpsUsed())) {
						
						sc.addCompositionId(cpn.getId());
						sc.addComposition(cpn);
					};
					//治痘风险
					if(!StringUtils.isBlank(sc.getHighSafety())&&!StringUtils.isBlank(cpn.getSafety())) {
						String hss[]=sc.getHighSafety().split("-");
						int min=Integer.parseInt(hss[0]);
						int max=Integer.parseInt(hss[1]);
						int cur=Integer.parseInt(cpn.getSafety().split("-")[0]);
						if(cur>=min&&cur<=max) {
							sc.addCompositionId(cpn.getId());
							sc.addComposition(cpn);
						}
							
					};
				}
				if(!StringUtils.isBlank(cpn.getShenyong())) {

				//孕妇慎用
				if(!StringUtils.isBlank(sc.getShenyong())&&sc.getShenyong().equals(cpn.getShenyong())) {
					sc.addCompositionId(cpn.getId());
					sc.addComposition(cpn);
				}
				}

			}
		}
		
		for(SafetyConfig scf:ls) {
			Explain explain=new Explain();
			explain.setId(scf.getId());
			explain.setCompositionIds(scf.getCompositionIds());
			explain.setName(scf.getName());
			explain.setDisplayName(scf.getName());
			explain.setComposition(scf.getComposition());
			explain.setDesc(scf.getDesc());
			Map map=Explain.maxNum;
			
			NumberFormat numberFormat = NumberFormat.getInstance();
			// 设置精确到小数点后2位
			numberFormat.setMaximumFractionDigits(2);
			//百分比
			String precent=numberFormat.format(Float.parseFloat(explain.getCompositionIds().size()+"")/Float.parseFloat(Explain.maxNum.get(explain.getId())+""));
			
			explain.setPercent(precent);
			
			addSafety(explain);
		}
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public List<Explain> getSafety() {
		return safety;
	}

	public void setSafety(List<Explain> safety) {
		this.safety = safety;
	}

	public List<Explain> getEffect() {
		return effect;
	}

	public void setEffect(List<Explain> effect) {
		this.effect = effect;
	}

	//功效分析
	public void effectAnalysis(List<GoodsEffectUsed> geus) {
		if(this.effect==null) this.effect=new ArrayList<Explain>();
		List<Explain> explains=new ArrayList<Explain>();
		//主要功效
		Map<Long,Composition> cps=new HashMap<Long,Composition>();
		List<Long> allcps=new ArrayList<Long>();
		//计算所有功效分成
			for(int i=0;i<geus.size();i++) {
				//分类
				setCategoryCateId(geus.get(i).getCategoryCateId());
				setCategoryCateName(geus.get(i).getCategoryCateName());

				Explain explain=new Explain();
				
				explain.setCompositionIds(geus.get(i).getCompositionIds());
				explain.setName(geus.get(i).getEffectName());
				
				explain.setDisplayName(geus.get(i).getDisplayName());
				explain.setDisplayType(geus.get(i).getDisplayType());
				explain.setDisplaySort(geus.get(i).getDisplaySort());

				explain.setId(geus.get(i).getId());
				explain.setEffectId(geus.get(i).getEffectId());
				explain.setEffectPid(geus.get(i).getEffectPid());
				explain.setEffectPidName(geus.get(i).getEffectPidName());
				explain.setDisplayCompareName(geus.get(i).getDisplayCompareName());
				explain.setDisplayCompareSort(geus.get(i).getDisplayCompareSort());
				explain.setDisplayCompare(geus.get(i).getDisplayCompare());
				explain.setDesc(geus.get(i).getDesc());
			
				explain.cpsUsedAs(geus.get(i).getCpsUsed(),geus.get(i).getCompositions());
				
				//非防嗮和清洁且成分数量为0的不显示
				if((getCategoryCateId()==3||getCategoryCateId()==2)||geus.get(i).getCompositionIds().size()>0) {
					explains.add(explain);
				}else if(getCategoryCateId()==0){
					explains.add(explain);
				}

				List<Composition> cpss=explain.getComposition();
				
				//获取全成分
				if(cpss!=null) {
					for(Composition c:cpss) {
						cps.put(c.getId(), c);
					}
				}
				allcps.addAll(explain.getCompositionIds());
				
 				//添加分类
			}
			//allcps排重
			Map<Long,Long> cpsu=new HashMap<Long,Long>();
			List<Long> allcpsu=new ArrayList<Long>();

			for(Long l:allcps) {
				if(cpsu.get(l)==null) {
					cpsu.put(l, l);
					allcpsu.add(l);
				}
			}
			allcps=allcpsu;
			
			//计算主要功效成分
			if(allcps.size()>0){
				Explain explain=new Explain();
				explain.setCompositionIds(allcps);
				
				explain.setDisplayName("主要功效成分");
				explain.setCompareName("功效成分");
				for(Long l:cps.keySet()) {
					explain.addComposition(cps.get(l));
				}

				//防嗮和清洁不显示
				if(getCategoryCateId()==3||getCategoryCateId()==2) {
					explain.setDisplayType(2);
				} else {
					explain.setDisplayType(0);
				}
				//主要功效成分
				explain.setId(-1);
				explain.setEffectId(-1);
				explains.add(0, explain);
			}
			setEffect(explains);
	}
	
	public void addSafety(Explain geus) {
		if(this.safety==null) this.safety=new ArrayList<Explain>();
		safety.add(geus);
	}
 
 

	public String getCategoryCateName() {
		return categoryCateName;
	}

	public void setCategoryCateName(String categoryCateName) {
		this.categoryCateName = categoryCateName;
	}

	public Integer getCategoryCateId() {
		return categoryCateId;
	}

	public void setCategoryCateId(Integer categoryCateId) {
		this.categoryCateId = categoryCateId;
	}

	public void skinAnalysis(String result) throws Exception {
		if(!StringUtils.isBlank(result)) {
			if(goods.getCategory() == 27 || goods.getCategory() == 20 || goods.getCategory() == 19 || goods.getCategory() == 18 || goods.getCategory() == 16 || goods.getCategory() == 15 || goods.getCategory() == 14 || goods.getCategory() == 13 || goods.getCategory() == 11 || goods.getCategory() == 10 || goods.getCategory() == 9 || goods.getCategory() == 8 || goods.getCategory() == 7) {
				this.suit=new Explain();
				this.suit.setName("适合我的肤质");
				this.suit.setDesc("skin_match");
				
				this.noSuit=new Explain();
				this.noSuit.setName("不适合我的肤质");
				this.noSuit.setDesc("skin_match");

				for(int i=0;i<getComposition().size();i++) {
					Composition composition=getComposition().get(i);
	 				String skin= BeanUtils.getProperty(composition, result);
	 				if(!StringUtils.isBlank(skin)) {
		 				if(skin.equals("Y")) {
		 					this.suit.addCompositionId(composition.getId());
		 				} else if(skin.equals("N")) {
		 					this.noSuit.addCompositionId(composition.getId());
		 				}
	 				}
				}

			}

		}
	}

	public Explain getSuit() {
		return suit;
	}

	public void setSuit(Explain suit) {
		this.suit = suit;
	}

	public Explain getNoSuit() {
		return noSuit;
	}

	public void setNoSuit(Explain noSuit) {
		this.noSuit = noSuit;
	}
	/**
	 * app功效显示说明
	 * @return
	 */
	public String getAppCategoryCateText() {
		Map<String,String> info=ConfUtils.getMap("goods_info", "skin_goods_category_desc");
		if(categoryCateId!=null) {
			/*if(categoryCateId==3) {
				appCategoryCateText="防晒解读";
			} else if(categoryCateId!=null&&categoryCateId==2) {
				appCategoryCateText="清洁解读";
			} else {
				appCategoryCateText="功效说";
			}*/
			appCategoryCateText=info.get(categoryCateId+"");
			if(appCategoryCateText==null) {
				 appCategoryCateText=info.get("def");
			}
		}else {
			 appCategoryCateText=info.get("def");

		}
		return appCategoryCateText;
	}

	public void setAppCategoryCateText(String appCategoryCateText) {
		this.appCategoryCateText = appCategoryCateText;
	}
	
	
	public String getCpsTypeDesc() {
		if(this.goods!=null) {
			Map<String,String> info= ConfUtils.getMap("goods_info", "cps_sort_desc");
			String val=info.get(this.goods.getCpsType());
			if(!StringUtils.isBlank(val)) {
				cpsTypeDesc=val;
			} else {
				cpsTypeDesc=info.get("def");
			}
		}
		return cpsTypeDesc;
	}

	public void setCpsTypeDesc(String cpsTypeDesc) {
		this.cpsTypeDesc = cpsTypeDesc;
	}

	

}