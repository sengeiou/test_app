package cn.bevol.staticc.Handler;



	import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

	public class FreeMarkerHandler extends HttpServlet {
		
		private Configuration configuration = null; //解释Configuration
		
		//构造函数
		public FreeMarkerHandler(){
			//创建Configuration实例
			configuration = new Configuration();
			//输出的数据默认的编码类型
			configuration.setDefaultEncoding("utf-8");
		}
		
		@SuppressWarnings("unchecked")
		public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
		{
			//---------------1.准备数据-----------------
			//要填入的数据文件
			Map dataMap = new HashMap();//解释数据的容器
			//添加数据
			dataMap.put("title", "FreeMarker示例");
			
			//---------------------------------------
			
			//---------------2.设置模板装载的方法(有多种方法)-
			//介绍两种方法：
//			configuration.setServletContextForTemplateLoading(getServletContext(),"templates");
			configuration.setClassForTemplateLoading(this.getClass(), "/cn/bevol/templates");
			//---------------------------------------
			
			//----------------3.获得模板----------------
			//获得需要装载的模版
			Template template = null;
			try {
				template = configuration.getTemplate("Test.ftl");
				template.setEncoding("utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//---------------------------------------
			
			//--------------4.开始准备生成输出--------------
	        //使用模版文件的charset作为本页面的charset
	        //使用text/html MIME-type
	        response.setContentType("text/html; charset=" + template.getEncoding());
	        PrintWriter out = response.getWriter();
	        
	        //合并数据模型和模版，并将结果输出到out中
	        try
	        {
	        	template.process(dataMap,out);// 用模板来开发servlet可以只在代码里面加入动态的数据
	        }
	        catch(TemplateException e)
	        {
	         throw new ServletException("处理Template模版中出现错误", e);
	        }
	      //------------------------------------------
	        
		}
		public static void main(String[] args) throws ServletException, IOException {
			//创建Configuration实例
			Configuration configuration = new Configuration();
			//输出的数据默认的编码类型
			configuration.setDefaultEncoding("utf-8");
			
			Map dataMap = new HashMap();//解释数据的容器
			dataMap.put("title", "FreeMarker示例");
			//configuration.setClassForTemplateLoading(this.getClass(),"/com/bevol/staticc/templates");
			configuration.setDirectoryForTemplateLoading(new File("C:\\templates"));
			
			//----------------3.获得模板----------------
			//获得需要装载的模版
			Template template = null;
			try {
				template = configuration.getTemplate("Test.ftl");
				 configuration.setDefaultEncoding("UTF-8");   //这个一定要设置，不然在生成的页面中 会乱码  
				
		           
				 Writer writer  = new OutputStreamWriter(new FileOutputStream("success.html"),"UTF-8");  
		            template.process(dataMap, writer);  
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
