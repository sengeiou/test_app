package cn.bevol.internal.entity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**     
 * 产品使用目的功效关系表
 * @author Administrator
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsUsedEffect implements Serializable,Comparable {
	private Integer id;
	//大成分的名称
	private String name;
	private Integer pid;
	/**
 	 * 页面显示的对比大成分名称
 	 */
 	private String displayCompareName;
 	
 	private Integer displayCompareSort;
 	
 	private Integer displayCompare;

 
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getPid() {
		return pid;
	}


	public void setPid(Integer pid) {
		this.pid = pid;
	}


	public String getDisplayCompareName() {
		return displayCompareName;
	}


	public void setDisplayCompareName(String displayCompareName) {
		this.displayCompareName = displayCompareName;
	}


	public Integer getDisplayCompareSort() {
		return displayCompareSort;
	}


	public void setDisplayCompareSort(Integer displayCompareSort) {
		this.displayCompareSort = displayCompareSort;
	}


	public Integer getDisplayCompare() {
		return displayCompare;
	}


	public void setDisplayCompare(Integer displayCompare) {
		this.displayCompare = displayCompare;
	}


	public int compareTo(Object o) {
		GoodsUsedEffect gut=(GoodsUsedEffect) o;
          if(displayCompareSort.intValue()>gut.getDisplayCompareSort()) {
               return displayCompareSort.intValue() - gut.getDisplayCompareSort();
          } else {
               return -1;
          }
	}
	
	
	
	public static void main(String[] args) {
		List<GoodsUsedEffect>  guef=new ArrayList<GoodsUsedEffect>();
		GoodsUsedEffect e=new GoodsUsedEffect();
		e.setDisplayCompareSort(1);
		guef.add(e);
		GoodsUsedEffect e2=new GoodsUsedEffect();
		e2.setDisplayCompareSort(4);
		guef.add(e2);
		
		GoodsUsedEffect e3=new GoodsUsedEffect();
		e3.setDisplayCompareSort(3);
		guef.add(e3);
		
		GoodsUsedEffect e4=new GoodsUsedEffect();
		e4.setDisplayCompareSort(6);
		guef.add(e4);
		Collections.sort(guef);

	}
	
	
}
