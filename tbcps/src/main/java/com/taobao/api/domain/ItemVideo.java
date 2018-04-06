package com.taobao.api.domain;

import java.util.Date;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 商品视频
 *
 * @author top auto create
 * @since 1.0, null
 */
public class ItemVideo extends TaobaoObject {

	private static final long serialVersionUID = 2541146711892566152L;

	/**
	 * 创建时间，格式:yyyy-MM-dd HH:mm:ss
	 */
	@ApiField("created")
	private Date created;

	/**
	 * 商品ID
	 */
	@ApiField("item_id")
	private Long itemId;

	/**
	 * 修改时间，格式:yyyy-MM-dd HH:mm:ss
	 */
	@ApiField("modified")
	private Date modified;

	/**
	 * ItemVideo对应的Item的数字id
	 */
	@ApiField("num_iid")
	private Long numIid;

	/**
	 * 视频ID
	 */
	@ApiField("video_id")
	private Long videoId;


	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getItemId() {
		return this.itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Date getModified() {
		return this.modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Long getNumIid() {
		return this.numIid;
	}
	public void setNumIid(Long numIid) {
		this.numIid = numIid;
	}

	public Long getVideoId() {
		return this.videoId;
	}
	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

}
