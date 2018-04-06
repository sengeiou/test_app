package cn.bevol.statics.service;

import cn.bevol.statics.config.OSSConfig;
import cn.bevol.util.response.ReturnData;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 16-6-8.
 */

@Service
public class OSSService {

    @Autowired
    @Qualifier("pc_client")
    private static OSSClient pc_client;

    @Autowired
    @Qualifier("m_client")
    private static OSSClient m_client;

    @Autowired
    @Qualifier("source_client")
    private static OSSClient source_client;



    public static OSSClient getPc_client() {
        return pc_client;
    }

    public static OSSClient getM_client() {
        return m_client;
    }

    public static OSSClient getSource_client() {
        return source_client;
    }

    public static String getPcBucket() {
        return OSSConfig.getPcBucket();
    }

    public static String getMBucketName() {
        return OSSConfig.getmBucket();
    }

    public static String getSourceBucket() {
        return OSSConfig.getSourceBucket();
    }

    public static OSSClient getPcClient() {
        return pc_client;
    }

    public static OSSClient getMClient() {
        return m_client;
    }

    public static OSSClient getSourceClient() {
        return source_client;
    }

    /**
     * 可使用uploadHtml2, 直接上传输入流
     * @param key
     * @param path
     * @param client
     * @param BUCKET_NAME
     * @throws OSSException
     * @throws ClientException
     * @throws FileNotFoundException
     */
    @Deprecated
    private static void uploadHtml(String key, String path, OSSClient client, String BUCKET_NAME)
            throws OSSException, ClientException, FileNotFoundException {

        File file = new File(key);
        InputStream input = new FileInputStream(file);
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.length());
        // 可以在metadata中标记文件类型
        objectMeta.setContentType("text/html");
        client.putObject(BUCKET_NAME, path, input, objectMeta);

        deleteFile(file);
    }

    /**
     * 上传html
     * @param html
     * @param path
     * @param client
     * @param BUCKET_NAME
     * @throws IOException
     */
    private static void uploadHtml2(String html, String path, OSSClient client, String BUCKET_NAME) throws IOException {
        uploadInputStream( html,  path, "text/html",  client,  BUCKET_NAME);
    }

    /**
     * 上传json
     * @param json
     * @param path
     * @param client
     * @param BUCKET_NAME
     * @throws IOException
     */
    private static void uploadJson(String json, String path, OSSClient client, String BUCKET_NAME)
            throws IOException {
        uploadInputStream( json,  path, "text/json",  client,  BUCKET_NAME);
    }

    /**
     * 上传xml
     * @param xml
     * @param path
     * @param client
     * @param BUCKET_NAME
     * @throws IOException
     */
    private static void uploadXml(String xml, String path, OSSClient client, String BUCKET_NAME)
            throws IOException {
        uploadInputStream( xml,  path, "text/xml",  client,  BUCKET_NAME);
    }

    /**
     * 流上传
     * @param input
     * @param path
     * @param contentType
     * @param client
     * @param BUCKET_NAME
     * @throws IOException
     */
    private static void uploadInputStream(InputStream input, String path, String contentType, OSSClient client, String BUCKET_NAME)
            throws IOException {
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(input.available());
        // 可以在metadata中标记文件类型
        objectMeta.setContentType(contentType);
        client.putObject(BUCKET_NAME, path, input, objectMeta);
    }

    /**
     * 流上传
     * @param str
     * @param path
     * @param contentType
     * @param client
     * @param BUCKET_NAME
     * @throws IOException
     */
    private static void uploadInputStream(String str, String path, String contentType, OSSClient client, String BUCKET_NAME) throws IOException {
        InputStream input = new ByteArrayInputStream(str.getBytes("UTF-8"));
        uploadInputStream(input , path, contentType, client, BUCKET_NAME);
    }

    /**
     * 流下载
     * @param key
     * @param client
     * @param BUCKET_NAME
     * @return
     */
    private static String downloadInputStream(String key, OSSClient client, String BUCKET_NAME) throws IOException {
        OSSObject ossObject = client.getObject(BUCKET_NAME, key);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        StringBuilder input = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            input.append(line);
            if (line == null) break;
        }
        reader.close();
        return input.toString();
    }

    /**
     * 重设content-type
     * @param contentType
     * @param key
     * @param client
     * @param BUCKET_NAME
     * @throws IOException
     */
    private static void resetContentType(String contentType, String key, OSSClient client, String BUCKET_NAME) throws IOException {
        String input = downloadInputStream(key, client, BUCKET_NAME);
        uploadInputStream(input, key, contentType, client, BUCKET_NAME);
    }

    /**
     * 重设content-type
     * @param contentType
     * @param key
     * @param bucket
     * @return
     */
    public static ReturnData resetContentType(String contentType, String key, String bucket){
        OSSClient client;
        String BUCKET_NAME;
        switch(bucket){
            case "mobile":
                client = getMClient();
                BUCKET_NAME = getMBucketName();
                break;
            case "m":
                client = getMClient();
                BUCKET_NAME = getMBucketName();
                break;
            case "pc":
                client = getPcClient();
                BUCKET_NAME = getPcBucket();
                break;
            case "source":
                client = getSourceClient();
                BUCKET_NAME = getSourceBucket();
                break;
            default:
                return ReturnData.FAILURE;
        }
        try {
            resetContentType(contentType, key, client, BUCKET_NAME);
            return ReturnData.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ReturnData.FAILURE;
    }

    /**
     * 使用uploadHtml2OSS上传输入流
     * 上传到PC
     * @param filepath mid
     * @param path
     * @throws OSSException
     * @throws ClientException
     * @throws FileNotFoundException
     */
    @Deprecated
    private static void uploadHtml2PC(String filepath, String path)
            throws OSSException, ClientException, FileNotFoundException {
        uploadHtml(filepath, path, pc_client, OSSConfig.getPcBucket());
    }

    /**
     * 使用uploadHtml2OSS上传输入流
     * 上传到M
     * @param filepath
     * @param path
     * @throws OSSException
     * @throws ClientException
     * @throws FileNotFoundException
     */
    @Deprecated
    private static void uploadHtml2M(String filepath, String path)
            throws OSSException, ClientException, FileNotFoundException {
        uploadHtml(filepath, path, m_client, OSSConfig.getmBucket());
    }

    public static void uploadJson2Source(String json, String path) throws IOException {
        uploadJson(json, path, source_client, OSSConfig.getSourceBucket());
    }

    public static void uploadXml2PC(String xml, String path) throws IOException {
        uploadXml(xml, path, pc_client, OSSConfig.getPcBucket());
    }

    public static void uploadXml2M(String xml, String path) throws IOException {
        uploadXml(xml, path, m_client, OSSConfig.getmBucket());
    }

    /**
     * 使用uploadHtml2OSS上传输入流
     * @param filepath
     * @param path
     * @param platform
     * @throws FileNotFoundException
     */
    @Deprecated
    public static void upload2OSS(String filepath, String path, String platform) throws FileNotFoundException {
        switch(platform){
            case "pc":
                uploadHtml2PC(filepath, path);
                break;
            case "mobile":
                uploadHtml2M(filepath, path);
                break;
            default:
                break;
        }
    }

    /**
     * 上传html到pc或mobile
     * @param html
     * @param path
     * @param platform
     * @throws IOException
     */
    public static void uploadHtml2OSS(String html, String path, String platform) throws IOException {
        switch(platform){
            case "pc":
                uploadHtml2(html, path, pc_client, OSSConfig.getPcBucket());
                break;
            case "mobile":
                uploadHtml2(html, path, m_client, OSSConfig.getmBucket());
                break;
            default:
                break;
        }
    }

    public static Boolean dose360SiteMapExist(Integer page){
        String key = "sitemap/product"+ page +".xml";
        //pc与m是同步的，所以只验证pc
        return doseObjectExist(pc_client, OSSConfig.getPcBucket(), key);
    }

    private static Boolean doseObjectExist(OSSClient ossClient, String bucketName, String key){
        return ossClient.doesObjectExist(bucketName, key);
    }
    /**
     * 删静态化后的单个文件临时文件
     * @param   file    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file) {
        boolean  flag = false;
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static void deleteMObject(String key){
        deleteObject(key, m_client, OSSConfig.getmBucket());
    }

    /**
     * 删除oss上的文件
     * @param key
     * @param client
     * @param bucketName
     */
    private static void deleteObject(String key, OSSClient client, String bucketName){
        client.deleteObject(bucketName, key);
    }

    /**
     * 获取指定目录下的所有文件地址
     */
    public static List<String> getListByDir(String dir, OSSClient client, String bucketName){
        List<String> result = new ArrayList<String>();
        // 构造ListObjectsRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        listObjectsRequest.setPrefix(dir);
        // 递归列出fun目录下的所有文件
        ObjectListing listing = client.listObjects(listObjectsRequest);
        // 遍历所有Object
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            result.add(objectSummary.getKey());
        }
        return result;
    }

    /**
     * 检查Object是否存在
     * @param client
     * @param bucketName
     * @param key
     * @return
     */
    public static Boolean isExist(OSSClient client, String bucketName, String key){
        Boolean found = client.doesObjectExist(bucketName, key);
        return found;
    }
}
