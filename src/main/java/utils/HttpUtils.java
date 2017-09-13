package utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
//            //设置请求的报文头部的编码
//            hg.setHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));
//            //设置期望服务端返回的编码
//            hg.setHeader(new BasicHeader("Accept", "text/json;charset=utf-8"));
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

}