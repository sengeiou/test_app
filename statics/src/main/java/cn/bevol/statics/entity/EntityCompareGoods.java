package cn.bevol.statics.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 实体对比
 * @author hualong
 *
 */
@Document(collection="entity_compare_goods")
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class EntityCompareGoods extends EntityCompare {
	
}
