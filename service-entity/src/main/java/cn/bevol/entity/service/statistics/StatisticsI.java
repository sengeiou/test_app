package cn.bevol.entity.service.statistics;

/**
 * Created by owen on 16-7-13.
 */
public interface StatisticsI {
    /**
     * 请求必带
     */
    public static final String KEY_USER_ID = "uid";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_PLATFORM = "o";
    public static final String KEY_MODEL = "model";
    public static final String KEY_VERSION = "v";
    public static final String KEY_SYS_V = "sys_v";
    public static final String KEY_CHANEL = "channel";
    public static final String KEY_IP = "ip";



    /**
     * active_field
     */
    public static final String COLLECTION_DAILY_REGISTER_PRE = "daily_register_";
    public static final String COLLECTION_DAILY_INIT_PRE = "daily_init_";
    public static final String COLLECTION_DAILY_LOGIN_PRE = "daily_login_";
    public static final String COLLECTION_DAILY_ACTIVE_PRE = "daily_active_";
    
    public static final String COLLECTION_NOSEARCH_PRE = "nosearch_";

    
    public static final String COLLECTION_SKIN_TEST = "log_skin_test";

    /**
     * 邮箱注册
     */
    public static final String COLLECTION_USR_REG = "user_reg";


    public static final String FIELD_USER_ID = "uid";
    public static final String FIELD_UUID = "uuid";
    public static final String FIELD_PLATFORM = "platform";
    public static final String FIELD_MODEL = "model";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_TOTAL_NUM = "totalNum";

    public static final String FIELD_TNAME = "tname";
    public static final String FIELD_KEYWORDS = "keywords";

}
