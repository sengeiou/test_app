package cn.bevol.util;


import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件app.properties可能有线上变动不使用const
 *
 * @author owen
 */
public class PropertyUtils {

    public static String getStringValue(String temp) {
        Properties props = new Properties();
        InputStream in;
        try {
            in = PropertyUtils.class.getResourceAsStream("/app.properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (props.isEmpty()) {
//            Logger.error("文件读取出现错误i");
        }
        try {
            return props.get(temp).toString();
        } catch (Exception ex) {
//            Logger.error("不存在的配置"+temp);
            return null;
        }

    }

    public static int getIntValue(String key) {
        Properties props = new Properties();
        InputStream in;
        try {
            in = PropertyUtils.class.getResourceAsStream("/app.properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        if (props.isEmpty()) {
            return 0;
        }
        String value = props.get(key).toString();
        int num = 0;
        try {
            num = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return num;

    }

    public static String getMetaStringValue(String temp) {
        Properties props = new Properties();
        InputStream in;
        try {
            in = PropertyUtils.class.getResourceAsStream("/meta.properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (props.isEmpty()) {

        }
        String save = "";
        try {
            save = new String(props.getProperty(temp).getBytes("ISO-8859-1"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return save;

    }
}
