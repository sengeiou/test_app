package cn.bevol.model.user;

import java.io.Serializable;

public class UserLocation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3775069411233583775L;

	private String ip;
	
	private Long visitTime;
	
	private Double[] location;
	
    
	public UserLocation() {
		super();
	}

	public UserLocation(String ip, Long visitTime, Double[] location) {
		super();
		this.ip = ip;
		this.visitTime = visitTime;
		this.location = location;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Long visitTime) {
		this.visitTime = visitTime;
	}

	public Double[] getLocation() {
		return location;
	}

	public void setLocation(Double[] location) {
		this.location = location;
	}
	
}
