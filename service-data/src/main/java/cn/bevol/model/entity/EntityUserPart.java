package cn.bevol.model.entity;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import cn.bevol.model.metadata.UserBaseInfo;
import cn.bevol.util.CommonUtils;

/**
 * 话题
 * @author hualong
 *
 */
@Document(collection="entity_user_part_lists")
public class EntityUserPart extends EntityBase {
 	
		/**
		 * 用户id
		 */
		private Long userId;
		
		/**
		 * 用户角色类型 
		 * 1 修修酱
		 * 2 达人类型
		 */
		private Integer userRole;
		
		/**
		 * type=1  普通
		 * type=2 试用
		 * 
		 */
		private Integer type;

		
		/**
		 * 用户基本信息
		 */
		private UserBaseInfo userBaseInfo;
		
		/**
		 * 标签信息
		 */
		private List<Integer> tags;
		
		/**
		 * 实体信息
		 */
		private List<UserPartDetail> userPartDetails;
		
		/**
		 * 对应上一级实体的id
		 */
		private Long pEntityId;
		
		/**
		 * 对应上一级实体的名称
		 */
		private String pEntityName;
		
		/**
		 *  修行社 精选 人工排序所有心得的
		 */
		private Integer sort;
		
		/**
		 * 福利社 话题 排序字段
		 */
		private Integer sort2;

		
		/**
		 * 申请类型排序
		 */
		private Integer type2Sort;
		
		
		/**
		 * 申请状态
		 * 1 申请中
		 * 2 中奖了 
		 * 3 结束(未中奖)
		 */
		private Integer applyState;

		
		/**
		 * 图片源路径
		 */
		@Transient
		private String imgSrc;
		
		/**
		 * 来源,扩展字段
		 */
		private Map exFeilds;

 

		public Integer getSort2() {
			return sort2;
		}

		public void setSort2(Integer sort2) {
			this.sort2 = sort2;
		}

		public Map getExFeilds() {
			return exFeilds;
		}

		public void setExFeilds(Map exFeilds) {
			this.exFeilds = exFeilds;
		}

		public String getImgSrc() {
			imgSrc=CommonUtils.getImageSrc("user_part/lists", this.getImage());
			return imgSrc;
		}

		public void setImgSrc(String imgSrc) {
			this.imgSrc = imgSrc;
		}
 
		public UserBaseInfo getUserBaseInfo() {
			return userBaseInfo;
		}


		public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
			this.userBaseInfo = userBaseInfo;
		}


		public List<Integer> getTags() {
			return tags;
		}


		public void setTags(List<Integer> tags) {
			this.tags = tags;
		}


		public Long getpEntityId() {
			return pEntityId;
		}


		public void setpEntityId(Long pEntityId) {
			this.pEntityId = pEntityId;
		}


		public List<UserPartDetail> getUserPartDetails() {
			return userPartDetails;
		}


		public void setUserPartDetails(List<UserPartDetail> userPartDetails) {
			this.userPartDetails = userPartDetails;
			/*if(userPartDetails!=null) {
				StringBuffer sb=new StringBuffer();
				for(int i=0;i<userPartDetails.size();i++) {
					if(userPartDetails.get(i)!=null) {
						if(userPartDetails.get(i) instanceof UserPartDetailGoods) {
							UserPartDetailGoods ug=(UserPartDetailGoods) userPartDetails.get(i);
							sb.append(ug.getTitle()).append(" ");
						} else  if(userPartDetails.get(i) instanceof UserPartDetailGoods) {
							
						}
					}
				}
			}*/
		}


		public Long getUserId() {
			return userId;
		}


		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getUserRole() {
			return userRole;
		}

		public void setUserRole(Integer userRole) {
			this.userRole = userRole;
		}

		public Integer getSort() {
			return sort;
		}

		public void setSort(Integer sort) {
			this.sort = sort;
		}

		public Integer getType2Sort() {
			return type2Sort;
		}

		public void setType2Sort(Integer type2Sort) {
			this.type2Sort = type2Sort;
		}

		public String getpEntityName() {
			return pEntityName;
		}

		public void setpEntityName(String pEntityName) {
			this.pEntityName = pEntityName;
		}

		public Integer getApplyState() {
			return applyState;
		}

		public void setApplyState(Integer applyState) {
			this.applyState = applyState;
		}


  
}
