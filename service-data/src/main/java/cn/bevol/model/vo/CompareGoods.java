package cn.bevol.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.model.user.UserInfo;
import cn.bevol.mybatis.model.Composition;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.mybatis.model.GoodsUsedEffect;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CompareGoods implements Serializable  {

	private String title;;
	
	private String mid;
	
	/**
	 * 1、产品
	 * 2、成分
	 * 4、货币
	 * 5、分隔
	 * 6、图片
	 * 7、星级
	 */
	private Integer type;
	
 	private List<CompareGoodsItem> compareGoodsItem;

 	//用于临时排序
 	private Integer index;
 	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

 	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
 	 * 添加属性
 	 * @param title
 	 * @param val
 	 * @return
 	 */
	public static CompareGoods addCompareGoods(String title,String... val) {
		List<CompareGoodsItem>  cgi=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle(title);
		cg.setCompareGoodsItem(cgi);
		for(int i=0;i<val.length;i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			c.setVal(val[i]);
			cgi.add(c);
		}
		return cg;
	}

	public List<CompareGoodsItem> getCompareGoodsItem() {
		return compareGoodsItem;
	}

	public void setCompareGoodsItem(List<CompareGoodsItem> compareGoodsItem) {
		this.compareGoodsItem = compareGoodsItem;
	}
	
	
	public final static String COMPARE_DEFAULT_VAL="-";
	/**
	 * 添加产品名称
	 * @param goods
	 * @return
	 */
	public static CompareGoods addTitle(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("产品名称");
		cg.setType(1);
		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			c.setVal(goods.get(i).getTitle());
			c.setType(1);
			c.setTypeId(goods.get(i).getId());
			c.setMid(goods.get(i).getMid());
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}
	
	/**
	 * 添加产品名称
	 * @param goods
	 * @return
	 */
	public static CompareGoods addCompanyEnglish(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("英文名称");
		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			if(!StringUtils.isBlank(goods.get(i).getAlias())) {
				c.setVal(goods.get(i).getAlias());
			} else {
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs); 
		return cg;
	}
	/** 
	 * 添加产品图片
	 * @param goods
	 * @return
	 */
	public static CompareGoods addImage(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("产品图片");
		cg.setType(6);
		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			if(!StringUtils.isBlank(goods.get(i).getImage())) {
				c.setVal(goods.get(i).getImage());
			}else {
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}

	/**
	 * 多个产品对比
	 * @param ges 需要对比的产品
	 * @param guef 功效特征显示项
	 * @param userInfo 用户
	 * @return
	 */
	public static List<CompareGoods> compareAnalysis(List<GoodsExplain> ges,List<GoodsUsedEffect>  guef,UserInfo userInfo) {
		List<CompareGoods> cgs=new ArrayList<CompareGoods>();
		
		List<Goods> goodss=new ArrayList<Goods>();
		List<List<Composition>> compositions=new ArrayList<List<Composition>>();
		List<List<Explain>> effecs=new ArrayList<List<Explain>>();
		
		//适合我的肤质
		List<Explain> suit=new ArrayList<Explain>();
		//不适合我的肤质
		List<Explain> noSuit=new ArrayList<Explain>();
		//安全风险(香精,防腐剂...)
		List<List<Explain>> safety=new ArrayList<List<Explain>>();
		
		//总成分
		List<List<Explain>> sumNum=new ArrayList<List<Explain>>();
		
		//addTitle
		for(int i=0;i<ges.size();i++) {
			goodss.add(ges.get(i).getGoods());
			suit.add(ges.get(i).getSuit());
			noSuit.add(ges.get(i).getNoSuit());
			safety.add(ges.get(i).getSafety());
			sumNum.add(ges.get(i).getEffect());
			compositions.add(ges.get(i).getComposition());
			effecs.add(ges.get(i).getEffect());

		}

 		
		//添加标题
		cgs.add(addTitle(goodss));
		//添加英文名称
		cgs.add(addCompanyEnglish(goodss));
		//添加图片
		cgs.add(addImage(goodss));
		
		CompareGoods jbxx=new CompareGoods();
		jbxx.setTitle("基本信息");
		jbxx.setType(5);
		
		cgs.add(jbxx);
		//2.基本信息
		//生产商
		cgs.add(addCompany(goodss));
		//容量
		cgs.add(addCapacity(goodss));
		//售价
		cgs.add(addPrice(goodss));
		//售价对应容量
		cgs.add(addSellCapacity(goodss));
		
		CompareGoods anfx=new CompareGoods();
		anfx.setTitle("安全风险");
		anfx.setType(5);
		
		cgs.add(anfx);

		//3.安全风险
		//香料香精 防腐剂  星级???
		cgs.addAll(addSafety(safety,compositions));
		
		CompareGoods gxcf=new CompareGoods();
		gxcf.setTitle("功效成分");
		gxcf.setType(5);
		cgs.add(gxcf);
		
		//功效成分数量
		cgs.addAll(addEffectDetail(guef,effecs,compositions));
		
		
		if(userInfo!=null&&userInfo.getId()>0) {
	 		//5.肤质匹配
			//适合我肤质的成分
			CompareGoods fzpp=new CompareGoods();
			fzpp.setTitle("肤质匹配");
			fzpp.setType(5);
			cgs.add(fzpp);
			
			cgs.add(addSuit(suit,compositions));
			//不适合我肤质的成分
			cgs.add(addNoSuit(noSuit,compositions));
		}


		return cgs;
	}
	
 	
	/**
	 * 多个产品对比
	 * @param ges 需要对比的产品
	 * @param guef 功效特征显示项
	 * @param userInfo 用户
	 * @return
	 */
	public static List<CompareGoods> compareAnalysis2(List<GoodsExplain> ges,List<GoodsUsedEffect>  guef,UserInfo userInfo) {
		List<CompareGoods> cgs=new ArrayList<CompareGoods>();
		
		
		List<List<Composition>> compositions=new ArrayList<List<Composition>>();
		List<List<Explain>> effecs=new ArrayList<List<Explain>>();
		//List<Goods> goodss=new ArrayList<Goods>();
		//总成分
		//List<List<Explain>> sumNum=new ArrayList<List<Explain>>();
		
		//addTitle
		for(int i=0;i<ges.size();i++) {
			/*goodss.add(ges.get(i).getGoods());
			sumNum.add(ges.get(i).getEffect());*/
			compositions.add(ges.get(i).getComposition());
			effecs.add(ges.get(i).getEffect());

		}
 		
		CompareGoods gxcf=new CompareGoods();
		gxcf.setTitle("功效成分");
		gxcf.setType(5);
		cgs.add(gxcf);
		
		//功效成分数量
		cgs.addAll(addEffectDetail(guef,effecs,compositions));

		return cgs;
	}
	
	/**
	 * 添加功效详细
	 * @param guef 
	 * @param effecs
	 * @return
	 */
	public static List<CompareGoods> addEffectDetail(List<GoodsUsedEffect> guefs, List<List<Explain>> effecs,List<List<Composition>> compositions) {
		//功效成分数量
		List<Explain> explains=new ArrayList<Explain>();
		Map<Integer,CompareGoods> exs=new HashMap<Integer,CompareGoods>();
		
		//主要功效成分
		CompareGoods effcgs=new CompareGoods();
		effcgs.setTitle("功效成分");
		
		for(int i=0;i<effecs.size();i++) {
				List<Explain> exps=effecs.get(i);
				if(null==exps || exps.size()==0){
					//主要功效成分
					effcgs.addCompareGoodsItemVal(0,"种");
				}
				for(int j=0;j<exps.size();j++) {
					//添加索引用于对比
					Explain explain=exps.get(j);
					//在对比中显示条件
					if(explain.getId()>0&&null!=explain.getDisplayCompare()&&explain.getDisplayCompare()==1&&(explain.getDisplayType()==0||explain.getDisplayType()==2)) {
						//主要功效成分
						exps.get(j).setIndex(i);
						Integer eid=exps.get(j).getEffectId();
						CompareGoods cp=exs.get(eid);
						List<CompareGoodsItem> cgis=null;
						if(cp==null) {
							cp=new CompareGoods();
							cp.setIndex(explain.getDisplayCompareSort());
							//记录数据排序
							if(!StringUtils.isBlank(explain.getCompareName()))
								cp.setTitle(explain.getCompareName());
							else 
								cp.setTitle(explain.getDisplayName());
							//添加前几个值为空
							cgis=new ArrayList<CompareGoodsItem>();
							cp.setCompareGoodsItem(cgis);
							for(int n=0;n<effecs.size();n++){
		 						CompareGoodsItem e=new CompareGoodsItem();
		 						e.setVal(COMPARE_DEFAULT_VAL);
								e.setType(2);
		 						cgis.add(e);
							}
							exs.put(eid, cp);
							cp.setType(2);
							
						}  
							cgis=cp.getCompareGoodsItem();
	 						CompareGoodsItem e=new CompareGoodsItem();
	 						//可以显示成分
							cgis.set(i, e);
							//成分数量
							if(explain.getCompositionIds().size()>0) {
								e.setVal(explain.getNum()+"");
							}
							//获取成分
							List<Composition> allcps=getCpsList(compositions.get(i),explain.getCompositionIds());
							e.setCompostion(allcps);
							//添加值
							explains.add(exps.get(j));
					}  else if(explain.getId()==-1){
						//主要功效成分
 						effcgs.addCompareGoodsItemVal(explain.getCompositionIds().size(),"种");
 					}
				}
		}

		//主要功效成分
 		
		
		List<CompareGoods>  czgs=new ArrayList<CompareGoods>();
		
		//全部成分
		
		
		CompareGoods cg=new CompareGoods();
		cg.setTitle("总成分");
		
		for(int i=0;i<compositions.size();i++) {
			cg.addCompareGoodsItemVal(compositions.get(i).size(), "种");
		}
		czgs.add(cg);
		
		if(effcgs.getCompareGoodsItem()==null) {
				effcgs.addCompareGoodsItemVal(0,"");
		}
		//主要功效成分
		czgs.add(effcgs);
		
		//
		List<CompareGoods> srts=new ArrayList<CompareGoods>();
		for(GoodsUsedEffect g: guefs) {
			CompareGoods c=exs.get(g.getId());
			if(c==null) {
				c=new CompareGoods();
				c.setTitle(g.getDisplayCompareName());
				c.createDefValItem(effecs.size(), "");
			}
			srts.add(c);
		}
		czgs.addAll(srts);
   		return czgs;
	}

	public void createDefValItem(int size,String ext) {
	    for(int i=0;i<size;i++) {
	    	addCompareGoodsItemVal(0,ext);
	    }
	}
	
	/**
	 * 添加显示项目
	 * @param cgi
	 */
	public void addCompareGoodsItem(CompareGoodsItem cgi) {
		if(this.compareGoodsItem==null) this.compareGoodsItem=new ArrayList<CompareGoodsItem>();
		this.compareGoodsItem.add(cgi);
	}
	/**
	 * 
	 * @param num 数字
	 * @param ext 单位
	 */
	public void addCompareGoodsItemVal(Integer num,String ext) {
		if(this.compareGoodsItem==null) this.compareGoodsItem=new ArrayList<CompareGoodsItem>();
		CompareGoodsItem cgi=new CompareGoodsItem();
		if(num!= null&&num>0) {
			cgi.setVal(num+""+ext);
		} else {
			cgi.setVal(COMPARE_DEFAULT_VAL);
		}
		addCompareGoodsItem(cgi);
	}


 	/**
	 * 添加产品生产商
	 * @param goods
	 * @return
	 */
	public static CompareGoods addCompany(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("生产商");
		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			if(!StringUtils.isBlank(goods.get(i).getCompany())) {
				c.setVal(goods.get(i).getCompany());
			} else {
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}
	
	/**
	 * 添加产品容量
	 * @param goods
	 * @return
	 */
	public  static CompareGoods addCapacity(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("容量");
		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			if(!StringUtils.isBlank(goods.get(i).getCapacity())) {
				String capatity=goods.get(i).getCapacity().trim();
				if(capatity.lastIndexOf("ml")!=-1 || capatity.lastIndexOf("g")!=-1){
					c.setVal(goods.get(i).getCapacity());
				} else{
					c.setVal(COMPARE_DEFAULT_VAL);
				}
			} else {
				c.setVal(COMPARE_DEFAULT_VAL);
			}

			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}
	
	
	/**
	 * 添加产品价格
	 * @param goods
	 * @return
	 */
	public static CompareGoods addPrice(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("价格");
		cg.setType(4);
		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			if(!StringUtils.isBlank(goods.get(i).getPrice())&&!goods.get(i).getPrice().equals("0.0")&&!goods.get(i).getPrice().equals("0")) {
				c.setVal("￥"+goods.get(i).getPrice()+"");
			}else {
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}

	
	/**
	 * 添加产品售价对应容量
	 * @param goods
	 * @return
	 */
	public static CompareGoods addSellCapacity(List<Goods> goods) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("100ml/100g价格");
		cg.setType(4);
 		for(int i=0;i<goods.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			if(!StringUtils.isBlank(goods.get(i).getCapacity())&&!goods.get(i).getCapacity().equals("0.0")&&!goods.get(i).getCapacity().equals("0")&&
					!StringUtils.isBlank(goods.get(i).getPrice())&&!goods.get(i).getPrice().equals("0.0")&&!goods.get(i).getPrice().equals("0")) {
				String capatity=goods.get(i).getCapacity().trim();
				if(capatity.lastIndexOf("ml")!=-1){
						try{
							float Icapatity=Float.parseFloat(StringUtils.substringBeforeLast(capatity,"ml"));
							float p=Float.parseFloat(goods.get(i).getPrice().trim());
							/*c.setVal("￥"+goods.get(i).getPrice());
							p=Integer.parseInt(goods.get(i).getPrice().trim());
							if(Icapatity>100){
								k=Icapatity/100;  //100ml/price
								c.setVal("￥"+(double)Math.round(p/k*10)/10);
							}else if(Icapatity<100){
								k=100/Icapatity;
								c.setVal("￥"+(double)Math.round(p*k*10)/10);
							}*/
							c.setVal("￥"+Math.round(p/Icapatity*100));
						}catch(Exception e){
							c.setVal(COMPARE_DEFAULT_VAL);
						}
					}else if(capatity.lastIndexOf("g")!=-1){
						float Icapatity=Float.parseFloat(StringUtils.substringBeforeLast(capatity,"g"));
						try{
							//c.setVal("￥"+goods.get(i).getPrice());
							float p=Float.parseFloat(goods.get(i).getPrice().trim());
							c.setVal("￥"+Math.round(p/Icapatity*100));
							/*if(Icapatity>100){
								k=Icapatity/100;  //100ml/price
								c.setVal("￥"+(double)Math.round(p/k*10)/10);
							}else if(Icapatity<100){
								k=100/Icapatity;
								c.setVal("￥"+(double)Math.round(p*k*10)/10);
							}*/
						}catch(Exception e){
							c.setVal(COMPARE_DEFAULT_VAL);
						}
					}
			} else {
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}
	
	
	/**
	 * 添加产品 适合我肤质的成分
	 * @param goods
	 * @return
	 */
	public static CompareGoods addSuit(List<Explain> suit,List<List<Composition>> compositions) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("适合我肤质的成分");
		cg.setType(2);
		for(int i=0;i<suit.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			//可以显示成分
			c.setType(2);
			if(suit.get(i)!=null&&suit.get(i).getCompositionIds().size()>0) {
				List<Composition> cps=getCpsList(compositions.get(i),suit.get(i).getCompositionIds());
				c.setCompostion(cps);
				c.setVal(cps.size()+"");
			} else {
				c.setCompostion(new ArrayList());
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}
	
	/**
	 * 根据成分ids获取成分对象
	 * @param compositions
	 * @param cpsIds
	 * @return
	 */
	private static List<Composition> getCpsList(List<Composition> compositions,List<Long> cpsIds) {
		List<Composition> cpns=new ArrayList<Composition>();
		//用于排除重复成分
		Map<Long,Long> s=new HashMap<Long,Long>();
		if(cpsIds.size()>0) {
			for(Composition c:compositions) {
				for(int m=0;m<cpsIds.size();m++) {
					if(c.getId()==cpsIds.get(m)&&s.get(c.getId())==null) {
						s.put(c.getId(), c.getId());
						Composition cn=new Composition();
						cn.setId(c.getId());
						cn.setTitle(c.getTitle());
						cpns.add(cn);
					}
				}
			}
		}
		return cpns;
	}
	/**
	 * 添加产品 不适合我肤质的成分
	 * @param goods
	 * @return
	 */
	public static CompareGoods addNoSuit(List<Explain> noSuit,List<List<Composition>> compositions) {
		List<CompareGoodsItem> cgs=new ArrayList<CompareGoodsItem>();
		CompareGoods cg=new CompareGoods();
		cg.setTitle("不适合我肤质的成分");
		cg.setType(2);
		for(int i=0;i<noSuit.size();i++) {
			CompareGoodsItem c=new CompareGoodsItem();
			c.setType(2);
			if(noSuit.get(i)!=null&&noSuit.get(i).getCompositionIds().size()>0) {
				List<Composition> cps=getCpsList(compositions.get(i),noSuit.get(i).getCompositionIds());
				c.setCompostion(cps);
				c.setVal(cps.size()+"");
			} else {
				c.setCompostion(new ArrayList());
				
				c.setVal(COMPARE_DEFAULT_VAL);
			}
			cgs.add(c);
		}
		cg.setCompareGoodsItem(cgs);
		return cg;
	}
	
	

	/**
	 * 添加产品  安全风险 香料香精 防腐剂
	 * @param goods
	 * @return
	 */
	public static List<CompareGoods> addSafety(List<List<Explain>> safetyList,List<List<Composition>> compositions) {
		try {
			Map<Integer,CompareGoods> exs=new TreeMap<Integer,CompareGoods>();
			int j=0;
			for(List<Explain> elist:safetyList){
				for(int i=0;i<elist.size();i++) {
					Explain explain=elist.get(i);
					Integer eid=explain.getId();
					CompareGoods cp=exs.get(eid);
					List<CompareGoodsItem> cgis=null;
					if(cp==null) {
						cp=new CompareGoods();
						cp.setTitle(explain.getDisplayName());
						//添加前几个值为空
						cgis=new ArrayList<CompareGoodsItem>();
						cp.setCompareGoodsItem(cgis);
	 					exs.put(eid, cp);
	 					//可显示成分
	 					if(explain.getUnit()==1) {
		 					cp.setType(7);
	 					} else if(explain.getUnit()==0){
		 					cp.setType(2);
	 					}
	 						
	 						
					}  
					cp.setTitle(explain.getDisplayName());
					cgis=cp.getCompareGoodsItem();
					
					CompareGoodsItem e=new CompareGoodsItem();
					
					//成分数量计算  安全星级排除在外
					if(explain.getCompositionIds().size()==0&&explain.getUnit()==0) {
						e.setVal(COMPARE_DEFAULT_VAL);
					}else {
						e.setVal(explain.getNum());
					}
					
					e.setUnit(explain.getUnit());
 					cgis.add(e);
 					
 					//添加成分
					List<Composition> allcps=getCpsList(compositions.get(j),explain.getCompositionIds());
					e.setCompostion(allcps);


	 			}
				j++;
			}
			
			List<CompareGoods>  czgs=new ArrayList<CompareGoods>();
			for(Integer key:exs.keySet()){
				czgs.add(exs.get(key));
			}
			return czgs;

		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
	
	
	
}
