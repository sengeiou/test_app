package cn.bevol.app.service.goodsCalculate;

import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.app.entity.vo.Explain;
import cn.bevol.app.entity.vo.GoodsExplain;
import cn.bevol.app.service.GoodsService;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 肤质计算
 * @author Administrator
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsSkinCalculateHandler implements GoodsCalculateI {
	private static Logger logger = LoggerFactory.getLogger(GoodsService.class);

	private GoodsExplain goodsExplain;

	private List<Composition> compositions;

	private Goods goods;

	private String updateSql="";

	private String insertSql;

	private String insertKeys;
	private String insertValues;

	private String selectSql;

	/**
	 * 16种肤质 
	 */
	private List<Map<String,Object>> skins;

	public GoodsSkinCalculateHandler(GoodsExplain goodsExplain, List<Map<String,Object>>  skins) {
		this.goodsExplain = goodsExplain;
		this.goods=goodsExplain.getGoods();
		this.compositions=goodsExplain.getComposition();
		this.skins=skins;
	}

	@Override
	public void handler() {
		// TODO Auto-generated method stub
		//计算肤质参数
		allGoodsSkin();
	}


	public void skinAnalysis(String result) throws Exception {
		if(!StringUtils.isBlank(result)) {
			if(isSkinCalculate()) {
				Explain suit=new Explain();
				 suit.setName("适合我的肤质");
				 Explain noSuit=new Explain();
				  noSuit.setName("不适合我的肤质");
				  //适合我的
				  this.goodsExplain.setSuit(suit);
				  //不适合我的
				  this.goodsExplain.setNoSuit(noSuit);

				for(int i=0;i<compositions.size();i++) {
					Composition composition=compositions.get(i);
	 				String skin=BeanUtils.getProperty(composition, result);
	 				if(!StringUtils.isBlank(skin)) {
		 				if(skin.equals("Y")) {
		 					 suit.addCompositionId(composition.getId());
		 				} else if(skin.equals("N")) {
		 					 noSuit.addCompositionId(composition.getId());
		 				}
	 				}
				}
			}
			}
		}

	private boolean isSkinCalculate()  {
		if(goods.getCategory() == 27 || goods.getCategory() == 20 || goods.getCategory() == 19 || goods.getCategory() == 18 || goods.getCategory() == 16 || goods.getCategory() == 15 || goods.getCategory() == 14 || goods.getCategory() == 13 || goods.getCategory() == 11 || goods.getCategory() == 10 || goods.getCategory() == 9 || goods.getCategory() == 8 || goods.getCategory() == 7) {
			return true;
		}
		return false;
	}
	/**
	 * 计算产品肤质信息 sql(是否存在)
	 */
	public void  allGoodsSkin() {
		try {
			//属于分类类型  就计算
			if(isSkinCalculate()) {
				//skins  得到16中肤质
				List<Map<String,Object>> listMap=this.skins;
				List<String> valueList=new ArrayList<String>();
				for(Map map:listMap){
					valueList.add((String)map.get("key"));
				}
				long goodsId=this.goods.getId();
				String mid=this.goods.getMid();
				 insertKeys="goods_id"+","+"mid"+",";
				 insertValues=goodsId+",'"+mid+"'"+",";
				for(int i=0;i<valueList.size();i++) {
					Explain exp=new Explain();
					String skinType=valueList.get(i);
					//适合我的肤质
					List<Long> SuitCpsList=new ArrayList<Long>();
					List<Long> NoSuitCpsList=new ArrayList<Long>();
					String suitCps="";
					String noSuitCps="";
					//计算肤质
					skinAnalysis(skinType);

					exp=this.goodsExplain.getSuit();

					//不匹配是否存在
					int notmatchisexist=0;
					//匹配是否存在
					int matchisexist=0;
					//匹配的数量
					long suitNum=0L;
					long noSuitNum=0L;
					if(null!=exp){
						SuitCpsList=this.goodsExplain.getSuit().getCompositionIds();
						suitCps=SuitCpsList.toString();
						suitCps=suitCps.trim().substring(1, suitCps.length()-1);

						suitNum=SuitCpsList.size();
						if(suitNum>0){
							matchisexist=1;
						}
					}
					exp=this.goodsExplain.getNoSuit();
					if(null!=exp){
						//不适合我的肤质
						NoSuitCpsList=this.goodsExplain.getNoSuit().getCompositionIds();
						noSuitCps=NoSuitCpsList.toString();
						noSuitCps=noSuitCps.substring(1, noSuitCps.length()-1);

						//不匹配的数量
						noSuitNum=NoSuitCpsList.size();
						if(noSuitNum>0){
							notmatchisexist=1;
						}
					}

					//不管有没有 先给sql赋值
					updateSql+="skin_match_"+skinType+"_num="+suitNum+","+"skin_notmatch_"+skinType+"_num="+noSuitNum+","+"skin_match_"+skinType+"_isexist="+matchisexist+","+"skin_notmatch_"+skinType+"_isexist="+notmatchisexist+","+"skin_match_"+skinType+"_cps="+"'"+suitCps+"'"+","+"skin_notmatch_"+skinType+"_cps="+"'"+noSuitCps+"'"+",";
					String fields="skin_match_{SKIN}_num,skin_notmatch_{SKIN}_num,skin_match_{SKIN}_isexist,skin_notmatch_{SKIN}_isexist,skin_match_{SKIN}_cps,skin_notmatch_{SKIN}_cps";
					//遍历把skinType的值给{skin}  for
					insertKeys+=StringUtils.replaceEach(fields, new String[]{"{SKIN}"}, new String[]{skinType})+",";
					insertValues+=suitNum+","+noSuitNum+","+matchisexist+","+notmatchisexist+",'"+suitCps+"','"+noSuitCps+"'"+",";
				}
				//赋值updaeSql()
				updateSql+="update_time="+new Date().getTime()/1000;
				insertKeys+="update_time";
				insertValues+=new Date().getTime()/1000;

				insertSql="insert into hq_goods_skin("+insertKeys+") values("+insertValues+")";

				updateSql="update hq_goods_skin set "+updateSql+" where goods_id="+goods.getId();
			} else {
				insertSql=null;
				updateSql=null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String updaeSql() {
		return updateSql;
	}

	@Override
	public String selectSql() {
		if(isSkinCalculate()) {
			this.selectSql="select * from hq_goods_skin where goods_id="+goods.getId();
		} else {
			this.selectSql=null;
		}
		return this.selectSql;
	}

	@Override
	public String insertSql() {
		// TODO Auto-generated method stub
		return insertSql;
	}

	@Override
	public void display(List<Map<String,Object>> maps) {
		// TODO Auto-generated method stub
		try{
			if(!StringUtils.isBlank(this.goodsExplain.getUserSkin())) {
				String skinType=this.goodsExplain.getUserSkin();
				int skinMatchNum;
				int skinNotMatchNum;
				int skinMatchIsexist;
				int skinNotMatchIsexist;
				String skinMatchCps;
				String skinNotMatchCps;
				//匹配肤质类型
				if(maps!=null) {
					for(Map map:maps){
						Set set=map.keySet();
						Explain suitExp=null;
						if(null!=goodsExplain.getSuit()){
							suitExp=goodsExplain.getSuit();
						}else{
							suitExp=new Explain();
						}
						Explain notSuitExp=null;
						if(null!=goodsExplain.getNoSuit()){
							notSuitExp=goodsExplain.getNoSuit();
						}else{
							notSuitExp=new Explain();
						}
						if(set.contains("skin_match_"+skinType+"_num")){
							skinMatchNum=(Integer)map.get("skin_match_"+skinType+"_num");
							suitExp.setSkinMatchNum(skinMatchNum);
						}
						if(set.contains("skin_notmatch_"+skinType+"_num")){
							skinNotMatchNum=(Integer)map.get("skin_notmatch_"+skinType+"_num");
							notSuitExp.setSkinNotMatchNum(skinNotMatchNum);
						}
						if(set.contains("skin_match_"+skinType+"_isexist")){
							skinMatchIsexist=(Integer)map.get("skin_match_"+skinType+"_isexist");
							suitExp.setSkinMatchIsexist(skinMatchIsexist);
						}
						if(set.contains("skin_notmatch_"+skinType+"_isexist")){
							skinNotMatchIsexist=(Integer)map.get("skin_notmatch_"+skinType+"_isexist");
							notSuitExp.setSkinNotMatchIsexist(skinNotMatchIsexist);
						}
						if(set.contains("skin_match_"+skinType+"_cps")){
							skinMatchCps=(String)map.get("skin_match_"+skinType+"_cps");
							suitExp.setSkinMatchCps(skinMatchCps);
						}
						if(set.contains("skin_notmatch_"+skinType+"_cps")){
							skinNotMatchCps=(String)map.get("skin_notmatch_"+skinType+"_cps");
							notSuitExp.setSkinNotMatchCps(skinNotMatchCps);
						}
						goodsExplain.setSuit(suitExp);
						goodsExplain.setNoSuit(notSuitExp);
					}

				}
			}else{
				goodsExplain.setNoSuit(null);
				goodsExplain.setSuit(null);
			}
		}catch(Exception e){
			logger.error("method:display arg:{maps:" + maps + "   desc:" +  ExceptionUtils.getStackTrace(e));
		}

	}

	public GoodsExplain getGoodsExplain() {
		return goodsExplain;
	}

	public void setGoodsExplain(GoodsExplain goodsExplain) {
		this.goodsExplain = goodsExplain;
	}

	public List<Composition> getCompositions() {
		return compositions;
	}

	public void setCompositions(List<Composition> compositions) {
		this.compositions = compositions;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	@Override
	public Map entityInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
