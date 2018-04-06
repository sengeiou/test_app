package cn.bevol.app.controller;

import cn.bevol.app.service.UserService;
import cn.bevol.model.user.UserAddressInfo;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.JsonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    public MockHttpSession session;

    public static final String get="get";
    public static final String post="post";

    public String  responseJsonStr="";

    @Before
    public void setup() {
        this.session = new MockHttpSession();
    }

    /**
     * 对controller测试
     * @throws Exception
     */
    public Map controllerMethodTest(Map<Object,Object> paramMap){
        if(StringUtils.isBlank(paramMap.get("method")+"") || StringUtils.isBlank(paramMap.get("requestMethod")+"")){
            Assert.fail("uri或者requestMethod为空----------");
        }
        String method=paramMap.get("method")+"";
        String requestMethod=paramMap.get("requestMethod")+"";
        StringBuffer param= new StringBuffer();
        String uri=method;
        //拼接参数
        for(Map.Entry<Object,Object> entry:paramMap.entrySet()) {
            if(!"method".equals(entry.getKey()+"")){
                if(param.length()==0){
                    param.append("?"+entry.getKey()+"="+entry.getValue());
                }else{
                    param.append("&"+entry.getKey()+"="+entry.getValue());
                }
            }
        }

        if(param.length()>0){
            uri+=param;
        }

        try{
            MockHttpServletRequestBuilder mockHttpServletRequestBuilder=null;
            //请求方式判断
            if(post.equals(requestMethod)){
                mockHttpServletRequestBuilder=MockMvcRequestBuilders.post(uri);
            }else if(get.equals(requestMethod)){
                mockHttpServletRequestBuilder=MockMvcRequestBuilders.get(uri);
            }
            if(null!=paramMap.get("session")){
                mockHttpServletRequestBuilder.session((MockHttpSession)paramMap.get("session"));
            }
            //开始请求
            ResultActions resultActions=mvc.perform(mockHttpServletRequestBuilder)
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print());
            //特殊返回类型处理
            /*if("/app/vcode2/refresh".equals(method)){
                resultActions .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
            }else{
                resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.ret").value(0));;
            }*/
            //打印返回的信息
            String responseJsonStr=resultActions.andReturn().getResponse().getContentAsString();
            Map reseponseMap=new HashMap();
            if(!"/app/vcode2/refresh".equals(method)){
                reseponseMap= JsonUtils.toMap(responseJsonStr);
            }
            reseponseMap.put("resultActions",resultActions);
            return reseponseMap;
        }catch (Exception e){
            System.out.println("--------error--------uri:"+uri);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据cookie获取用户id
     * @param request
     * @return
     */
    public long getUserId(HttpServletRequest request) {
        Long id=CommonUtils.getLoginCookieById(request);
        return id;
    }


    /**
     * 根据cookie获取用户id
     * @param request
     * @return
     */
    public long getUserId2(HttpServletRequest request) {
        Long id=CommonUtils.getLoginCookieById2(request);
        return id;
    }

    /**
     * todo(规则)
     * 判断返回值是否有误
     * @param reseponseMap
     */
    public void returnZero(Map reseponseMap,String erroMsg){
        if(null==reseponseMap || 0!=Integer.parseInt(reseponseMap.get("ret")+"")){
            Assert.fail(erroMsg);
        }
    }


    /**
     * 设置用户cookie和session
     * @param request
     * @param response
     * @param userInfo
     */
    public void setUser(HttpServletRequest request,HttpServletResponse response,UserInfo userInfo) {
        //设置cookies
        CommonUtils.setLoginCookieId(response,userInfo.getId(),null);
        request.getSession().setAttribute("userInfo", userInfo);
    }

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    public UserInfo getUser(HttpServletRequest request) {
        //设置cookies
        long id=this.getUserId(request);
        UserInfo userInfo=null;
        if(id>0) {
            userInfo=this.getUserInfo(request);
            if(null==userInfo||null==userInfo.getId()) {
                ReturnData rd=userService.getUserById(id);
                if(rd.getRet()==0) {
                    userInfo=(UserInfo)rd.getResult();
                    userService.userInit(userInfo);
                    request.getSession().setAttribute("userInfo",userInfo );
                }

            }
        }
        return userInfo;
    }

    public void getUserAddres(LinkedHashMap m){
        try{
            List<UserAddressInfo> addre=new ArrayList<UserAddressInfo>();
            if(null!=m.get("userAddressInfos") && m.get("userAddressInfos").getClass().isArray()){
                UserAddressInfo[] userAddressInfos=(UserAddressInfo[])m.get("userAddressInfos");
                addre= Arrays.asList(userAddressInfos);
            } else if(null!=m.get("userAddressInfos") && m.get("userAddressInfos") instanceof ArrayList){
                addre=(ArrayList<UserAddressInfo>)m.get("userAddressInfos");
            }
            if(null!=addre && addre.size()>0){
                UserAddressInfo[] userAddressInfos=new UserAddressInfo[addre.size()];
                for(int i=0;i<addre.size();i++){
                    UserAddressInfo uai=new UserAddressInfo();
                    BeanUtils.populate(uai,CommonUtils.ObjectToMap(addre.get(i)));
                    userAddressInfos[i]=uai;
                }
                m.put("userAddressInfos", userAddressInfos);
            }
        }catch (Exception e){
            Map map=new HashMap();
            map.put("method", "BaseController.getUserAddres");
            new LogException(e,map);
        }

    }

    private UserInfo getUserInfo(HttpServletRequest request) {
        try {

            // todo mongodb 作为session的时候  需要转型
            request.getSession().getAttribute("userInfo");
            Object o=request.getSession().getAttribute("userInfo");
            UserInfo userInfo=new UserInfo();
            if(o!=null) {
                if(o instanceof LinkedHashMap) {
                    LinkedHashMap m=(LinkedHashMap)o;
                    this.getUserAddres(m);
                    BeanUtils.populate(userInfo,m);
                    userInfo.setId(Long.parseLong(m.get("_id")+""));
                    userInfo.set_id(null);
                    if(m==null||m.size()==0) {
                        return null;
                    }
                } else {
                    userInfo=(UserInfo) o;
                }
            }
            if(userInfo.getId()==null){
                return null;
            }
            return userInfo;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "BaseController.getUserInfo");
            new LogException(e,map);
        }
        return null;
    }
    /**
     * 重新加载用户
     * @param request
     */
    public ReturnData reloadUser(HttpServletRequest request) {
        UserInfo userInfo=this.getUserInfo(request);
        if(userInfo!=null){
            request.getSession().removeAttribute("userInfo");
        }
        userInfo= getUser(request);
        return new ReturnData(userInfo);
    }
    /**
     * 移去用户cookie和session
     * @param request
     * @param response
     */
    public void removeUser(HttpServletRequest request, HttpServletResponse response) {
        CommonUtils.removeCookieByName(response,"logincode");
        request.getSession().removeAttribute("userInfo");
    }
}
