package cn.bevol.util.client;


import cn.bevol.util.JsonUtils;
import cn.bevol.util.http.HttpUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by owen on 16-9-19.
 */
public class AdClient {
      //  private String AD_SERVER_HOST="http://127.0.0.1:8088";
	  //private String AD_SERVER_HOST = "http://tstatic.bevol.cn:8088";//公网
    private String AD_SERVER_HOST = "http://api.static.internal.bevol.cn";//内网
    private String module = "";

    public AdClient(String module) {
        setModule(module);
    }

    /**
     * 根据类型查询广告位 V2
     * @param name
     * @param type
     * @param classifyId 分类ID eg:某一个成分下的广告、某一发现的下的广告
     * @return
     */
    public Map<String,Object> findAd(Integer name,String type,Integer classifyId ,String positionType) {
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("name", name);
        if (!StringUtils.isEmpty(type)) {
            value.put("type", type);
        }
        if(classifyId!=null&&classifyId>0){
            value.put("classifyId", classifyId);
        }
        if(!StringUtils.isEmpty(positionType)){
            value.put("positionType", positionType);
        }
        try {
            String resultJson = HttpUtils.post(AD_SERVER_HOST + "/static/ad/findAd2", value);
            Map<String,Object> map =   JsonUtils.toMap(resultJson);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    /**
     * 根据类型查询广告位 V1
     * @param name
     * @param type
     * @param classifyId 分类ID eg:某一个成分下的广告、某一发现的下的广告
     * @return
     */
    public Map<String,Object> findAd(Integer name,String type,Integer classifyId) {
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("name", name);
        if (!StringUtils.isEmpty(type)) {
            value.put("type", type);
        }
        if(classifyId!=null&&classifyId>0){
            value.put("classifyId", classifyId);
        }
        try {
            String resultJson = HttpUtils.post(AD_SERVER_HOST + "/static/ad/findAd", value);
            Map<String,Object> map =   JsonUtils.toMap(resultJson);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 根据类型查询广告位(临时)
     * @param name
     * @param type
     * @param classifyId 分类ID eg:某一个成分下的广告、某一发现的下的广告
     * @return
     */
    public Map<String,Object> findAdTmp(Integer name,String type,Integer classifyId ) {
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("name", name);
        if (!StringUtils.isEmpty(type)) {
            value.put("type", type);
        }
        if (classifyId != null && classifyId > 0) {
            value.put("classifyId", classifyId);
        }
        String resultJson = "{\"ret\":\"0\",\"result\":[{\"id\":3,\"orientation\":3,\"isReplace\":0,\"imgUrl\":\"https://img2.bevol.cn/Goods/source/5628a86300ad7.jpg\",\"entityId\":140,\"entityType\":6}," + "{\"id\":4,\"orientation\":4,\"isReplace\":0,\"imgUrl\":\"https://img2.bevol.cn/Goods/source/5628a86300ad7.jpg\",\"entityId\":141,\"entityType\":12}]}";
        try {
            Map<String, Object> map = JsonUtils.toMap(resultJson);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
    /**
     * 添加广告记录
     * @param id
     */
    public void addADLog(Integer id,String positionType) {
        HashMap<String, Object> value = new HashMap<String, Object>();
       if(id!=null&&id>0) {
          value.put("ad_id", id);
       }
        if(!StringUtils.isEmpty(positionType)){
            value.put("position_type",positionType);
        }
        try {
            String resultJson = HttpUtils.post(AD_SERVER_HOST + "/static/ad/log/add", value);
        }  catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void initAdlog(){
        try {
            HashMap<String, Object> value = new HashMap<String, Object>();
            String resultJson = HttpUtils.post(AD_SERVER_HOST + "/static/ad/log/init",value);
        }  catch (Exception e) {
            e.printStackTrace();

        }
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public static void main(String[] arg) throws NoSuchFieldException {
        System.out.println(new AdClient("ad").findAdTmp(1, "",0));

        final AdClient ad = new AdClient("ad");
    }
    class TempAd{
        private Long id;
        private Integer orientation;
        private Integer isReplace;
        private String imgUrl;
        private Integer entityId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getOrientation() {
            return orientation;
        }

        public void setOrientation(Integer orientation) {
            this.orientation = orientation;
        }

        public Integer getIsReplace() {
            return isReplace;
        }

        public void setIsReplace(Integer isReplace) {
            this.isReplace = isReplace;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public Integer getEntityId() {
            return entityId;
        }

        public void setEntityId(Integer entityId) {
            this.entityId = entityId;
        }
    }

}
