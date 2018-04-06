package cn.bevol.statics.service;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Locale;
import java.util.Map;

@Service
public class FreemarkerService {
    private static Logger logger = LoggerFactory.getLogger(FreemarkerService.class);
    final private static String TEMPLATE_PATH = "/cn/bevol/statics/template";
    final private static String TEMPLATE_EXT = "ftl";
    @Value("${static.html.root}")
    public static String freemarkerPath;

    /**
     * html直接上传输入流，不需要生成html文件
     * @param ftlName ftl的名称
     * @param htmlName 要生成的html名称
     * @param platform 调用的模板
     * @param dataMap 需要渲染到ftl中的map数据
     * @return
     */
    @Deprecated
    public  boolean createHtmlFile(String ftlName,
                                   String htmlName, String platform, Map dataMap) throws IOException
    {
        boolean result = false;
        // 创建Configuration对象
        Configuration cfg = new Configuration();
        // 创建Template对象
        Template template = null;
        Writer writer = null;
        //构建templates路径
        String templates_path = TEMPLATE_PATH + "/" + platform;
        try {

            cfg.setClassForTemplateLoading(this.getClass(),templates_path);
            cfg.setEncoding(Locale.getDefault(), "utf-8");

            template = cfg.getTemplate(ftlName);
            template.setEncoding("utf-8");
            String path = freemarkerPath;
            File file = new File(path+"/"+platform);
            if  (!file .exists()  && !file .isDirectory()) {
                file.mkdir();
            }



            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path+"/"+platform+"/"+htmlName+".html"), "utf-8"));
//
            // 生成静态页面
            template.process(dataMap, writer);
            result = true;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            result = false;
            logger.error("初始化页面模板出错",e.getMessage());
        } catch (TemplateException e) {
            e.printStackTrace();

            result = false;
            logger.error("初始化页面出错",e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public String createFile(String ftlName, String filePath, Map dataMap){
        // 创建Configuration对象
        Configuration cfg = new Configuration();
        // 创建Template对象
        Template template = null;
        Writer writer = null;
        try {

            cfg.setClassForTemplateLoading(this.getClass(), TEMPLATE_PATH);
            cfg.setEncoding(Locale.getDefault(), "utf-8");

            template = cfg.getTemplate(ftlName + "." + TEMPLATE_EXT);
            template.setEncoding("utf-8");
            File file = new File(filePath.substring(0, filePath.lastIndexOf("/")));
            if  (!file .exists()  && !file .isDirectory()) {
                file.mkdir();
            }

            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath), "utf-8"));
            // 生成静态页面
            template.process(dataMap, writer);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            logger.error("初始化页面模板出错",e.getMessage());
        } catch (TemplateException e) {
            e.printStackTrace();

            logger.error("初始化页面出错",e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return filePath;
    }

    /***
     * 生成PC站
     * @param ftlName
     * @param htmlName
     * @param dataMap
     * @return
     * @throws IOException
     */
    @Deprecated
    public  boolean createHtmlFile2PC(String ftlName,
                                      String htmlName, Map dataMap) throws IOException
    {
        String templates ="pc";
        return createHtmlFile(ftlName,htmlName,templates,dataMap);
    }
    /***
     * 生成M站
     * @param ftlName
     * @param htmlName
     * @param dataMap
     * @return
     * @throws IOException
     */
    @Deprecated
    public  boolean createHtmlFile2M(String ftlName,
                                     String htmlName, Map dataMap) throws IOException
    {
        String templates ="mobile";
        return createHtmlFile(ftlName,htmlName,templates,dataMap);
    }

    @Deprecated
    public boolean createHtmlFiles(String ftlName, String htmlName, Map dataMap, String platform) throws IOException {
        boolean result = false;
        if(platform == "pc"){
            result = createHtmlFile2PC(ftlName, htmlName, dataMap);
        }
        if(platform == "mobile"){
            result = createHtmlFile2M(ftlName, htmlName, dataMap);
        }
        return result;
    }

    public String get360SeoXml(String ftlName, Map dataMap) throws UnsupportedEncodingException {
        String templateDir = TEMPLATE_PATH + "/seo";
        String ftlExt = "ftl";
        return getStringFromFtl(templateDir, ftlName, ftlExt ,dataMap);
    }


    public static String getHtml(String ftlName, String platform, Map dataMap, Boolean isBack) throws UnsupportedEncodingException {
        if(isBack){
            dataMap.put("back", 1);
        }
        //构建templates路径
        String templateDir = TEMPLATE_PATH + "/" + platform;
        String ftlExt = "html";
        return getStringFromFtl(templateDir, ftlName, ftlExt, dataMap);
    }

    public static String getHtml(String ftlName, String platform, Map dataMap) throws UnsupportedEncodingException {
        return getHtml(ftlName, platform, dataMap, false);
    }

    /**
     * 将解析之后的文件内容返回字符串
     * @param templateDir 模板目录
     * @param ftlName  模板名
     * @param ftlExt 模板后缀
     * @param dataMap  数据map
     * @return  字符串
     * @throws UnsupportedEncodingException
     */
    private static String getStringFromFtl(String templateDir, String ftlName, String ftlExt, Map dataMap) throws UnsupportedEncodingException {
        StringWriter out = new StringWriter();
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setClassForTemplateLoading(FreemarkerService.class, templateDir);
            cfg.setEncoding(Locale.getDefault(), "utf-8");
            //通过一个文件输出流，就可以写到相应的文件中
            Template temp = cfg.getTemplate(ftlName+"."+ftlExt);
            temp.process(dataMap, out);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (TemplateException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }

    /**
     * 将解析之后的文件内容返回字符串
     * @param ftlName  模板名
     * @param dataMap  数据map
     * @return  字符串
     * @throws UnsupportedEncodingException
     */
    public String getStringFromFtl(String ftlName, Map dataMap) throws UnsupportedEncodingException {
        return getStringFromFtl(TEMPLATE_PATH, ftlName, TEMPLATE_EXT, dataMap);
    }
}
