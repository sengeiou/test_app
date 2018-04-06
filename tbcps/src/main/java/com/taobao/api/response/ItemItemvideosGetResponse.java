package com.taobao.api.response;

import java.util.List;
import com.taobao.api.domain.ItemVideo;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.item.itemvideos.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class ItemItemvideosGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6492415326717573478L;

	/** 
	 * 商品和视频关联列表
	 */
	@ApiListField("item_videos")
	@ApiField("item_video")
	private List<ItemVideo> itemVideos;

	/** 
	 * 总数（根据卖家查询商品列表的时候有)
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setItemVideos(List<ItemVideo> itemVideos) {
		this.itemVideos = itemVideos;
	}
	public List<ItemVideo> getItemVideos( ) {
		return this.itemVideos;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
