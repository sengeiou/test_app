package com.taobao.api.domain;

import java.util.Date;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 淘客定向招商活动基本信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public class TbkEvent extends TaobaoObject {

	private static final long serialVersionUID = 4816232343347954322L;

	/**
	 * 定向招商活动结束开始时间
	 */
	@ApiField("end_time")
	private Date endTime;

	/**
	 * 淘宝联盟定向招商活动id
	 */
	@ApiField("event_id")
	private Long eventId;

	/**
	 * 淘宝联盟定向招商活动名称
	 */
	@ApiField("event_title")
	private String eventTitle;

	/**
	 * 定向招商活动结束开始时间
	 */
	@ApiField("start_time")
	private Date startTime;


	public Date getEndTime() {
		return this.endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getEventId() {
		return this.eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getEventTitle() {
		return this.eventTitle;
	}
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public Date getStartTime() {
		return this.startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

}
