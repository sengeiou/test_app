package cn.bevol.app.entity.constant;


import cn.bevol.model.entity.*;
import cn.bevol.model.entityAction.ApplyGoodsUser;
import cn.bevol.util.DateUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.*;

public class CommenMeta {

    /**
     * 喜欢表
     *
     */
    public static final String COLLTION_LIKE="entity_like2_";


    public static final Map<String, Map<String, String>> METADATA_MAP = new HashMap<String, Map<String, String>>();
    public static final Map<String, List<MetaKv>> METADATA_KVS = new HashMap<String, List<MetaKv>>();

    /**
     * 实体tname和class的关系
     */
    public static final Map<String,Class> ENTITY_ACTION = new HashMap<String, Class>();

    /**
     * 文件上传模块对应的 buter映射
     */
    public static final Map<String, String> UPFILE_PATH = new HashMap<String, String>();



    /**
     * 消息与模块的关系映射
     * key:feedback.type 类型
     * val:message.type=1时 message.description 类型
     */
    public static final Map<String, MessageStatus> ACTIONNAME_MESSAGE = new HashMap<String,MessageStatus>();

    private static enum Type {
        WORK_STATUS, WORK_RESULT;
    }

    /**
     * 肤质
     */
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
    }

    static {
        METADATA_MAP.put(Type.WORK_STATUS.name(), DeleteStatus.MAP);
        METADATA_MAP.put(Type.WORK_RESULT.name(), PublishStatus.MAP);


        METADATA_KVS.put(Type.WORK_STATUS.name(), DeleteStatus.KVS);
        METADATA_KVS.put(Type.WORK_RESULT.name(), PublishStatus.KVS);


        //上传文件bucket对应关系
        UPFILE_PATH.put("feedback", "bevol-static");
        UPFILE_PATH.put("Goods/userupload", "bevol-static");



        //评论回复部分
        //产品评论回复
        ACTIONNAME_MESSAGE.put("msg-reply-comment_goods", MessageStatus.COMMENT_GOODS_REPLY);
        //成分评论回复
        ACTIONNAME_MESSAGE.put("msg-reply-comment_composition", MessageStatus.COMMENT_COMMPOSITION_REPLY);
        //发现评论回复
        ACTIONNAME_MESSAGE.put("msg-reply-comment_find", MessageStatus.COMMENT_FIND_REPLY);
        //清单评论回复
        ACTIONNAME_MESSAGE.put("msg-reply-comment_lists", MessageStatus.COMMENT_LISTS_REPLY);
        //心得回复
        ACTIONNAME_MESSAGE.put("msg-reply-comment_user_part_lists", MessageStatus.COMMENT_USER_PART_LISTS_REPLY);
        //v3.2 福利社回复
        ACTIONNAME_MESSAGE.put("msg-reply-comment_apply_goods", MessageStatus.COMMENT_APPLY_GOODS_REPLY);
        //v3.2 发心得的一级评论的消息
        ACTIONNAME_MESSAGE.put("msg-reply-main-comment_user_part_lists", MessageStatus.MAIN_COMMENT_USER_PART_LISTS_REPLY);

        //产品对比讨论回复消息
        ACTIONNAME_MESSAGE.put("msg-reply-discuss_compare_goods", MessageStatus.DISCUSS_COMPARE_GOODS_REPLY);


        //点赞消息
        ACTIONNAME_MESSAGE.put("msg-comment_like_goods", MessageStatus.COMMENT_LIKE_GOODS);
        //成分评论举报
        ACTIONNAME_MESSAGE.put("msg-comment_like_composition", MessageStatus.COMMENT_LIKE_COMMPOSITION);
        //发现评论举报
        ACTIONNAME_MESSAGE.put("msg-comment_like_find", MessageStatus.COMMENT_LIKE_FIND);
        //清单评论举报
        ACTIONNAME_MESSAGE.put("msg-comment_like_lists", MessageStatus.COMMENT_LIKE_LISTS);
        //心得点赞
        ACTIONNAME_MESSAGE.put("msg-comment_like_user_part_lists", MessageStatus.COMMENT_LIKE_USER_PART_LISTS);
        //福利社评论点赞
        ACTIONNAME_MESSAGE.put("msg-comment_like_apply_goods2", MessageStatus.COMMENT_LIKE_APPLY_GOODS);

        //产品对比讨论点赞
        ACTIONNAME_MESSAGE.put("msg-discuss_like_compare_goods", MessageStatus.DISCUSS_LIKE_COMPARE_GOODS);


        ACTIONNAME_MESSAGE.put("msg-sys-new_active", MessageStatus.MSG_NEW_ACTVIE);
        ACTIONNAME_MESSAGE.put("msg-sys-common", MessageStatus.MSG_SYS);


        //修修酱
        ACTIONNAME_MESSAGE.put("msg-xxj-mange", MessageStatus.MSG_MANAGE);
        //反馈部分  start-------------
        //产品纠错---
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-1-goods", MessageStatus.GOODS_CRTN);
        //肤质测试意见反馈
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-2-skin_test", MessageStatus.SKIN_TEST_SGTN);
        //意见反馈
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-2-suggestion", MessageStatus.SGTN);
        //发现反馈
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-2-find", MessageStatus.FIND_SGTN);

        //举报
        //产品评论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-comment_goods", MessageStatus.COMMENT_GOODS_TIPOFF);
        //成分评论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-comment_composition", MessageStatus.COMMENT_COMMPOSITION_TIPOFF);
        //发现评论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-comment_find", MessageStatus.COMMENT_FIND_TIPOFF);
        //清单评论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-comment_lists", MessageStatus.COMMENT_LISTS_TIPOFF);
        //心得评论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-comment_user_part_lists", MessageStatus.COMMENT_USER_PART_LISTS_TIPOFF);
        //文章举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-find", MessageStatus.FIND_TIPOFF);
        //心得举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-user_part_lists", MessageStatus.USER_PART_LISTS_TIPOFF);
        //福利社评论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-comment_apply_goods2", MessageStatus.COMMENT_APPLY_GOODS_TIPOFF);
        //产品对比讨论举报
        ACTIONNAME_MESSAGE.put("msg-xxj-feedback-3-discuss_compare_goods", MessageStatus.COMMENT_APPLY_DISCUSS_COMPARE_GOODS);
        //反馈部分  end-------------



        //评论中奖发送消息
        ACTIONNAME_MESSAGE.put("msg-xxj-apply_goods_user", MessageStatus.APPLY_GOODS_USER);


        ENTITY_ACTION.put("apply_goods_user", ApplyGoodsUser.class);
        ENTITY_ACTION.put("entity_apply_goods", EntityApplyGoods.class);
        ENTITY_ACTION.put("entity_apply_goods2", EntityApplyGoods2.class);
        ENTITY_ACTION.put("entity_composition", EntityComposition.class);
        ENTITY_ACTION.put("entity_find", EntityFind.class);
        ENTITY_ACTION.put("entity_goods", EntityGoods.class);
        ENTITY_ACTION.put("entity_lists", EntityLists.class);
        ENTITY_ACTION.put("entity_user_part_lists", EntityUserPart.class);
    }


    /**
     * 获取tname的 class
     * @param tname
     * @return
     */
    public static Class getEntityClass(String tname) {
        return ENTITY_ACTION.get(tname);
    }

    public static enum MessageStatus {
        //type=4表示点赞
        COMMENT_LIKE_GOODS(4,1,"comment_like_goods","产品评论点赞"),
        COMMENT_LIKE_COMMPOSITION(4,2,"comment_like_composition","成分评论点赞"),
        COMMENT_LIKE_FIND(4,3,"comment_like_find","发现评论点赞"),
        COMMENT_LIKE_LISTS(4,4,"comment_like_lists","清单评点赞"),
        COMMENT_LIKE_USER_PART_LISTS(4,5,"comment_like_user_part_lists","心得点赞"),
        DISCUSS_LIKE_COMPARE_GOODS(4,6,"discuss_like_compare_goods","产品对比话题点赞"),
        COMMENT_LIKE_APPLY_GOODS(4,7,"comment_like_apply_goods","福利社评论点赞"),


        //type=3修修酱级别
        MSG_MANAGE(3,7,"mange","管理员修修酱消息"),
        GOODS_CRTN(3,4,"goods", "产品纠错"),
        SKIN_TEST_SGTN(3,12,"skin_test","肤质测试反馈"),
        FIND_SGTN(3,8,"find","发现反馈"),
        SGTN(3,5,"suggestion","意见反馈"),
        APPLY_GOODS_USER(3,9,"apply_goods_user","用户中奖"),


        COMMENT_GOODS_TIPOFF(3,6,"comment_goods","产品评论的举报"),
        COMMENT_COMMPOSITION_TIPOFF(3,9,"comment_composition","成分评论的举报"),
        COMMENT_FIND_TIPOFF(3,10,"comment_find","发现评论的举报"),
        COMMENT_LISTS_TIPOFF(3,11,"comment_lists","清单评的论举报"),
        COMMENT_USER_PART_LISTS_TIPOFF(3,12,"comment_user_part_lists","心得的评论"),
        FIND_TIPOFF(3,13,"find","文章举报"),
        COMMENT_APPLY_GOODS_TIPOFF(3,14,"comment_apply_goods","福利社评论的举报"),
        COMMENT_APPLY_DISCUSS_COMPARE_GOODS(3,15,"discuss_compare_goods","产品对比讨论的举报"),
        USER_PART_LISTS_TIPOFF(3,16,"user_part_lists","心得举报"),

        //评论消息 type=2
        COMMENT_GOODS_REPLY(2,1,"comment_goods","产品的评论"),
        COMMENT_COMMPOSITION_REPLY(2,2,"composition_goods","成分的评论"),
        COMMENT_FIND_REPLY(2,3,"find_goods","发现的评论"),
        COMMENT_LISTS_REPLY(2,4,"lists_goods","清单的评论"),
        COMMENT_USER_PART_LISTS_REPLY(2,5,"user_part_lists","心得的评论"),
        COMMENT_APPLY_GOODS_REPLY(2,6,"apply_goods","福利社的评论"),
        MAIN_COMMENT_USER_PART_LISTS_REPLY(2,7,"main_user_part_lists","心得"),

        DISCUSS_COMPARE_GOODS_REPLY(2,8,"compare_goods","产品对比的讨论"),

        // 系统级别type=1 系统级别 主要是 拉消息
        MSG_NEW_ACTVIE(1,8,"new_active","最新活动"),
        MSG_SYS(1,2,"sys","系统通知");


        private final Integer type;
        private final Integer description;
        private final String actionName;
        private final String text;//说明

        MessageStatus(int type, int description,String actionName,String text) {
            this.type=type;
            this.description=description;
            this.text=text;
            this.actionName=actionName;
        }

        public Integer getType() {
            return type;
        }

        public Integer getDescription() {
            return description;
        }

        public String getActionName() {
            return actionName;
        }

        public String getText() {
            return text;
        }

        /**
         * 获取状态
         * @param key
         * @return
         */
        public static MessageStatus getStatusByKey(String key) {
            return ACTIONNAME_MESSAGE.get(key);
        }
        /**
         * 获取管理员回复内容
         * @param key
         * @return
         */
		/*public String managerReply(long time,String nickname,String replyContent) {
			String crdate=DateUtils.timeStampParseDateStr(Integer.valueOf(time+""));
			String content="你在"+crdate+"发布在"+getText()+"，收到来自管理员"+nickname+"的回复‘’"+replyContent+"’‘ ";
			return content;
		}*/
        /**
         * 获取管理员回复内容
         * @param key
         * @return
         */
        public String managerReply(long time,String nickname,String title,String replyContent) {
            String crdate=DateUtils.timeStampParseDateStr(Integer.valueOf(time+""));
            if(title==null) title="";
            String content="你在"+crdate+"发布在"+getText()+" "+title+"，收到来自管理员"+nickname+"的回复:"+replyContent+" ";
            return content;
        }

    }

    /**
     * 流程处理状态
     */
    public static enum DeleteStatus {
        USED(0, "正在使用"), DELETED(1, "已经删除");

        public static Map<String, String> MAP;
        public static List<MetaKv> KVS;

        static {
            MAP = new HashMap<String, String>(DeleteStatus.values().length);
            KVS = new ArrayList<MetaKv>(DeleteStatus.values().length);
            for (DeleteStatus status : DeleteStatus.values()) {
                MAP.put(String.valueOf(status.getCode()), status.getDesc());
                KVS.add(new MetaKv(status.getCode(), status.getDesc()));
            }
        }

        public static String findDescByCode(int code) {
            return findDescByCode(String.valueOf(code));
        }

        public static String findDescByCode(String code) {
            String desc = MAP.get(code);
            if (null == desc) {
                desc = "未知";
            }
            return desc;
        }

        private final int code;
        private final String desc;

        DeleteStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 工作流程 处理结果
     */
    public static enum PublishStatus {
        DEFAULT(0, "默认未发布"), PUBLISHED(1, "已发布");


        public static Map<String, String> MAP;
        public static List<MetaKv> KVS;

        static {
            MAP = new HashMap<String, String>(PublishStatus.values().length);
            KVS = new ArrayList<MetaKv>(PublishStatus.values().length);
            for (PublishStatus status : PublishStatus.values()) {
                MAP.put(String.valueOf(status.getCode()), status.getDesc());
                KVS.add(new MetaKv(status.getCode(), status.getDesc()));
            }
        }

        public static String findDescByCode(int code) {
            return findDescByCode(String.valueOf(code));
        }

        public static String findDescByCode(String code) {
            String desc = MAP.get(code);
            if (null == desc) {
                desc = "未知";
            }
            return desc;
        }

        private final int code;
        private final String desc;

        PublishStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 工作流程 设置权重优先级
     */
    public static enum HiddenStatus {
        DEFAULT(0, "默认"), Hidden(1, "已经隐藏"),;


        public static Map<String, String> MAP;
        public static List<MetaKv> KVS;

        static {
            MAP = new HashMap<String, String>(PublishStatus.values().length);
            KVS = new ArrayList<MetaKv>(PublishStatus.values().length);
            for (PublishStatus status : PublishStatus.values()) {
                MAP.put(String.valueOf(status.getCode()), status.getDesc());
                KVS.add(new MetaKv(status.getCode(), status.getDesc()));
            }
        }

        public static String findDescByCode(int code) {
            return findDescByCode(String.valueOf(code));
        }

        public static String findDescByCode(String code) {
            String desc = MAP.get(code);
            if (null == desc) {
                desc = "未知";
            }
            return desc;
        }

        private final int code;
        private final String desc;

        HiddenStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
