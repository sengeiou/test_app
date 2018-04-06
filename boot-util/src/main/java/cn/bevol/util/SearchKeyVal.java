package cn.bevol.util;

import java.util.HashMap;

/**
 * 模拟可重复的key val 由于引用查询
 * @author hualong
 *
 * @param <K>
 * @param <V>
 */
public class SearchKeyVal<K,V> extends HashMap<K,V> {

	public V put(K k,V v) {
		if(this.get(k)==null) {
			super.put(k, v);
		}
		return this.get(k);
	}

}
