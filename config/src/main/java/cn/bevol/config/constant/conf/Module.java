package cn.bevol.config.constant.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 16-9-6.
 */
public class Module {
    public static List<String> modules = new ArrayList<String>();

    static {
        modules.add("manage");
        modules.add("spider");
        modules.add("app");
        modules.add("common");
        modules.add("api");
        modules.add("cron");
    }
}
