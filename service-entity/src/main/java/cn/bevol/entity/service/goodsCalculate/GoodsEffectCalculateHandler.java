package cn.bevol.entity.service.goodsCalculate;

import cn.bevol.conf.client.ConfUtils;
import cn.bevol.mybatis.model.Composition;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.mybatis.model.GoodsEffectUsed;
import cn.bevol.model.vo.Explain;
import cn.bevol.model.vo.GoodsExplain;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功效
 * @author Administrator
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GoodsEffectCalculateHandler implements GoodsCalculateI {

	private GoodsExplain goodsExplain;
	  
	private List<Composition> compositions;

	private List<GoodsEffectUsed>  geus;
	
	private Goods goods;
	
	private String updateSql;
	
	private String insertSql;
	
	private String insertKeys;
	private String insertValues;

	private String selectSql;
	//产品大分类名称
	private String appCategoryCateText;
	
	public GoodsEffectCalculateHandler(GoodsExplain goodsExplain,List<GoodsEffectUsed>  geus) {
		this.goodsExplain = goodsExplain;
		this.goods=goodsExplain.getGoods();
		this.compositions=goodsExplain.getComposition();
		this.geus=geus;
	}
	
	@Override
	public void handler() {
		// TODO Auto-generated method stub
		effectAnalysis();
	}
	
	
	/**
	 * 产品功效
	 */
	public void  createSql() {
		try {
			//计算
			effectAnalysis();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void display(List<Map<String,Object>> maps) {
		//this.goodsExplain.getEffect();
	}

	//功效分析
	public void effectAnalysis() {
		if(this.goodsExplain.getEffect()==null || this.goodsExplain.getEffect().size()==0) this.goodsExplain.setEffect(new ArrayList<Explain>());
		List<Explain> explains=new ArrayList<Explain>();
		//主要功效
		Map<Long,Composition> cps=new HashMap<Long,Composition>();
		List<Long> allcps=new ArrayList<Long>();
		//计算所有功效分成
			for(int i=0;i<geus.size();i++) {
				//分类
				this.goodsExplain.setCategoryCateId(geus.get(i).getCategoryCateId());
				this.goodsExplain.setCategoryCateName(geus.get(i).getCategoryCateName());

				Explain explain=new Explain();
				//cps
				explain.setCompositionIds(geus.get(i).getCompositionIds());
				explain.setName(geus.get(i).getEffectName());
				
				explain.setDisplayName(geus.get(i).getDisplayName());
				explain.setDisplayType(geus.get(i).getDisplayType());
				explain.setDisplaySort(geus.get(i).getDisplaySort());

				explain.setId(geus.get(i).getId());
				explain.setEffectId(geus.get(i).getEffectId());
				//effect_id_num
				//effect_id_cps
				//effect_id_isexsit
				//effect_id_ecuid //分类关系对应的id 
				
				explain.setEffectPid(geus.get(i).getEffectPid());
				explain.setEffectPidName(geus.get(i).getEffectPidName());
				explain.setDisplayCompareName(geus.get(i).getDisplayCompareName());
				explain.setDisplayCompareSort(geus.get(i).getDisplayCompareSort());
				explain.setDisplayCompare(geus.get(i).getDisplayCompare());
				
			
				explain.cpsUsedAs(geus.get(i).getCpsUsed(),geus.get(i).getCompositions());
				
				//非防嗮和清洁且成分数量为0的不显示
				if((this.goodsExplain.getCategoryCateId()==3||this.goodsExplain.getCategoryCateId()==2)||geus.get(i).getCompositionIds().size()>0) {
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
				
				//大表  产品
				
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
				if(this.goodsExplain.getCategoryCateId()==3||this.goodsExplain.getCategoryCateId()==2) {
					explain.setDisplayType(2);
				} else {
					explain.setDisplayType(0);
				}
				//主要功效成分
				explain.setId(-1);
				explain.setEffectId(-1);
				explains.add(0, explain);
			}
			this.goodsExplain.setEffect(explains);
			
			getAppCategoryCateText();
	}
	
	
	/**
	 * app功效显示说明
	 * @return
	 */
	public String getAppCategoryCateText() {
		Map<String,String> info= ConfUtils.getMap("goods_info", "skin_goods_category_desc");
		if(this.goodsExplain.getCategoryCateId()!=null) {
			/*if(categoryCateId==3) {
				appCategoryCateText="防晒解读";
			} else if(categoryCateId!=null&&categoryCateId==2) {
				appCategoryCateText="清洁解读";
			} else {
				appCategoryCateText="功效说";
			}*/
			appCategoryCateText=info.get(this.goodsExplain.getCategoryCateId()+"");
			if(appCategoryCateText==null) {
				 appCategoryCateText=info.get("def");
			}
		}else {
			 appCategoryCateText=info.get("def");

		}
		this.goodsExplain.setAppCategoryCateText(appCategoryCateText);
		return appCategoryCateText;
	}

	
	@Override
	public String updaeSql() {
		return updateSql;
	}

	@Override
	public String selectSql() {
		return this.selectSql;
	}

	@Override
	public String insertSql() {
		// TODO Auto-generated method stub
		return this.insertSql;
	}

	@Override
	public Map entityInfo() {
		// TODO Auto-generated method stub
		return null;
	}

 }
