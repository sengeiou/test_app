package cn.bevol.model.user;

import cn.bevol.app.entity.metadata.EntityInfo;
import cn.bevol.model.entity.MongoBase;
import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@Document(collection="user_skin_protection")
public class UserSkinProtection extends MongoBase {

	/**
	 * 产品分类
	 */
	private Long categoryId;
	
	
	/**
	 * 产品分类 pid
	 */
	private Long categoryPid;

	
	/**
	 * 用户id
	 */
	private Long userId;
	
	
	private Long entityId;
	
	/**
	 * 实体类型
	 */
	private String entityName;

	
	/**
	 * 实体信息
	 */
	private EntityInfo entityInfo;
	
	/**
	 * 是否开封
	 * 1表示开封
	 */
	private Integer open;
	
	/**
	 * 打开日期
	 */
	private Long openTime;
	
	/**
	 * 过期时间
	 */
	private Long expireTime;
	
	/**
	 * 保质期
	 */
	private Integer releaseDate;
	
	/**
	 * 剩余天数
	 */
	private Integer remainingDays;
 	
	/**
	 * 使用时段 1：白天 2：晚上 3全天 
	 */
	private Integer usedType;
	
	/**
	 * 过期产品是否需要提醒
	 * true已被查看
	 * false提醒用户
	 */
	private Boolean expire=true;
		
	public Long getCategoryPid() {
		return categoryPid;
	}
	public void setCategoryPid(Long categoryPid) {
		this.categoryPid = categoryPid;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
 	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}
	public Long getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Long openTime) {
		this.openTime = openTime;
	}
  	public Integer getOpen() {
		return open;
	}
	public void setOpen(Integer open) {
		this.open = open;
	}
	public Integer getUsedType() {
		return usedType;
	}
	public void setUsedType(Integer usedType) {
		this.usedType = usedType;
	}
	public Integer getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Integer releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Integer getRemainingDays() {
		if(this.open!=null&&this.open==1&&this.getOpenTime()!=null&&getReleaseDate()!=null) {
			//剩余天数
			int oneDay=(60*60*24); //一线的
			
			int day=this.releaseDate*oneDay*30;
			
			long cur= DateUtils.nowInMillis()/1000;
			int lastDay=(int) (cur-(this.getOpenTime()+day));
			
			//1、过期
			if(lastDay>0) {
				return 0;
			} else {
				remainingDays=-(lastDay/oneDay);
			}
			//2、剩下天数
		}
		return remainingDays;
	}
	public void setRemainingDays(Integer remainingDays) {
		this.remainingDays = remainingDays;
	}
	public Boolean getExpire() {
		return expire;
	}
	public void setExpire(Boolean expire) {
		this.expire = expire;
	}
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
 	
	
	
 }
