package cn.bevol.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mysens on 17-5-31.
 */
public class StringUtil {

    /**
     * 首字母大写  （apache StringUtils中的capitalize会将首字母之外的转为小写）
     * @param name
     * @return
     */
    public static String captureName(String name) {
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }

    public static boolean isChineseChar(String str){
        boolean temp = false;
        Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m=p.matcher(str);
        if(m.find()){
            temp =  true;
        }
        return temp;
    }

    //截取字符串长度(中文2个字节，半个中文显示一个)
    public static String subTextString(String str,int len){
        if(str.length()<len/2)return str;
        int count = 0;
        StringBuffer sb = new StringBuffer();
        String[] ss = str.split("");
        for(int i=1;i<ss.length;i++){
            count+=ss[i].getBytes().length>1?2:1;
            sb.append(ss[i]);
            if(count>=len)break;
        }
        //不需要显示...的可以直接return sb.toString();
        return (sb.toString().length()<str.length())?sb.append("...").toString():str;
    }

    /**
     * 去除前后指定字符
     * @param source 目标字符串
     * @param beTrim 要删除的指定字符
     * @return 删除之后的字符串
     * 调用示例：System.out.println(trim(", ashuh  ",","));
     */
    public static String trim(String source, String beTrim) {
        if(source==null){
            return "";
        }
        source = source.trim(); // 循环去掉字符串首的beTrim字符
        if(source.isEmpty()){
            return "";
        }
        String beginChar = source.substring(0, 1);
        if (beginChar.equalsIgnoreCase(beTrim)) {
            source = source.substring(1, source.length());
            beginChar = source.substring(0, 1);
        }
        // 循环去掉字符串尾的beTrim字符
        String endChar = source.substring(source.length() - 1, source.length());
        if (endChar.equalsIgnoreCase(beTrim)) {
            source = source.substring(0, source.length() - 1);
            endChar = source.substring(source.length() - 1, source.length());
        }
        return source;
    }

    public static String  strCleanMark(String str) {
        return str.replaceAll("\u200B","").replaceAll("\\(.*?\\)|（.*?）", "").replaceAll(" ", " ").replaceAll("\\s+"," ").trim();
    }

    public static String convertPunctuation2En(String str){
        String[] cn = {"，", "。", "！", "；", "（", "）"};
        String[] en = {",", ".", "!", ":", "(", ")"};
        for (int i = 0; i <cn.length ; i++) {
            str = str.replaceAll (cn[i], en[i]);
        }
        return str;
    }

    public static String formatStandardName(String str){
        str = convertPunctuation2En(str);
        str = str.replaceAll("\u200B","").replaceAll(" ", " ").replaceAll("\\s+"," ").trim();
        str = str.replaceAll(" ?\\( ?", "(");
        str = str.replaceAll(" ?\\) ?", ")");
        str = str.toUpperCase();
        return str;
    }

    public static String sanitizedName(String str){
        return StringUtil.trim(str.replaceAll(" ", " ").trim(), ".");
    }
}
