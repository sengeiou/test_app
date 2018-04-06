package cn.bevol.internal.service.goodsCalculate;


import cn.bevol.internal.entity.model.Composition;
import cn.bevol.internal.entity.model.Goods;
import cn.bevol.internal.entity.vo.Explain;
import cn.bevol.internal.entity.vo.GoodsExplain;
import cn.bevol.internal.entity.vo.SafetyConfig;
import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * 肤质计算
 * @author Administrator
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsSafetyCalculateHandler implements GoodsCalculateI {

	private GoodsExplain goodsExplain;
	
	private List<Composition> compositions;

	private Goods goods;
	
	private String updateSql;
	
	private String insertSql;
	
	private String selectSql;
	
	private Map<String,Integer> safety;

	private String xinji;

	private double safe;

	/**
	 * 含有风险的成分的数量
	 */
	private int cpsSize;

	public GoodsSafetyCalculateHandler(GoodsExplain goodsExplain) {
		this.goodsExplain = goodsExplain;
		this.goods=goodsExplain.getGoods();
		this.compositions=goodsExplain.getComposition();
	}
	
	@Override
	public void handler() {
		//计算肤质参数
		createSafetySql();
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
		ls.add(scfg);
		scfg=new SafetyConfig();
		scfg.setId(3);
		scfg.setName("防腐剂");
		scfg.setCpsUsed("69");
		ls.add(scfg);
		scfg=new SafetyConfig();
		scfg.setId(4);
		scfg.setName("风险成分");
		scfg.setHighSafety("7-10");
		ls.add(scfg);
		scfg=new SafetyConfig();
		scfg.setId(5);
		scfg.setName("孕妇慎用");
		scfg.setShenyong("Y");
		ls.add(scfg);
		return ls;
	}
	
	public void proccSafterAnalysis(){
		if(null!=this.compositions){
			/**
			 * 产品成分数低于20的时候，取a算法的星级；
			 * 产品成分数>=20且b算法与a算法的差值<1的时候，取b算法的星级，
			 * 产品成分数>=20且b算法与a算法的差值>=1，取a、b算法的平均值。
				平均值=round（算法a+算法b,0）/2
			 */
			//int size=this.compositions.size();
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
				if(Math.abs(Double.parseDouble(oldXinji)- Double.parseDouble(bXinji))<1){
					//b算法
					this.xinji=bXinji;

				}else if(Math.abs(Double.parseDouble(oldXinji)- Double.parseDouble(bXinji))>=1){
					//b与a的星级的绝对值>=1的时候，取a、b算法星级的平均值（算法a的星级+算法b的星级）/2
					double sum= Double.parseDouble(oldXinji)+ Double.parseDouble(bXinji);
					//System.out.println(sum+"---");
					double avg=(double) Math.round(sum)/2;
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

 	//安全分析
	public void safterAnalysis() {
		//星级
		 Map<String,Integer> safety=new HashMap<String,Integer>();
			safety.put("7_10", 0);
			safety.put("3_6", 0);
			safety.put("0_2", 0);
		for(int i=0;i<this.compositions.size();i++) {
			String vals=this.compositions.get(i).getSafety();
			if(!StringUtils.isBlank(vals)) {
				Integer val= Integer.parseInt(vals.split("-")[0]);
				if(val>=7&&val<=10) {
					safety.put("7_10",safety.get("7_10")+1); 
				}else if(val>=3&&val<=6) {
					safety.put("3_6",safety.get("3_6")+1);  
				} else {
					safety.put("0_2",safety.get("0_2")+1);
				}
			}
			
		}
		String xinji="";
		//计算积分
		if(safety.get("7_10")==0) {
			if(safety.get("3_6")==0) {
				xinji="5";
			} else if(safety.get("3_6")==1||safety.get("3_6")==2) {
				xinji="4.5";
			} else if(safety.get("3_6")>=3) {
				xinji="4";
			}
		} else if(safety.get("7_10")==1) {
			 if(safety.get("3_6")==0||safety.get("3_6")==1||safety.get("3_6")==2||safety.get("3_6")==3) {
				xinji="4";
			} else if(safety.get("3_6") >=4) {
				xinji="3.5";
			}
		}else if(safety.get("7_10")==2) {// 2  1 11
			 if(safety.get("3_6")==0||safety.get("3_6")==1||safety.get("3_6")==2||safety.get("3_6")==3) {
					xinji="3.5";
				} else if(safety.get("3_6")>=4) {
					xinji="3";
				}
		}else if(safety.get("7_10")==3) {
				 if(safety.get("3_6")<6) {
						xinji="3";
					} else if(safety.get("3_6")>=6) {
						xinji="2.5";
					}
			}else if(safety.get("7_10")==4) {
				 if(safety.get("3_6")<6) {
						xinji="2.5";
					} else if(safety.get("3_6")>=6) {
						xinji="2";
					}
			}else if(safety.get("7_10")==5) {
				 if(safety.get("3_6")<6) {
					 xinji="2";
					} else if(safety.get("3_6")>=6) {
						xinji="1.5";
					}
			}else if(safety.get("7_10")==6) {
				 if(safety.get("3_6")<6) {
					 xinji="1.5";
				 } else if(safety.get("3_6")>=6) {
						xinji="1";
				 }
			}else if(safety.get("7_10")>=7) {
				xinji="1";
			}
		
		Explain explain=new Explain();
		explain.setName("安全星级");
		explain.setUnit(1);
		explain.setId(1);
		explain.setNum(xinji);
		explain.setDisplayName("安全星级");
		addSafety(explain);
		safty2(); 
	}
	
	//安全分析
	public void safterAnalysis2() {
		//星级
		safety=new HashMap<String,Integer>();
			safety.put("7_10", 0);
			safety.put("3_6", 0);
			safety.put("0_2", 0);
		for(int i=0;i<this.compositions.size();i++) {
			String vals=this.compositions.get(i).getSafety();
			if(!StringUtils.isBlank(vals)) {
				Integer val= Integer.parseInt(vals.split("-")[0]);
				if(val>=7&&val<=10) {
					safety.put("7_10",safety.get("7_10")+1);
				}else if(val>=3&&val<=6) {
					safety.put("3_6",safety.get("3_6")+1);
				} else {
					safety.put("0_2",safety.get("0_2")+1);
				}
			}

		}
		xinji="";
		//计算积分
		if(safety.get("7_10")==0) {
			if(safety.get("3_6")==0) {
				xinji="5";
			} else if(safety.get("3_6")==1||safety.get("3_6")==2) {
				xinji="4.5";
			} else if(safety.get("3_6")>=3) {
				xinji="4";
			}
		} else if(safety.get("7_10")==1) {
			 if(safety.get("3_6")==0||safety.get("3_6")==1||safety.get("3_6")==2||safety.get("3_6")==3) {
				xinji="4";
			} else if(safety.get("3_6") >=4) {
				xinji="3.5";
			}
		}else if(safety.get("7_10")==2) {// 2  1 11
			 if(safety.get("3_6")==0||safety.get("3_6")==1||safety.get("3_6")==2||safety.get("3_6")==3) {
					xinji="3.5";
				} else if(safety.get("3_6")>=4) {
					xinji="3";
				}
		}else if(safety.get("7_10")==3) {
				 if(safety.get("3_6")<6) {
						xinji="3";
					} else if(safety.get("3_6")>=6) {
						xinji="2.5";
					}
			}else if(safety.get("7_10")==4) {
				 if(safety.get("3_6")<6) {
						xinji="2.5";
					} else if(safety.get("3_6")>=6) {
						xinji="2";
					}
			}else if(safety.get("7_10")==5) {
				 if(safety.get("3_6")<6) {
					 xinji="2";
					} else if(safety.get("3_6")>=6) {
						xinji="1.5";
					}
			}else if(safety.get("7_10")==6) {
				 if(safety.get("3_6")<6) {
					 xinji="1.5";
				 } else if(safety.get("3_6")>=6) {
						xinji="1";
				 }
			}else if(safety.get("7_10")>=7) {
				xinji="1";
			}

		//高风险
		int h=this.safety.get("7_10");
		//中等风险
		int m=this.safety.get("3_6");
		//低风险
		int l=this.safety.get("0_2");
		//安全值=（低风险成分数+中风险成分数*0.9+高风险成分数*0.7）／成分总数
		this.cpsSize=h+m+l;
		this.safe=(l+m*0.9+h*0.7)/this.cpsSize;


		/*Explain explain=new Explain();
		explain.setName("安全星级");
		explain.setUnit(1);
		explain.setId(1);
		explain.setNum(xinji);
		explain.setDisplayName("安全星级");
		addSafety(explain);
		safty2(); */
	}


	/**
	 * 计算防腐剂
	 * 香精
	 * 孕妇慎用
	 */
	public void safty2() {
		List<SafetyConfig> ls= safetyConfigs();
		for(int i=0;i<compositions.size();i++) {
			Composition cpn=compositions.get(i);
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
						int min= Integer.parseInt(hss[0]);
						int max= Integer.parseInt(hss[1]);
						int cur= Integer.parseInt(cpn.getSafety().split("-")[0]);
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

			addSafety(explain);
		}
	}

	public void addSafety(Explain geus) {
		if(goodsExplain.getSafety()==null) goodsExplain.setSafety(new ArrayList<Explain>());;
		goodsExplain.getSafety().add(geus);
	}
 

	/**
	 * 只有 更新或者插入的时候需要计算 生成  update insert 
	 * 获取safetysql 
	 * @return
	 */
	public void createSafetySql() {
		//safterAnalysis();
		//解析星级
		proccSafterAnalysis();
		float safety_1_num=0;
		List<Explain> eps=this.goodsExplain.getSafety();
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
			
			if(e.getId()==1){
				safety_1_num=filed_num_val;
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
		updateStr+=",update_time="+ DateUtils.nowInMillis()/1000;
		fileds+=",update_time";
		vals+=","+DateUtils.nowInMillis()/1000;
		this.updateSql="update hq_goods_safter set "+updateStr+" where goods_id="+goods.getId();
		this.insertSql="insert into hq_goods_safter("+fileds+") "+"values("+vals+")";
	
	}

	@Override
	public String updaeSql() {
		return this.updateSql;
	}
	
	/**
	 * 查询的时候不需要计算
	 */
	@Override
	public String selectSql() {
		return this.selectSql="select * from hq_goods_safter where goods_id="+this.goods.getId();
	}

	@Override
	public String insertSql() {
		return this.insertSql;
	}

	@Override
	public void display(List<Map<String,Object>> maps) {
		goodsExplain.setSafety(null);
		for(Map map:maps){
			int size=(map.size()-2)/4;
			for(int i=1;i<=size;i++){
				Explain exp=new Explain();
				List<Long> cpsId=new ArrayList<Long>();
				String filed_name="safety_"+i+"_name";
				String name=(String)map.get(filed_name.trim());
				exp.setName(name);
				
				String filed_unit="safety_"+i+"_unit";
				int unit=(Integer)map.get(filed_unit.trim());
				exp.setUnit(unit);
				
				String filed_num="safety_"+i+"_num";
				Float num=(Float)map.get(filed_num.trim());
				exp.setNum(num.toString());
				
				String filed_cps="safety_"+i+"_cps";
				String cps=(String)map.get(filed_cps);
				if(!StringUtils.isBlank(cps)){
					String[] cpss=cps.split(",");
					if(null!=cpss && cpss.length>0){
						for(int j=0;j<cpss.length;j++){
							String c=cpss[j].trim();
							for(int n=0;n<compositions.size();n++) {
								if(compositions.get(n).getId()== Long.parseLong(c)) {
									exp.addComposition(compositions.get(n));
								}
							}
						}
					}
					
				}
				exp.setCompositionIds(cpsId);
				exp.setId(i);
				exp.setDisplayName(name);
				goodsExplain.addSafety(exp);
 			}
		}

	}
	
	@Override
	public Map entityInfo() {
		Map map=new HashMap();
		map.put("goodsId", this.goods.getId());
		map.put("xinji", this.xinji);
		return map;
	}


}
