package cn.bevol.model.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import cn.bevol.model.Base;


@Document(collection="entity_record_ean")
public class RecordOfEan extends Base{
	//entity_goods_ean中的唯一id
	private Long eanId;
	
	private List<Long> goodsId;
	


	private Long pid;
	
	private Integer count;

	private int isPass;
	
	private String ean;
	
	private int source;
	
	
	public Long getEanId() {
		return eanId;
	}

	public void setEanId(Long eanId) {
		this.eanId = eanId;
	}

	public int getIsPass() {
		return isPass;
	}

	public void setIsPass(int isPass) {
		this.isPass = isPass;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public List<Long> getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(List<Long> goodsId) {
		this.goodsId = goodsId;
	}
	
	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
