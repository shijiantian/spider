package utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

/**
 * 用于模拟HTTP请求中GET/POST方式 
 * @author shijt
 *
 */
public class HttpUtils {  
	
	/** 
     * 发get请求 
     *  
     * @param url 
     *            目的地址 
     * @param parameters 
     *            请求参数，Json类型�? 
	 * @param parameters2 
     * @return 远程响应结果 
	 * @throws IOException 
	 * @throws URISyntaxException 
     */  
    public static String sendGet(String url, Map<String, String> parameters) { 
    	String entityString= null;
    	try {
            CloseableHttpClient httpClient=HttpClients.createDefault();
            URIBuilder uri=new URIBuilder(url);
            //设置查询参数
            Set<String> keys=parameters.keySet();
            keys.stream().forEach(key->{
            	uri.addParameter(key, parameters.get(key));
            });
          
            //创建httpGet对象
            HttpGet hg = new HttpGet(uri.build());
            //设置请求的报文头部的编码
            hg.setHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));
            //设置期望服务端返回的编码
            hg.setHeader(new BasicHeader("Accept", "text/json;charset=utf-8"));
            //请求服务
            CloseableHttpResponse response = httpClient.execute(hg);

            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode == 200) {
                //获取返回实例entity
                entityString =EntityUtils.toString(response.getEntity());
                //输出
                System.out.println("请求成功!");
            }else {
                //输出
                System.out.println("请求失败!");
            }

            //关闭response和client
            response.close();
            httpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return entityString;
    }  
    
    /** 
     * 发POST请求 
     *  
     * @param url 
     *            目的地址 
     * @param parameters 
     *            请求参数，Json类型�? 
     * @return 远程响应结果 
     */  
    public static JSONObject sendPost(String url, JSONObject parameters) {  
        JSONObject jsonResult = null;
        HttpClient httpClient=new HttpClient();
        PostMethod method=new PostMethod(url);
        //使用系统提供的默认的恢复策略
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
        try {
        	//执行getMethod
        	int statusCode = httpClient.executeMethod(method);
        	if (statusCode != HttpStatus.SC_OK) {
        		System.err.println("Method failed: "+ method.getStatusLine());
        	}
        	//读取内容 
        	byte[] responseBody = method.getResponseBody();
        	//处理内容
        	System.out.println(new String(responseBody));
		} catch (HttpException e) {
			//发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			//发生网络异常
			e.printStackTrace();
		} finally {
			//释放连接
			method.releaseConnection();
		}
        return jsonResult;  
    }  

}