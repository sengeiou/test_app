package cn.bevol.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class CACHE_NAME {
    /**
     * namespace
     */
    public static final String NAMESPACE = "bevol";

    /**
     * wx接口使用
     */
    public static String TICKET = "ticket";
    public static String ACCESS_TOKEN = "access_token";
    public static String OAUTH_ACCESS_TOKEN="oauth_access_token";
    public static String ACCOUNT="bevol_mlxxapp";
    public static String WX_KEYWORD_ARTILE ="wx_kw_artile";

    /**
     * cache queue   ex：  [x]_m
     */
    public static final String FOREVER_CACHE_QUEUE = "forever";
    public static final String FIVE_MINUTE_CACHE_QUEUE = "5_m";
    public static final String MATCH_ALL_CACHE_QUEUE = "*_m";
    public static final String THIRTY_MINUTE_CACHE_QUEUE = "30_m";
    public static final String THIRTY_2DAY_CACHE_QUEUE = "1440_m";

    public static final String FOREVER="f_";

    public static final String TIME="m_";

    /**
     * cache instance
     * 单个成分和成分的使用目的缓存
     */
    public static final String INSTANCE_COMPOSITION_ID_OR_MID_PREFIX = TIME+"com_mid_or_id2";
    public static final String INSTANCE_PRODUCT_ID_OR_MID_PREFIX = TIME+"pro_mid_or_id2";

    /**
     * 正式环境缓存
     */
    public static String VERSION = "3.3.0.33";

    /**
     * 测试环境缓存
     */
    //public static final String VERSION = "t_3.3.0.4";

    /**
     * 产品mid
     */
    @Value("根据产品mid清除单个产品缓存")
    public static final String INSTANCE_PRODUCT_MID_PREFIX = TIME+"pro_mid";

    /**
     * 成分使用目的
     */
    @Value("清除成分使用目的缓存")
    public static final String INSTANCE_COMPOSITION_USED_PREFIX = TIME+"cps_used";

    @Value("清除功效关系关系列表的缓存")
    public static final String INSTANCE_GOODS_EFFECT_USED_PREFIX = TIME+"geet";

    @Value("清除产品目的功效关系列表的缓存")
    public static final String INSTANCE_GOODS_USED_EFFECT_PREFIX = TIME+"geut";

    @Value("清除根据模糊匹配成分名的成分列表缓存")
    public static final String INSTANCE_COMPOSITION_LIKE_LIST_PREFIX = TIME+"like_cps_list";

    @Value("清除所有成分的缓存")
    public static final String INSTANCE_COMPOSITION_LIST_PREFIX = TIME+"all_cps_list";

    @Value("清除v2.6以前文章的缓存")
    public static final String INSTANCE_FIND_ARTICLE_PREFIX = TIME+"find_article_2_6";

    @Value("清除产品计算时16种肤质的缓存")
    public static final String INSTANCE_GOODS_SKIN_PREFIX = TIME+"goods_skin";

    @Value("清除产品计算时的普通分类规则的缓存")
    public static final String INSTANCE_GOODS_COMMON_CATEGORY_PREFIX = TIME+"goods_common_category";

    @Value("清除产品计算时的特殊分类规则的缓存")
    public static final String INSTANCE_GOODS_SPECIAL_CATEGORY_PREFIX = TIME+"goods_special_category";

    @Value("清除产品计算时的产品标签规则列表的缓存")
    public static final String INSTANCE_GOODS_RULE_PREFIX = TIME+"goods_rule";

    @Value("清除产品计算时的标签核心成分列表的缓存")
    public static final String INSTANCE_GOODS_TAG_CPS_PREFIX = TIME+"goods_tag_cps";

    @Value("清除产品计算时的不想要的成分组列表的缓存")
    public static final String INSTANCE_GOODS_RULEOUT_CPS_PREFIX = TIME+"goods_ruleOut_cps";

    @Value("清除产品计算时的原始产品分类列表的缓存")
    public static final String INSTANCE_GOODS_CATEGORY_PREFIX = TIME+"goods_category";


    /**
     * 评论总数量
     */
    @Value("清除评论总数量的缓存")
    public static final String INSTANCE_COMMENT_TOTAL_PREFIX = TIME+"comment_tt";


    /**
     * 2.5以以前的评论主评论列表
     */
    public static final String INSTANCE_COMMENT_OLDLIST_PREFIX = TIME+"comment_ol";


    /**
     * 2.5以后的评论子评论列表
     */
    @Value("清除2.5以后的评论子评论列表的缓存")
    public static final String INSTANCE_COMMENT_SUBLIST25_PREFIX = TIME+"comment_sl25";

    /**
     * 2.5以后的评论主评论列表
     */
    public static final String INSTANCE_COMMENT_MAINLIST25_PREFIX = TIME+"comment_ml25";


    @Value("对比广场列表")
    public static final String INSTANCE_COMPARE_LIST_PREFIX = TIME+"compare_ls";

    @Value("话题对比列表")
    public static final String INSTANCE_DISCUSS_COMPARE_LIST_PREFIX = TIME+"discuss_ls";

    /**
     * 2.7以后的评论主评论列表
     */
    @Value("清除2.7以后的评论主评论列表的缓存")
    public static final String INSTANCE_COMMENT_MAINLIST2_25_PREFIX = TIME+"comment2_ml25";

    /**
     * 3.0源生产品详情中一级评论列表
     */
    @Value("清除3.0源生产品详情中一级评论列表的缓存")
    public static final String INSTANCE_SOURCE_COMMENT_MAINLIST_PREFIX = TIME+"source_comment_list";


    /**
     *  文章详情
     */
    @Value("清除文章详情的缓存")
    public static final String INSTANCE_FIND_INFO_PREFIX = TIME+"find_info";

    /**
     *  文章标签
     */
    @Value("清除文章标签的缓存")
    public static final String INSTANCE_FIND_LABLE_PREFIX = TIME+"find_fl";

    /**
     * 文章分类
     */
    @Value("清除文章分类的缓存")
    public static final String INSTANCE_FIND_TYPE_PREFIX = TIME+"find_ft";

    /**
     * 发现中的行业资讯列表
     */
    @Value("清除pc文章行业资讯列表的缓存")
    public static final String INSTANCE_INDUSTRY_PREFIX = TIME+"industry_ls";

    /**
     * 发现列表
     */
    @Value("清除发现列表的缓存")
    public static final String INSTANCE_FIND_LIST_PREFIX = TIME+"find_ls";


    /**
     * 往期精选 列表
     */
    public static final String INSTANCE_FIND_OLDAARTICLE_PREFIX = TIME+"oldatcl_ls";

    /**
     * v2.9 往期精选 列表
     */
    @Value("清除精选点评列表的缓存")
    public static final String INSTANCE_FIND_OLDAARTICLE2_PREFIX = TIME+"oldatcl2_ls";


    /**
     * 根据id 获取清单
     */
    @Value("根据id清除清单的缓存")
    public static final String  INSTANCE_LISTS_ID_PREFIX = TIME+"list_id";



    @Value("清除话题/清单列表的缓存")
    public static final String INSTANCE_LISTS3_LIST_PREFIX = TIME+"list3.1_ls";

    /**
     *  同肤质喜欢的产品
     */
    @Value("清除同肤质喜欢的产品的缓存")
    public static final String  INSTANCE_SKIN_LIKE_GOODS_PREFIX = TIME+"skin_lg";



    /**
     *  肤质测试题目
     */
    @Value("清除肤质测试题文案的缓存")
    public static final String  INSTANCE_SKIN_TEST_DESC_PREFIX = FOREVER+"skin_td";

    /**
     * 心得列表
     */
    @Value("清除心得列表的缓存")
    public static final String  INSTANCE_USERPART_LIST_PREFIX = TIME+"upart_ls";

    /**
     * 试用报告列表
     */
    @Value("清除试用报告列表的缓存")
    public static final String  INSTANCE_USERPART_APPGOODS_LIST_PREFIX = TIME+"upart_ag";

    /**
     * 福利社全部活动列表
     */
    @Value("清除福利社全部活动列表的缓存")
    public static final String  INSTANCE_APPGOODS_LIST_PREFIX = TIME+"alyg_ls";


    /**
     * 根据id 获取黑名单
     */
    @Value("根据用户id清除黑名单的缓存")
    public static final String  INSTANCE_USERBLACKLIST_ID_PREFIX = TIME+"userBlackList_id";

    /**
     * 每日注册人数
     */
    @Value("清除每日通过短信注册的人数的缓存")
    public static final String  INSTANCE_REGISTERNUM_DAY_PREFIX = FOREVER+"registerNum_day";

    /**
     * 修行社首页
     */
    public static final String INSTANCE_SNS_INDEX_PREFIX = TIME+"sns_i";

    /**
     * 新修行社
     */
    @Value("清除修行社首页的缓存")
    public static final String INSTANCE_SNS_INDEX2_PREFIX = TIME+"sns_i2";

    /**
     * 单个发现
     */
    @Value("清除我的列表中文章的信息")
    public static final String INSTANCE_FIND_ID_PREFIX = TIME+"find_id";

    /**
     * 产品计算多分类缓存
     */
    @Value("清除产品多分类缓存 ")
    public static final String INSTANCE_POLY_CATEGORY_PREFIX = TIME+"poly_category";

    /**
     *
     * initApp  key
     */
    @Value("清除初始化接口3.1")
    public static final String INSTANCE_INITAPP_8_PREFIX = TIME+"initApp_8";
    @Value("清除初始化接口3.0")
    public static final String INSTANCE_INITAPP_7_PREFIX = TIME+"initApp_7";

    public static final String INSTANCE_INITAPP_6_PREFIX = TIME+"initApp_6";
    public static final String INSTANCE_INITAPP_5_PREFIX = TIME+"initApp_5";
    public static final String INSTANCE_INITAPP_4_PREFIX = TIME+"initApp_4";
    public static final String INSTANCE_INITAPP_3_PREFIX = TIME+"initApp_3";
    public static final String INSTANCE_INITAPP_2_PREFIX = TIME+"initApp_2";
    public static final String INSTANCE_INITAPP_1_PREFIX = TIME+"initApp_1";

    /**
     * index
     */
    @Value("清除3.1版本初始化接口")
    public static final String INSTANCE_INDEX_6_PREFIX = TIME+"index_6";
    public static final String INSTANCE_INDEX_7_PREFIX = TIME+"index_7";
    @Value("清除3.0版本初始化接口")
    public static final String INSTANCE_INDEX_5_PREFIX = TIME+"index_5";
    public static final String INSTANCE_INDEX_4_PREFIX = TIME+"index_4";
    public static final String INSTANCE_INDEX_3_PREFIX = TIME+"index_3";
    public static final String INSTANCE_INDEX_2_PREFIX = TIME+"index_2";
    public static final String INSTANCE_INDEX_1_PREFIX = TIME+"index_1";

    @Value("清除3.1 openApp接口")
    public static final String INSTANCE_OPEN_APP_PREFIX = TIME+"open_app";
    public static final String INSTANCE_APPVESION_PREFIX = TIME+"appver";

    @Value("清除config表的缓存")
    public static final String INSTANCE_CONFIG_PREFIX = TIME+"config";

    @Value("清除v3.2实体列表的数值的缓存")
    public static final String INSTANCE_ENITTY_STATE_PREFIX = TIME+"entity_state";



    public static final List<Map> listMap=new ArrayList<Map>();


    static{
        try {
            /**
             * INSTANCE_INITAPP_7_PREFIX--
             initApp_7--
             清除初始化接口--
             */
            Class clazz = Class.forName("cn.bevol.cache.CACHE_NAME");
            Field[] fields = clazz.getFields();
            for( Field field : fields ){
                if(field.getName().indexOf("INSTANCE")!=-1) {
                    if(field.getAnnotations().length>0) {
                        Map map=new HashMap();
                        Value val=(Value) field.getAnnotations()[0];
                        String key=(String)field.get((clazz).toString());
                        map.put("desc", val.value());
                        map.put("key", key);
                        String cacheType="";
		        		/*if(StringUtils.isNotBlank(key)){
		        			cacheType=key.substring(0, 1);
		        			if("m".equals(cacheType)){
		        				//时效
		        				map.put("type", 1);
		        			}
		        			if("f".equals(cacheType)){
		        				//永久
		        				map.put("type", 2);
		        			}
		        		}*/
                        listMap.add(map);
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String createInstanceForeverKey(String...  org){
        return StringUtils.join(org,"_");
    }

    public static String createInstanceKey(String...  org){
        String strs[]=Arrays.copyOf(org, org.length+1);
        strs[org.length]=VERSION;
        return createInstanceForeverKey(strs);
    }

    public static String createInstanceKey(ArrayList<String> org){
        String strs[]= new String[org.size()+1];
        strs[org.size()]=VERSION;
        return createInstanceForeverKey(strs);
    }

    public static String createInstanceCleanCacheKey(String...  org){
        String strs[]=Arrays.copyOf(org, org.length+1);
        return createInstanceForeverKey(strs);
    }

    /**
     * oss 正式目录转换为临时目录
     * @return
     */
    public static String dirConverTmpl(String dir) {
        String tplt="_bevol_";
        String tmpl="tmpl";
        String dplt="/";
        String path=tmpl+dplt+StringUtils.replace(dir,dplt, tplt);
        return path;
    }

    /**
     * oss 临时目录转换为临时目录
     * @param tmp
     * @return
     */
    public static String tmplConverDir(String tmp) {
        String tplt="_bevol_";
        String tmpl="tmpl";
        String dplt="/";
        String path=StringUtils.replace(tmp,tplt, dplt);
        return StringUtils.replace(path,tmpl+dplt,"");
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

        System.out.println(dirConverTmpl("goods/bdss/vsd"));
        System.out.println(tmplConverDir("tmpl/goods_bevol_bdss_bevol_vsd"));

        System.out.println(dirConverTmpl("find"));
        System.out.println(tmplConverDir("tmpl/find"));

    }
}
