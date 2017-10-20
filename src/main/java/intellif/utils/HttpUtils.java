package intellif.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import intellif.entity.ApplicationProperties;

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
    	InputStream inputStream=null;
    	InputStreamReader inputStreamReader=null;
    	BufferedReader bufferedReader=null;
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
            	HttpEntity entity=response.getEntity();
            	inputStream=entity.getContent();
            	inputStreamReader=new InputStreamReader(inputStream,"utf-8");
            	bufferedReader= new BufferedReader(inputStreamReader);
            	StringBuffer sb=new StringBuffer();
            	String newLine=null;
            	while ((newLine =bufferedReader.readLine())!=null) {
					sb.append(newLine);
				}
                entityString =sb.toString();
                //输出
                System.out.println("请求成功!");
                EntityUtils.consume(entity);
            }else {
            	System.out.println("请求失败！"+uri.toString());
            }
		} catch (Exception e) {
			entityString=null;
			System.out.println("发送http请求失败");
			e.printStackTrace();
		}finally {
            try {
            	bufferedReader.close();
            	inputStreamReader.close();
            	inputStream.close();
            	response.close();
				httpClient.close();
			} catch (IOException e) {
				System.out.println("关闭失败");
				e.printStackTrace();
			}
		}
        return entityString;
    } 

}