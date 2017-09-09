package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import entity.ApplicationProperties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommonUtils {
	public static List<String> getStartListJson(String entityString){
		List<String> result=new ArrayList<>();
		try {
			JSONObject entity=JSONObject.fromObject(entityString);
			JSONArray data=entity.getJSONArray("data");
			if(data==null||data.isEmpty())
				return result;
			JSONObject dataObj=data.getJSONObject(0);
			if(dataObj.isEmpty()||dataObj.isNullObject())
				return result;
			JSONArray resultArray=dataObj.getJSONArray("result");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator=resultArray.iterator();
			while (iterator.hasNext()) {
				JSONObject object=iterator.next();
				result.add(object.getString("ename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}

	/**
	 * 从返回结果中解析图片路径
	 * @param imageMap
	 * @param entityString
	 */
	public static boolean parseImageUrl(String entityString) {
		JSONObject entity=JSONObject.fromObject(entityString);
		String queryExt=entity.getString("queryExt");
		JSONArray data=entity.getJSONArray("data");
		if(data==null||data.isEmpty())
			return false;
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iterator=data.iterator();
		while(iterator.hasNext()){
			JSONObject jsonObject=iterator.next();
			if(jsonObject==null||jsonObject.isEmpty()||jsonObject.isNullObject())
				continue;
			String middleURL=jsonObject.getString("middleURL");
			String fileUrl=ApplicationProperties.getDownloadFilePath()+File.separator+(int)(10000000*Math.random())+middleURL.substring(middleURL.lastIndexOf("."));
			downloadFile(middleURL,fileUrl,queryExt);
		}
		return true;
	}

	private static void downloadFile(String urlStr,String fileName,String queryExt){
		try {
            CloseableHttpClient httpClient=HttpClients.createDefault();
            URIBuilder uri=new URIBuilder(urlStr);

            //创建httpGet对象
            HttpGet hg = new HttpGet(uri.build());
            hg.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            hg.setHeader("Accept-Encoding","gzip, deflate, br");
            hg.setHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
            hg.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            hg.setHeader("Referer", "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=index&fr=&hs=0&xthttps=111111&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word="+URLEncoder.encode(queryExt, "utf-8"));
            //请求服务
            CloseableHttpResponse response = httpClient.execute(hg);

            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode == 200) {
                //获取返回实例entity
                HttpEntity entity=response.getEntity();
                //输出
                OutputStream outstream=new FileOutputStream(fileName);
                entity.writeTo(outstream);
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
		
	}
}
