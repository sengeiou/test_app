package cn.bevol.statics.entity.entityAction;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;


/**
 * 16种肤质排序
 * @author hualong
 *
 */
public class  Skin  extends EntityActionBase {

	private Integer DZ_RZ_N_T ;
	private Integer DZ_RZ_N_W ;
	private Integer DZ_RZ_P_T ;
	private Integer DZ_RZ_P_W ;
	private Integer DZ_RQ_N_T ;
	private Integer DZ_RQ_N_W ;
	private Integer DZ_RQ_P_W ;
	private Integer DZ_RQ_P_T ;
	private Integer DZ_SQ_N_T ;
	private Integer DZ_SQ_N_W ;
	private Integer DZ_SQ_P_W ;
	private Integer DZ_SQ_P_T ;
	private Integer DZ_SZ_P_T ;
	private Integer DZ_SZ_P_W ;
	private Integer DZ_SZ_N_W ;
	private Integer DZ_SZ_N_T ;
	private Integer DQ_RZ_N_T ;
	private Integer DQ_RZ_N_W ;
	private Integer DQ_RZ_P_T ;
	private Integer DQ_RZ_P_W ;
	private Integer DQ_RQ_N_T ;
	private Integer DQ_RQ_N_W ;
	private Integer DQ_RQ_P_W ;
	private Integer DQ_RQ_P_T ;
	private Integer DQ_SQ_N_T ;
	private Integer DQ_SQ_N_W ;
	private Integer DQ_SQ_P_W ;
	private Integer DQ_SQ_P_T ;
	private Integer DQ_SZ_P_T ;
	private Integer DQ_SZ_P_W ;
	private Integer DQ_SZ_N_W ;
	private Integer DQ_SZ_N_T ;

	private Integer OQ_RZ_N_T ;
	private Integer OQ_RZ_N_W ;
	private Integer OQ_RZ_P_T ;
	private Integer OQ_RZ_P_W ;
	private Integer OQ_RQ_N_T ;
	private Integer OQ_RQ_N_W ;
	private Integer OQ_RQ_P_W ;
	private Integer OQ_RQ_P_T ;
	private Integer OQ_SQ_N_T ;
	private Integer OQ_SQ_N_W ;
	private Integer OQ_SQ_P_W ;
	private Integer OQ_SQ_P_T ;
	private Integer OQ_SZ_P_T ;
	private Integer OQ_SZ_P_W ;
	private Integer OQ_SZ_N_W ;
	private Integer OQ_SZ_N_T ;
	private Integer OZ_RZ_N_T ;
	private Integer OZ_RZ_N_W ;
	private Integer OZ_RZ_P_T ;
	private Integer OZ_RZ_P_W ;
	private Integer OZ_RQ_N_T ;
	private Integer OZ_RQ_N_W ;
	private Integer OZ_RQ_P_W ;
	private Integer OZ_RQ_P_T ;
	private Integer OZ_SQ_N_T ;
	private Integer OZ_SQ_N_W ;
	private Integer OZ_SQ_P_W ;
	private Integer OZ_SQ_P_T ;
	private Integer OZ_SZ_P_T ;
	private Integer OZ_SZ_P_W ;
	private Integer OZ_SZ_N_W ;
	private Integer OZ_SZ_N_T ;
 
	
	public Integer getDZ_RZ_N_T() {
		return DZ_RZ_N_T;
	}


	public void setDZ_RZ_N_T(Integer dZ_RZ_N_T) {
		DZ_RZ_N_T = dZ_RZ_N_T;
	}


	public Integer getDZ_RZ_N_W() {
		return DZ_RZ_N_W;
	}


	public void setDZ_RZ_N_W(Integer dZ_RZ_N_W) {
		DZ_RZ_N_W = dZ_RZ_N_W;
	}


	public Integer getDZ_RZ_P_T() {
		return DZ_RZ_P_T;
	}


	public void setDZ_RZ_P_T(Integer dZ_RZ_P_T) {
		DZ_RZ_P_T = dZ_RZ_P_T;
	}


	public Integer getDZ_RZ_P_W() {
		return DZ_RZ_P_W;
	}


	public void setDZ_RZ_P_W(Integer dZ_RZ_P_W) {
		DZ_RZ_P_W = dZ_RZ_P_W;
	}


	public Integer getDZ_RQ_N_T() {
		return DZ_RQ_N_T;
	}


	public void setDZ_RQ_N_T(Integer dZ_RQ_N_T) {
		DZ_RQ_N_T = dZ_RQ_N_T;
	}


	public Integer getDZ_RQ_N_W() {
		return DZ_RQ_N_W;
	}


	public void setDZ_RQ_N_W(Integer dZ_RQ_N_W) {
		DZ_RQ_N_W = dZ_RQ_N_W;
	}


	public Integer getDZ_RQ_P_W() {
		return DZ_RQ_P_W;
	}


	public void setDZ_RQ_P_W(Integer dZ_RQ_P_W) {
		DZ_RQ_P_W = dZ_RQ_P_W;
	}


	public Integer getDZ_RQ_P_T() {
		return DZ_RQ_P_T;
	}


	public void setDZ_RQ_P_T(Integer dZ_RQ_P_T) {
		DZ_RQ_P_T = dZ_RQ_P_T;
	}


	public Integer getDZ_SQ_N_T() {
		return DZ_SQ_N_T;
	}

/* *//**
     * 肤质
     *//*
    public static Configuration SKIN_CONFIG = null;
    //油性皮肤
    public static Map<String,String> SKIN_O = new HashMap<String,String>();
    //干性皮肤
    public static Map<String,String> SKIN_D = new HashMap<String,String>();
	static {
		try {
			SKIN_CONFIG = new PropertiesConfiguration("bevol-skinsort.ini");
	    	Iterator<String> it=SKIN_CONFIG.getKeys();
	    	while(it.hasNext()){
	    		String key=it.next();
	    		String skinType=key.substring(0, 1);
	    		if("O".equals(skinType)){
	    			SKIN_O.put(key, null);
	    		}else if("D".equals(skinType)){
	    			SKIN_D.put(key, null);
	    		}
	    	}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}*/
	public void setDZ_SQ_N_T(Integer dZ_SQ_N_T) {
		DZ_SQ_N_T = dZ_SQ_N_T;
	}


	public Integer getDZ_SQ_N_W() {
		return DZ_SQ_N_W;
	}


	public void setDZ_SQ_N_W(Integer dZ_SQ_N_W) {
		DZ_SQ_N_W = dZ_SQ_N_W;
	}


	public Integer getDZ_SQ_P_W() {
		return DZ_SQ_P_W;
	}


	public void setDZ_SQ_P_W(Integer dZ_SQ_P_W) {
		DZ_SQ_P_W = dZ_SQ_P_W;
	}


	public Integer getDZ_SQ_P_T() {
		return DZ_SQ_P_T;
	}


	public void setDZ_SQ_P_T(Integer dZ_SQ_P_T) {
		DZ_SQ_P_T = dZ_SQ_P_T;
	}


	public Integer getDZ_SZ_P_T() {
		return DZ_SZ_P_T;
	}


	public void setDZ_SZ_P_T(Integer dZ_SZ_P_T) {
		DZ_SZ_P_T = dZ_SZ_P_T;
	}


	public Integer getDZ_SZ_P_W() {
		return DZ_SZ_P_W;
	}


	public void setDZ_SZ_P_W(Integer dZ_SZ_P_W) {
		DZ_SZ_P_W = dZ_SZ_P_W;
	}


	public Integer getDZ_SZ_N_W() {
		return DZ_SZ_N_W;
	}


	public void setDZ_SZ_N_W(Integer dZ_SZ_N_W) {
		DZ_SZ_N_W = dZ_SZ_N_W;
	}


	public Integer getDZ_SZ_N_T() {
		return DZ_SZ_N_T;
	}


	public void setDZ_SZ_N_T(Integer dZ_SZ_N_T) {
		DZ_SZ_N_T = dZ_SZ_N_T;
	}


	public Integer getDQ_RZ_N_T() {
		return DQ_RZ_N_T;
	}


	public void setDQ_RZ_N_T(Integer dQ_RZ_N_T) {
		DQ_RZ_N_T = dQ_RZ_N_T;
	}


	public Integer getDQ_RZ_N_W() {
		return DQ_RZ_N_W;
	}


	public void setDQ_RZ_N_W(Integer dQ_RZ_N_W) {
		DQ_RZ_N_W = dQ_RZ_N_W;
	}


	public Integer getDQ_RZ_P_T() {
		return DQ_RZ_P_T;
	}


	public void setDQ_RZ_P_T(Integer dQ_RZ_P_T) {
		DQ_RZ_P_T = dQ_RZ_P_T;
	}


	public Integer getDQ_RZ_P_W() {
		return DQ_RZ_P_W;
	}


	public void setDQ_RZ_P_W(Integer dQ_RZ_P_W) {
		DQ_RZ_P_W = dQ_RZ_P_W;
	}


	public Integer getDQ_RQ_N_T() {
		return DQ_RQ_N_T;
	}


	public void setDQ_RQ_N_T(Integer dQ_RQ_N_T) {
		DQ_RQ_N_T = dQ_RQ_N_T;
	}


	public Integer getDQ_RQ_N_W() {
		return DQ_RQ_N_W;
	}


	public void setDQ_RQ_N_W(Integer dQ_RQ_N_W) {
		DQ_RQ_N_W = dQ_RQ_N_W;
	}


	public Integer getDQ_RQ_P_W() {
		return DQ_RQ_P_W;
	}


	public void setDQ_RQ_P_W(Integer dQ_RQ_P_W) {
		DQ_RQ_P_W = dQ_RQ_P_W;
	}


	public Integer getDQ_RQ_P_T() {
		return DQ_RQ_P_T;
	}


	public void setDQ_RQ_P_T(Integer dQ_RQ_P_T) {
		DQ_RQ_P_T = dQ_RQ_P_T;
	}


	public Integer getDQ_SQ_N_T() {
		return DQ_SQ_N_T;
	}


	public void setDQ_SQ_N_T(Integer dQ_SQ_N_T) {
		DQ_SQ_N_T = dQ_SQ_N_T;
	}


	public Integer getDQ_SQ_N_W() {
		return DQ_SQ_N_W;
	}


	public void setDQ_SQ_N_W(Integer dQ_SQ_N_W) {
		DQ_SQ_N_W = dQ_SQ_N_W;
	}


	public Integer getDQ_SQ_P_W() {
		return DQ_SQ_P_W;
	}


	public void setDQ_SQ_P_W(Integer dQ_SQ_P_W) {
		DQ_SQ_P_W = dQ_SQ_P_W;
	}


	public Integer getDQ_SQ_P_T() {
		return DQ_SQ_P_T;
	}


	public void setDQ_SQ_P_T(Integer dQ_SQ_P_T) {
		DQ_SQ_P_T = dQ_SQ_P_T;
	}


	public Integer getDQ_SZ_P_T() {
		return DQ_SZ_P_T;
	}


	public void setDQ_SZ_P_T(Integer dQ_SZ_P_T) {
		DQ_SZ_P_T = dQ_SZ_P_T;
	}


	public Integer getDQ_SZ_P_W() {
		return DQ_SZ_P_W;
	}


	public void setDQ_SZ_P_W(Integer dQ_SZ_P_W) {
		DQ_SZ_P_W = dQ_SZ_P_W;
	}


	public Integer getDQ_SZ_N_W() {
		return DQ_SZ_N_W;
	}


	public void setDQ_SZ_N_W(Integer dQ_SZ_N_W) {
		DQ_SZ_N_W = dQ_SZ_N_W;
	}


	public Integer getDQ_SZ_N_T() {
		return DQ_SZ_N_T;
	}


	public void setDQ_SZ_N_T(Integer dQ_SZ_N_T) {
		DQ_SZ_N_T = dQ_SZ_N_T;
	}


	public Integer getOQ_RZ_N_T() {
		return OQ_RZ_N_T;
	}


	public void setOQ_RZ_N_T(Integer oQ_RZ_N_T) {
		OQ_RZ_N_T = oQ_RZ_N_T;
	}


	public Integer getOQ_RZ_N_W() {
		return OQ_RZ_N_W;
	}


	public void setOQ_RZ_N_W(Integer oQ_RZ_N_W) {
		OQ_RZ_N_W = oQ_RZ_N_W;
	}


	public Integer getOQ_RZ_P_T() {
		return OQ_RZ_P_T;
	}


	public void setOQ_RZ_P_T(Integer oQ_RZ_P_T) {
		OQ_RZ_P_T = oQ_RZ_P_T;
	}


	public Integer getOQ_RZ_P_W() {
		return OQ_RZ_P_W;
	}


	public void setOQ_RZ_P_W(Integer oQ_RZ_P_W) {
		OQ_RZ_P_W = oQ_RZ_P_W;
	}


	public Integer getOQ_RQ_N_T() {
		return OQ_RQ_N_T;
	}


	public void setOQ_RQ_N_T(Integer oQ_RQ_N_T) {
		OQ_RQ_N_T = oQ_RQ_N_T;
	}


	public Integer getOQ_RQ_N_W() {
		return OQ_RQ_N_W;
	}


	public void setOQ_RQ_N_W(Integer oQ_RQ_N_W) {
		OQ_RQ_N_W = oQ_RQ_N_W;
	}


	public Integer getOQ_RQ_P_W() {
		return OQ_RQ_P_W;
	}


	public void setOQ_RQ_P_W(Integer oQ_RQ_P_W) {
		OQ_RQ_P_W = oQ_RQ_P_W;
	}


	public Integer getOQ_RQ_P_T() {
		return OQ_RQ_P_T;
	}


	public void setOQ_RQ_P_T(Integer oQ_RQ_P_T) {
		OQ_RQ_P_T = oQ_RQ_P_T;
	}


	public Integer getOQ_SQ_N_T() {
		return OQ_SQ_N_T;
	}


	public void setOQ_SQ_N_T(Integer oQ_SQ_N_T) {
		OQ_SQ_N_T = oQ_SQ_N_T;
	}


	public Integer getOQ_SQ_N_W() {
		return OQ_SQ_N_W;
	}


	public void setOQ_SQ_N_W(Integer oQ_SQ_N_W) {
		OQ_SQ_N_W = oQ_SQ_N_W;
	}


	public Integer getOQ_SQ_P_W() {
		return OQ_SQ_P_W;
	}


	public void setOQ_SQ_P_W(Integer oQ_SQ_P_W) {
		OQ_SQ_P_W = oQ_SQ_P_W;
	}


	public Integer getOQ_SQ_P_T() {
		return OQ_SQ_P_T;
	}


	public void setOQ_SQ_P_T(Integer oQ_SQ_P_T) {
		OQ_SQ_P_T = oQ_SQ_P_T;
	}


	public Integer getOQ_SZ_P_T() {
		return OQ_SZ_P_T;
	}


	public void setOQ_SZ_P_T(Integer oQ_SZ_P_T) {
		OQ_SZ_P_T = oQ_SZ_P_T;
	}


	public Integer getOQ_SZ_P_W() {
		return OQ_SZ_P_W;
	}


	public void setOQ_SZ_P_W(Integer oQ_SZ_P_W) {
		OQ_SZ_P_W = oQ_SZ_P_W;
	}


	public Integer getOQ_SZ_N_W() {
		return OQ_SZ_N_W;
	}


	public void setOQ_SZ_N_W(Integer oQ_SZ_N_W) {
		OQ_SZ_N_W = oQ_SZ_N_W;
	}


	public Integer getOQ_SZ_N_T() {
		return OQ_SZ_N_T;
	}


	public void setOQ_SZ_N_T(Integer oQ_SZ_N_T) {
		OQ_SZ_N_T = oQ_SZ_N_T;
	}


	public Integer getOZ_RZ_N_T() {
		return OZ_RZ_N_T;
	}


	public void setOZ_RZ_N_T(Integer oZ_RZ_N_T) {
		OZ_RZ_N_T = oZ_RZ_N_T;
	}


	public Integer getOZ_RZ_N_W() {
		return OZ_RZ_N_W;
	}


	public void setOZ_RZ_N_W(Integer oZ_RZ_N_W) {
		OZ_RZ_N_W = oZ_RZ_N_W;
	}


	public Integer getOZ_RZ_P_T() {
		return OZ_RZ_P_T;
	}


	public void setOZ_RZ_P_T(Integer oZ_RZ_P_T) {
		OZ_RZ_P_T = oZ_RZ_P_T;
	}


	public Integer getOZ_RZ_P_W() {
		return OZ_RZ_P_W;
	}


	public void setOZ_RZ_P_W(Integer oZ_RZ_P_W) {
		OZ_RZ_P_W = oZ_RZ_P_W;
	}


	public Integer getOZ_RQ_N_T() {
		return OZ_RQ_N_T;
	}


	public void setOZ_RQ_N_T(Integer oZ_RQ_N_T) {
		OZ_RQ_N_T = oZ_RQ_N_T;
	}


	public Integer getOZ_RQ_N_W() {
		return OZ_RQ_N_W;
	}


	public void setOZ_RQ_N_W(Integer oZ_RQ_N_W) {
		OZ_RQ_N_W = oZ_RQ_N_W;
	}


	public Integer getOZ_RQ_P_W() {
		return OZ_RQ_P_W;
	}


	public void setOZ_RQ_P_W(Integer oZ_RQ_P_W) {
		OZ_RQ_P_W = oZ_RQ_P_W;
	}


	public Integer getOZ_RQ_P_T() {
		return OZ_RQ_P_T;
	}


	public void setOZ_RQ_P_T(Integer oZ_RQ_P_T) {
		OZ_RQ_P_T = oZ_RQ_P_T;
	}


	public Integer getOZ_SQ_N_T() {
		return OZ_SQ_N_T;
	}


	public void setOZ_SQ_N_T(Integer oZ_SQ_N_T) {
		OZ_SQ_N_T = oZ_SQ_N_T;
	}


	public Integer getOZ_SQ_N_W() {
		return OZ_SQ_N_W;
	}


	public void setOZ_SQ_N_W(Integer oZ_SQ_N_W) {
		OZ_SQ_N_W = oZ_SQ_N_W;
	}


	public Integer getOZ_SQ_P_W() {
		return OZ_SQ_P_W;
	}


	public void setOZ_SQ_P_W(Integer oZ_SQ_P_W) {
		OZ_SQ_P_W = oZ_SQ_P_W;
	}


	public Integer getOZ_SQ_P_T() {
		return OZ_SQ_P_T;
	}


	public void setOZ_SQ_P_T(Integer oZ_SQ_P_T) {
		OZ_SQ_P_T = oZ_SQ_P_T;
	}


	public Integer getOZ_SZ_P_T() {
		return OZ_SZ_P_T;
	}


	public void setOZ_SZ_P_T(Integer oZ_SZ_P_T) {
		OZ_SZ_P_T = oZ_SZ_P_T;
	}


	public Integer getOZ_SZ_P_W() {
		return OZ_SZ_P_W;
	}


	public void setOZ_SZ_P_W(Integer oZ_SZ_P_W) {
		OZ_SZ_P_W = oZ_SZ_P_W;
	}


	public Integer getOZ_SZ_N_W() {
		return OZ_SZ_N_W;
	}


	public void setOZ_SZ_N_W(Integer oZ_SZ_N_W) {
		OZ_SZ_N_W = oZ_SZ_N_W;
	}


	public Integer getOZ_SZ_N_T() {
		return OZ_SZ_N_T;
	}


	public void setOZ_SZ_N_T(Integer oZ_SZ_N_T) {
		OZ_SZ_N_T = oZ_SZ_N_T;
	}
	private static Configuration config = null;
	static {
		try {
			config = new PropertiesConfiguration("bevol-skinsort.ini");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算肤质
	 * @param skin
	 */
	public  void calculatSkin() {
		if(!StringUtils.isBlank(this.getSkinResults())) {
			setDel100Skin();
			String [] skins=config.getStringArray(this.getSkinResults());
			for(int i=0;i<skins.length;i++) {
				try {
					BeanUtils.setProperty(this, skins[i], i+1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			setDel110Skin();
		}
	}


	public void setDel100Skin() {
		this.DZ_RZ_N_T = 100;
		this.DZ_RZ_N_W = 100;
		this.DZ_RZ_P_T = 100;
		this.DZ_RZ_P_W = 100;
		this.DZ_RQ_N_T = 100;
		this.DZ_RQ_N_W = 100;
		this.DZ_RQ_P_W = 100;
		this.DZ_RQ_P_T = 100;
		this.DZ_SQ_N_T = 100;
		this.DZ_SQ_N_W = 100;
		this.DZ_SQ_P_W = 100;
		this.DZ_SQ_P_T = 100;
		this.DZ_SZ_P_T = 100;
		this.DZ_SZ_P_W = 100;
		this.DZ_SZ_N_W = 100;
		this.DZ_SZ_N_T = 100;
		this.DQ_RZ_N_T = 100;
		this.DQ_RZ_N_W = 100;
		this.DQ_RZ_P_T = 100;
		this.DQ_RZ_P_W = 100;
		this.DQ_RQ_N_T = 100;
		this.DQ_RQ_N_W = 100;
		this.DQ_RQ_P_W = 100;
		this.DQ_RQ_P_T = 100;
		this.DQ_SQ_N_T = 100;
		this.DQ_SQ_N_W = 100;
		this.DQ_SQ_P_W = 100;
		this.DQ_SQ_P_T = 100;
		this.DQ_SZ_P_T = 100;
		this.DQ_SZ_P_W = 100;
		this.DQ_SZ_N_W = 100;
		this.DQ_SZ_N_T = 100;
		this.OQ_RZ_N_T = 100;
		this.OQ_RZ_N_W = 100;
		this.OQ_RZ_P_T = 100;
		this.OQ_RZ_P_W = 100;
		this.OQ_RQ_N_T = 100;
		this.OQ_RQ_N_W = 100;
		this.OQ_RQ_P_W = 100;
		this.OQ_RQ_P_T = 100;
		this.OQ_SQ_N_T = 100;
		this.OQ_SQ_N_W = 100;
		this.OQ_SQ_P_W = 100;
		this.OQ_SQ_P_T = 100;
		this.OQ_SZ_P_T = 100;
		this.OQ_SZ_P_W = 100;
		this.OQ_SZ_N_W = 100;
		this.OQ_SZ_N_T = 100;
		this.OZ_RZ_N_T = 100;
		this.OZ_RZ_N_W = 100;
		this.OZ_RZ_P_T = 100;
		this.OZ_RZ_P_W = 100;
		this.OZ_RQ_N_T = 100;
		this.OZ_RQ_N_W = 100;
		this.OZ_RQ_P_W = 100;
		this.OZ_RQ_P_T = 100;
		this.OZ_SQ_N_T = 100;
		this.OZ_SQ_N_W = 100;
		this.OZ_SQ_P_W = 100;
		this.OZ_SQ_P_T = 100;
		this.OZ_SZ_P_T = 100;
		this.OZ_SZ_P_W = 100;
		this.OZ_SZ_N_W = 100;
		this.OZ_SZ_N_T = 100;
	}
	public void setDel110Skin() {
		this.DZ_RZ_N_T = 110;
		this.DZ_RZ_N_W = 110;
		this.DZ_RZ_P_T = 110;
		this.DZ_RZ_P_W = 110;
		this.DZ_RQ_N_T = 110;
		this.DZ_RQ_N_W = 110;
		this.DZ_RQ_P_W = 110;
		this.DZ_RQ_P_T = 110;
		this.DZ_SQ_N_T = 110;
		this.DZ_SQ_N_W = 110;
		this.DZ_SQ_P_W = 110;
		this.DZ_SQ_P_T = 110;
		this.DZ_SZ_P_T = 110;
		this.DZ_SZ_P_W = 110;
		this.DZ_SZ_N_W = 110;
		this.DZ_SZ_N_T = 110;
		this.DQ_RZ_N_T = 110;
		this.DQ_RZ_N_W = 110;
		this.DQ_RZ_P_T = 110;
		this.DQ_RZ_P_W = 110;
		this.DQ_RQ_N_T = 110;
		this.DQ_RQ_N_W = 110;
		this.DQ_RQ_P_W = 110;
		this.DQ_RQ_P_T = 110;
		this.DQ_SQ_N_T = 110;
		this.DQ_SQ_N_W = 110;
		this.DQ_SQ_P_W = 110;
		this.DQ_SQ_P_T = 110;
		this.DQ_SZ_P_T = 110;
		this.DQ_SZ_P_W = 110;
		this.DQ_SZ_N_W = 110;
		this.DQ_SZ_N_T = 110;
		this.OQ_RZ_N_T = 110;
		this.OQ_RZ_N_W = 110;
		this.OQ_RZ_P_T = 110;
		this.OQ_RZ_P_W = 110;
		this.OQ_RQ_N_T = 110;
		this.OQ_RQ_N_W = 110;
		this.OQ_RQ_P_W = 110;
		this.OQ_RQ_P_T = 110;
		this.OQ_SQ_N_T = 110;
		this.OQ_SQ_N_W = 110;
		this.OQ_SQ_P_W = 110;
		this.OQ_SQ_P_T = 110;
		this.OQ_SZ_P_T = 110;
		this.OQ_SZ_P_W = 110;
		this.OQ_SZ_N_W = 110;
		this.OQ_SZ_N_T = 110;
		this.OZ_RZ_N_T = 110;
		this.OZ_RZ_N_W = 110;
		this.OZ_RZ_P_T = 110;
		this.OZ_RZ_P_W = 110;
		this.OZ_RQ_N_T = 110;
		this.OZ_RQ_N_W = 110;
		this.OZ_RQ_P_W = 110;
		this.OZ_RQ_P_T = 110;
		this.OZ_SQ_N_T = 110;
		this.OZ_SQ_N_W = 110;
		this.OZ_SQ_P_W = 110;
		this.OZ_SQ_P_T = 110;
		this.OZ_SZ_P_T = 110;
		this.OZ_SZ_P_W = 110;
		this.OZ_SZ_N_W = 110;
		this.OZ_SZ_N_T = 110;
	}
	
	/**
	 * 设置属性为null用于前台展现
	 */
	public void setDelNullSkin() {
		this.DZ_RZ_N_T = null;
		this.DZ_RZ_N_W = null;
		this.DZ_RZ_P_T = null;
		this.DZ_RZ_P_W = null;
		this.DZ_RQ_N_T = null;
		this.DZ_RQ_N_W = null;
		this.DZ_RQ_P_W = null;
		this.DZ_RQ_P_T = null;
		this.DZ_SQ_N_T = null;
		this.DZ_SQ_N_W = null;
		this.DZ_SQ_P_W = null;
		this.DZ_SQ_P_T = null;
		this.DZ_SZ_P_T = null;
		this.DZ_SZ_P_W = null;
		this.DZ_SZ_N_W = null;
		this.DZ_SZ_N_T = null;
		this.DQ_RZ_N_T = null;
		this.DQ_RZ_N_W = null;
		this.DQ_RZ_P_T = null;
		this.DQ_RZ_P_W = null;
		this.DQ_RQ_N_T = null;
		this.DQ_RQ_N_W = null;
		this.DQ_RQ_P_W = null;
		this.DQ_RQ_P_T = null;
		this.DQ_SQ_N_T = null;
		this.DQ_SQ_N_W = null;
		this.DQ_SQ_P_W = null;
		this.DQ_SQ_P_T = null;
		this.DQ_SZ_P_T = null;
		this.DQ_SZ_P_W = null;
		this.DQ_SZ_N_W = null;
		this.DQ_SZ_N_T = null;
		this.OQ_RZ_N_T = null;
		this.OQ_RZ_N_W = null;
		this.OQ_RZ_P_T = null;
		this.OQ_RZ_P_W = null;
		this.OQ_RQ_N_T = null;
		this.OQ_RQ_N_W = null;
		this.OQ_RQ_P_W = null;
		this.OQ_RQ_P_T = null;
		this.OQ_SQ_N_T = null;
		this.OQ_SQ_N_W = null;
		this.OQ_SQ_P_W = null;
		this.OQ_SQ_P_T = null;
		this.OQ_SZ_P_T = null;
		this.OQ_SZ_P_W = null;
		this.OQ_SZ_N_W = null;
		this.OQ_SZ_N_T = null;
		this.OZ_RZ_N_T = null;
		this.OZ_RZ_N_W = null;
		this.OZ_RZ_P_T = null;
		this.OZ_RZ_P_W = null;
		this.OZ_RQ_N_T = null;
		this.OZ_RQ_N_W = null;
		this.OZ_RQ_P_W = null;
		this.OZ_RQ_P_T = null;
		this.OZ_SQ_N_T = null;
		this.OZ_SQ_N_W = null;
		this.OZ_SQ_P_W = null;
		this.OZ_SQ_P_T = null;
		this.OZ_SZ_P_T = null;
		this.OZ_SZ_P_W = null;
		this.OZ_SZ_N_W = null;
		this.OZ_SZ_N_T = null;
	}

}

