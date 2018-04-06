package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.tpwd.create response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkTpwdCreateResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3872447117992615928L;

	/** 
	 * data
	 */
	@ApiField("data")
	private MapData data;


	public void setData(MapData data) {
		this.data = data;
	}
	public MapData getData( ) {
		return this.data;
	}
	
	/**
 * data
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class MapData extends TaobaoObject {

	private static final long serialVersionUID = 6778313162645949483L;

	/**
		 * password
		 */
		@ApiField("model")
		private String model;
	

	public String getModel() {
			return this.model;
		}
		public void setModel(String model) {
			this.model = model;
		}

}



}
