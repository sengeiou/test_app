package cn.bevol.constant.api;


import cn.bevol.constant.MetaKv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityMeta {
    public static final String ENTITY_TABLE_PREFIX = "entity_";
    public static final String ENTITY_INC_SUFFIX = "_inc";
    public static final Map<String, Map<String, String>> METADATA_MAP = new HashMap<String, Map<String, String>>();
    public static final Map<String, List<MetaKv>> METADATA_KVS = new HashMap<String, List<MetaKv>>();

    public static final String UNIQUE_ID = "unique_id_inc";


    /**
     * 全局配置 信息
     */
    public static final String GLOBAL_TABLE = "global_config";

    private static enum Type {
        ENTITY_KIND;
    }

    static {
        METADATA_MAP.put(Type.ENTITY_KIND.name(), EntityKinds.MAP);
        METADATA_KVS.put(Type.ENTITY_KIND.name(), EntityKinds.KVS);
    }

    /**
     * 流程处理状态
     */
    public static enum EntityKinds {
        GOODS("goods", "产品"),
        COMPOSITION("composition", "成分"),
        FIND("find", "发现"),
        LISTS("lists", "清单");

        public static Map<String, String> MAP;
        public static List<MetaKv> KVS;

        static {
            MAP = new HashMap<String, String>(EntityKinds.values().length);
            KVS = new ArrayList<MetaKv>(EntityKinds.values().length);
            for (EntityKinds status : EntityKinds.values()) {
                MAP.put(String.valueOf(status.getCode()), status.getDesc());
                KVS.add(new MetaKv(status.getCode(), status.getDesc()));
            }
        }

        public static String findDescByCode(String code) {
            return findDescByCode(String.valueOf(code));
        }


        private final String code;
        private final String desc;

        EntityKinds(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
