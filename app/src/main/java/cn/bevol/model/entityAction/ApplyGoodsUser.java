package cn.bevol.model.entityAction;

import cn.bevol.util.CommonUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请的产品
 * @author Administrator
 *
 */
@Document(collection="apply_goods_user")
public class ApplyGoodsUser extends Apply {

	/**
	 * 申请状态
	 * 1 申请中
	 * 2 中奖了 
	 * 3 结束(未中奖)
	 */
	private Integer state;
	
	/**
	 * 申请状态提示语
	 */
	private String stateDesc;
	

	/***    new 20170815 start***/

	/**
	 * 试用报告状态
	 * 1 表示写了
	 */
	private Integer userPartState;
	
	
	/**
	 * 申请内容
	 */
	private String content;
	
	/**
	 * 申请时的用户修行值
	 */
	private Long score = 0L;
	
	/**
	 * 图片
	 */
	private List images;
	
	/**
	 * 用户信息，地址，电话等
	 */
	private String userDesc;
	
	/**
	 * 快递公司
	 */
	private String express;
	
	/**
	 * 快递号
	 */
	private String expressNumber;
	
	/**
	 * 图片完整路径
	 */
	@Transient
	private List imagesSrc;

	/***     20170815 end***/

	
	
	
	public Integer getState() {
		return state;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getExpressNumber() {
		return expressNumber;
	}

	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getUserPartState() {
		return userPartState;
	}

	public void setUserPartState(Integer userPartState) {
		this.userPartState = userPartState;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List getImages() {
		return images;
	}

	public void setImages(List images) {
		this.images = images;
	}
	
	public List getImagesSrc() {
		//图片完整路径处理
		if(null!=this.images && this.images.size()>0){
			List<String> list=new ArrayList<String>();
			for(int i=0;i<this.images.size();i++){
				String image=(String)this.images.get(i);
				if(image.indexOf("http")==-1){
					image= CommonUtils.getImageSrc("apply/reason", image);
				}
				list.add(image);
			}
			this.imagesSrc=list;
		}
		return imagesSrc;
	}

	public void setImagesSrc(List imagesSrc) {
		this.imagesSrc = imagesSrc;
	}

	public String getStateDesc() {
		if(null!=this.state){
			if(state==1){
				stateDesc="申请中";
			} else if(state==2){
				stateDesc="已中奖";
			} else if(state==3 || (state!=1 && state!=2)){
				stateDesc="下次加油";
			}
		}
		return stateDesc;
	}

	public void setStateDesc(String stateDesc) {
		this.stateDesc = stateDesc;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}
	


	
}
