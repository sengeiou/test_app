package cn.bevol.entity.service.goodsCalculate;

import java.util.List;
import java.util.Map;

/**
 * 产品计算接口
 * @author Administrator
 *
 */
public interface GoodsCalculateI{

	public void handler();

	public String updaeSql();

	public String selectSql();

	public String insertSql();

	public Map entityInfo();

	/**
	 * 显示的结果 封装
	 * @param map
	 */
	public void display(List<Map<String,Object>> listMap);


}
