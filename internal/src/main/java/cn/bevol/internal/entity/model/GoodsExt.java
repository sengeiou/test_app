package cn.bevol.internal.entity.model;

import java.io.Serializable;

/**
 * goods 成分排序
 * @author Administrator
 *
 */
public class GoodsExt implements Serializable {

	private Long id;
	
	/**
	 * 默认药监局顺序
	 */
	private String defCps;

	/**
	 * 标签顺序
	 */
	private String mfjCps;
	
	/**
	 * 手动排序顺序
	 */
	private String gcCps;
	
	/**
	 * 药监局扩展顺序
	 */
	private String defExtCps;
	
	/**
	 * cps默认显示的顺序
	 *  'def_cps_1':'产品配方顺序',
		'cps_def_2':'产品配方顺序',
		'def_cps_3':'产品备案顺序',
		'gc_cps':'标签顺序',
		'mfj_cps':'标签顺序',
		'def':'标签顺序',
		'def_ext_cps:''标签顺序'
	 */
	private String cpsType;
	
	
	
	/**
	 * 是否可以发送评论 1表示不能发送
	 */
	private Integer allowComment;

	/**
	 * goodsid
	 */
	private Long goodsId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDefCps() {
		return defCps;
	}

	public void setDefCps(String defCps) {
		this.defCps = defCps;
	}

	public String getMfjCps() {
		return mfjCps;
	}

	public void setMfjCps(String mfjCps) {
		this.mfjCps = mfjCps;
	}

	public String getGcCps() {
		return gcCps;
	}

	public void setGcCps(String gcCps) {
		this.gcCps = gcCps;
	}

	public String getDefExtCps() {
		return defExtCps;
	}

	public void setDefExtCps(String defExtCps) {
		this.defExtCps = defExtCps;
	}

	public String getCpsType() {
		return cpsType;
	}

	public void setCpsType(String cpsType) {
		this.cpsType = cpsType;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getAllowComment() {
		return allowComment;
	}

	public void setAllowComment(Integer allowComment) {
		this.allowComment = allowComment;
	}

 
 	
	
}
