package cn.bevol.app.controller;

import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.dao.mapper.SearchOldMapper;
import cn.bevol.app.entity.dto.SeachComposition;
import cn.bevol.app.entity.model.Goods;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class LonginControllerTest extends BaseControllerTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    SearchOldMapper searchOldMapper;

    @Autowired
    GoodsOldMapper goodsOleMapper;


    /**
     * 账号登录
     */
    @Test
    public void accountLoginTest(){
        //1.getVcode2_9_2获取短信验证码
        //2.图片验证码
        //3.确认图片验证
        //4.对比短信验证码
        //5.注册
        //6.登录
        //7.修改/找回密码等操作
    }

    /**
     * 获取短信验证码
     */
    @Test
    public void getVcode2_9_2Test(){
        Map paramMap=new HashMap();
        paramMap.put("method","/app/vcode3");
        paramMap.put("account","13469958973");
        paramMap.put("type",0);
        paramMap.put("requestMethod",post);
        Map reseponseMap=this.controllerMethodTest(paramMap);
        super.returnZero(reseponseMap,"获取账号验证码接口有误-----");
    }

    /**
     * 获取图片验证码
     */
    @Test
    public void createImgVcodeTest(){
        Map paramMap=new HashMap();
        paramMap.put("method","/app/vcode2/refresh");
        paramMap.put("account","13469958973");
        paramMap.put("type",0);
        paramMap.put("requestMethod",get);
        Map reseponseMap=this.controllerMethodTest(paramMap);
        //super.returnZero(reseponseMap,"/app/vcode2/refresh验证码出错-------");
    }

    /**
     * 账号注册
     */
    @Test
    public void accountRegister2_6Test(){
        String account="13469958974";
        int type=0;
        String password="123456";
        //获取短信验证码
        Map paramMap=new HashMap();
        paramMap.put("method","/app/vcode3");
        paramMap.put("account",account);
        paramMap.put("type",type);
        paramMap.put("requestMethod",post);
        Map reseponseMap=this.controllerMethodTest(paramMap);
        if(null==reseponseMap || null==reseponseMap.get("ret")){
            Assert.fail("/app/vcode3验证码接口没有返回值-------");
        }
        int ret=Integer.parseInt(reseponseMap.get("ret")+"");
        if(ret!=0 && ret!=10){
            Assert.fail("/app/vcode3验证码接口返回值出错-------");
        }

        if(ret==10){
            //获取短信验证码之前,需要图片验证码 TODO
            paramMap=new HashMap();
            paramMap.put("method","/app/vcode2/refresh");
            paramMap.put("account",account);
            paramMap.put("type",type);
            paramMap.put("requestMethod",get);
            reseponseMap=this.controllerMethodTest(paramMap);
            // super.returnZero(reseponseMap,"获取图片验证码接口有误-----");

            //获取图片验证码 Todo
            ResultActions resultActions=(ResultActions)reseponseMap.get("resultActions");
            HttpSession session=resultActions.andReturn().getRequest().getSession();
            //this.session=(MockHttpSession) resultActions.andReturn().getRequest().getSession();
            String sessionImgVcode = (String)session.getAttribute("imgVcode");
            String[] sessionImgVcodes=sessionImgVcode.split("_");
            String imgVcode=sessionImgVcodes[0];
            //对比图片验证码
            paramMap=new HashMap();
            paramMap.put("method","/app/vcode2/submit");
            paramMap.put("account",account);
            paramMap.put("type",type);
            paramMap.put("imag_vcode",imgVcode);
            paramMap.put("requestMethod",post);
            reseponseMap=this.controllerMethodTest(paramMap);
            super.returnZero(reseponseMap,"对比图片验证码接口有误-----");
        }

        Map result=(Map)reseponseMap.get("result");
        //验证手机验证码
        paramMap=new HashMap();
        paramMap.put("method","/app/vcode/valid");
        paramMap.put("account",account);
        paramMap.put("type",type);
        paramMap.put("vcode",result.get("vcode"));
        paramMap.put("requestMethod",post);
        reseponseMap=this.controllerMethodTest(paramMap);
        super.returnZero(reseponseMap,"/app/vcode/valid对比手机验证码接口出错-------");

        /*ResultActions resultActions=(ResultActions)reseponseMap.get("resultActions");
        this.session=(MockHttpSession) resultActions.andReturn().getRequest().getSession();*/
        //注册
        paramMap=new HashMap();
        paramMap.put("method","/app/register2");
        paramMap.put("account",account);
        paramMap.put("password",password);
        paramMap.put("requestMethod",post);
        paramMap.put("session",this.session);
        reseponseMap=this.controllerMethodTest(paramMap);
        super.returnZero(reseponseMap,"/app/register注册接口出错-------");
    }

    /**
     * 微信登录
     */
    @Test
    public void wxLoginTest(){
        //1.getVcode2_9_2获取短信验证码
        //2.图片验证码
        //3.确认图片验证
        //4.对比短信验证码
        //5.注册
        //6.登录
        //7.修改/找回密码等操作
    }

    @Test
    public void goodsTemp(){
        List<SeachComposition> ruleCpsList= searchOldMapper.ruleOutComposition();
        List<Goods> goodsList=goodsOleMapper.tempGoods();
        List<Map<Object,Object>> mapList=new ArrayList<Map<Object,Object>>();
        Map map=null;
        for(Goods goods:goodsList){
            String[] cpss=goods.getCps().split(",");
            map=new HashMap();
            map.put("goods_id",goods.getId());
            StringBuffer goodsRuleCps=new StringBuffer();
            StringBuffer ruleNames=new StringBuffer();
            for(int i=0;i<cpss.length;i++){
                for(SeachComposition sc:ruleCpsList){
                    String ruleCps=sc.getComposition_ids();
                    String ruleName=sc.getTitle();
                    if((","+ruleCps+",").contains(","+cpss[i]+",") && sc.getId()==9){
                        goodsRuleCps.append(cpss[i]).append(",");
                        if(!(","+ruleNames.toString()+",").contains(","+ruleName+",")){
                            ruleNames.append(ruleName+",");
                        }
                        if(StringUtils.isBlank(ruleNames)){
                            ruleNames.append(ruleName+",");
                        }
                    }
                }
            }
            String goodsRules="";
            String goodsRuleCpss="";
            if(StringUtils.isNotBlank(ruleNames)){
                goodsRuleCpss=goodsRuleCps.substring(0,goodsRuleCps.length()-1);
                goodsRules=ruleNames.substring(0,ruleNames.length()-1);
                String updateSql="update temp_goods_copy set goodsRuleCpss='"+goodsRuleCpss+"' where goods_id="+goods.getId();
                goodsOleMapper.update(updateSql);
            }


        }

    }

    @Test
    public void goodsTemp2(){
        Map paramMap=null;
        Map reseponseMap=null;
        List<Goods> goodsList=goodsOleMapper.getGoodsByCategory2();
        for(Goods goods:goodsList){
            paramMap= new HashMap();
            paramMap.put("method","/entity/info2/goods");
            paramMap.put("mid",goods.getMid());
            paramMap.put("requestMethod",post);
            reseponseMap=this.controllerMethodTest(paramMap);
            boolean isAjs=false;
            Map ajsCps=new HashMap();
            if(Integer.parseInt(reseponseMap.get("ret")+"")==0){
                Map result=(HashMap)reseponseMap.get("result");
                if(null!=result.get("entityInfo")){
                    Map entityInfo=(Map) result.get("entityInfo");
                    List<Map> effectList=(ArrayList) entityInfo.get("effect");
                    if(null!=effectList){
                        for(Map map:effectList){
                            if("氨基酸表活成分".equals(map.get("displayName")+"") && Integer.parseInt((map.get("num")+""))>0){
                                isAjs=true;
                                ajsCps=map;
                            }

                        }
                    }
                }
            }
            if(isAjs){
                List<Long> effectList=(ArrayList)ajsCps.get("compositionIds");
                StringBuffer sb=new StringBuffer();
                for(int i=0;i<effectList.size();i++){
                    sb.append(effectList.get(i)).append(",");
                }
                String acps=sb.substring(0,sb.length()-1);
                String updateSql="update temp_goods_copy set ajs='"+1+"',ajsCps='"+acps+"' where goods_id="+goods.getId();
                goodsOleMapper.update(updateSql);
            }


        }


    }



}
