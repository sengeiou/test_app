package cn.bevol.statics.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection="entity_goods_ean")
public class GoodsOfEan extends MongoBase{
	//数据来源类型(2为预备库 3为用户自行填写)
	private Integer source ;
	//数据在预备库中的id
	private Long goodsId ;
	//与entity_record_ean的关联Id
	private Long recordId;



	private String tname;

	//是否通过审批(0为不通过 1为通过)
	private Integer isPass ;
	
	//是否入库upc(1为已入库匹配)
	private Integer isCheck ;

	private String message;
	
	private Long userId;
	

	private Map info;
	
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getIsPass() {
		return isPass;
	}

	public void setIsPass(Integer isPass) {
		this.isPass = isPass;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map getInfo() {
		return info;
	}

	public void setInfo(Map info) {
		this.info = info;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Integer getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(Integer isCheck) {
		this.isCheck = isCheck;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
}
