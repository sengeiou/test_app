package cn.bevol.statics.entity.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class UserAddressInfo {

    /**
     * 省/自治区/直辖市
     */
    private String province;

    /**
     * 地级市/县级市/县
     */
    private String city;

    /**
     * 区/镇
     */
    private String district;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 邮政编码
     */
    private String zip;

    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 收货人
     */
    private String receiver;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

 
}
