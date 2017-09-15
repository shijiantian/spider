package utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import entity.ApplicationProperties;
import entity.RandomUserAgent;

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
	 * @param queryWord 
	 * @param parameters2 
     * @return 远程响应结果 
	 * @throws IOException 
	 * @throws URISyntaxException 
     */  
    public static String sendGet(String url, Map<String, String> parameters, String queryWord,int needReferer) { 
    	String entityString= null;
    	CloseableHttpClient httpClient=null;
    	CloseableHttpResponse response=null;
    	try {
            httpClient=HttpClients.createDefault();
            URIBuilder uri=new URIBuilder(url);
            //设置查询参数
            Set<String> keys=parameters.keySet();
            keys.stream().forEach(key->{
            	uri.addParameter(key, parameters.get(key));
            });
            //创建httpGet对象
            HttpGet hg = new HttpGet(uri.build());
//            RequestConfig config=RequestConfig.custom()
//                    .setConnectTimeout(50000)
//                    .setSocketTimeout(50000)
//                    .setConnectionRequestTimeout(50000)
//                    .build();
//            hg.setConfig(config);
            hg.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            if(needReferer==1)
            	hg.setHeader("Referer", ApplicationProperties.getBaiduReferer()+URLEncoder.encode(queryWord, "utf-8"));
            //请求服务
            response = httpClient.execute(hg);
            if(response==null)
            	return entityString;
            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode == 200) {
                //获取返回实例entity
                entityString =EntityUtils.toString(response.getEntity());
                //输出
                System.out.println("请求成功!"+uri.toString());
            }else {
                //输出
                System.out.println("请求失败!");
            }
            response.close();
		} catch (Exception e) {
			entityString=null;
			e.printStackTrace();
		}finally {
            try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return entityString;
    } 

}