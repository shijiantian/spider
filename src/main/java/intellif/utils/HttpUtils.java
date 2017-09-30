package intellif.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import intellif.entity.ApplicationProperties;

/**
 * 用于模拟HTTP请求中GET/POST方式 
 * @author shijt
 *
 */
public class HttpUtils { 
	
	private static Logger LOG = LogManager.getLogger(HttpUtils.class);
	
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
            	inputStreamReader=new InputStreamReader(response.getEntity().getContent(),"utf-8");
            	bufferedReader= new BufferedReader(inputStreamReader);
            	StringBuffer sb=new StringBuffer();
            	String newLine=null;
            	while ((newLine =bufferedReader.readLine())!=null) {
					sb.append(newLine);
				}
                entityString =sb.toString();
                //输出
                System.out.println("请求成功!"+uri.toString());
            }else {
                LOG.error("请求失败！"+uri.toString());
            }
		} catch (Exception e) {
			entityString=null;
			LOG.error("发送http请求失败",e);
		}finally {
            try {
            	bufferedReader.close();
            	inputStreamReader.close();
            	response.close();
				httpClient.close();
			} catch (IOException e) {
				LOG.error("关闭失败",e);
			}
		}
        return entityString;
    } 

}