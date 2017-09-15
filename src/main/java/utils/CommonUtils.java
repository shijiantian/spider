package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import entity.ApplicationProperties;
import entity.QueryParams;
import entity.RandomUserAgent;
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
	public static boolean parseBaiduImageUrl(String entityString) {
		JSONObject entity=JSONObject.fromObject(entityString);
		String queryExt=entity.getString("queryExt");
		if(StringUtils.isBlank(queryExt))
			return false;
		
		queryExt=new String(queryExt.getBytes(Charset.forName("ISO-8859-1")));
		if(queryExt.contains("site"))
			queryExt=queryExt.substring(0, queryExt.indexOf(" "));
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
			if(StringUtils.isBlank(middleURL))
				continue;
			String fromURLHost=jsonObject.getString("fromURLHost");
			Integer isSiteDownloaded=ApplicationProperties.getDownloadedSites().get(fromURLHost);
			String fileUrl="";
			//图片链接是否已下载
			Integer integer=null;
			synchronized (CommonUtils.class) {
				ConcurrentMap<String, Integer> map=ApplicationProperties.getDownloadedMap();
				integer=map.get(middleURL);
			}
			if(integer==null||integer!=1){
				fileUrl=ApplicationProperties.getDownloadFilePath()+File.separator+queryExt;
				File file=new File(fileUrl);
				if(!file.exists())file.mkdirs();
				synchronized (CommonUtils.class) {
					ApplicationProperties.getDownloadedMap().put(middleURL, 0);
					ApplicationProperties.setFileNo(ApplicationProperties.getFileNo()+1);
					fileUrl=fileUrl+File.separator+ApplicationProperties.getFileNo()+middleURL.substring(middleURL.lastIndexOf("."));
				}
				downloadFile(middleURL,fileUrl,queryExt);
			}
			//website是否已访问
			if(isSiteDownloaded==null||isSiteDownloaded!=1){
				ApplicationProperties.getDownloadedSites().put(fromURLHost, 1);
				downloadSite(fromURLHost,queryExt);
			}
		}
		
		return true;
	}

	private static boolean downloadSite(String fromURLHost, String name) {
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(0);
		params.setName(name+" site:"+fromURLHost);
		params.setRn(30);
		do{
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
			String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters,name,1);
			if(entityString!=null&&StringUtils.isNotBlank(entityString)){
				CommonUtils.parseBaiduImageUrl(entityString);
			}
			params.setPn(params.getPn()+params.getRn());
		}while(params.getPn()<2000);
		return true;
	}

	private static void downloadFile(String urlStr,String fileName,String queryExt){
		CloseableHttpClient httpClient=null;
		OutputStream outstream=null;
		CloseableHttpResponse response=null;
		try {
            httpClient=HttpClients.createDefault();
            URIBuilder uri=new URIBuilder(urlStr);
            
            //创建httpGet对象
            HttpGet hg = new HttpGet(uri.build());
//            RequestConfig config=RequestConfig.custom()
//                    .setConnectTimeout(50000)
//                    .setSocketTimeout(50000)
//                    .setConnectionRequestTimeout(50000)
//                    .build();
//            hg.setConfig(config);
            hg.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            hg.setHeader("Accept-Encoding","gzip, deflate, br");
            hg.setHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
            hg.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            hg.setHeader("Referer", ApplicationProperties.getBaiduReferer()+URLEncoder.encode(queryExt, "utf-8"));
            //请求服务
            response = httpClient.execute(hg);
            if(response!=null){
            	//获取响应码
                int statusCode = response.getStatusLine().getStatusCode();

                if(statusCode == 200) {
                	ApplicationProperties.getDownloadedMap().put(urlStr, 1);
                	write2File(queryExt,urlStr,1);
                    //获取返回实例entity
                    HttpEntity entity=response.getEntity();
                    //输出
                    outstream=new FileOutputStream(fileName);
                    entity.writeTo(outstream);
                }else {
                    //输出
                	ApplicationProperties.getDownloadedMap().put(urlStr, 0);
                    System.out.println("请求失败!");
                }

              //关闭response
               response.close();
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(outstream!=null)
					outstream.close();
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private synchronized static void write2File(String queryExt, String urlStr, int value) {
		File logParentPath=new File(ApplicationProperties.getLogFileSavePath());
		if(!logParentPath.exists())
			logParentPath.mkdirs();
		String logPath=ApplicationProperties.getLogFileSavePath()+File.separator+queryExt;
		File logFile=new File(logPath);
		FileOutputStream outputStream=null;
		BufferedWriter bufferedWriter=null;
		try {
            String logContent=urlStr+","+value;
			if(logFile.exists()){
				outputStream=new FileOutputStream(logFile,true);
			}else{
				outputStream=new FileOutputStream(logFile);
			}
			bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
			try {
				bufferedWriter.write(logContent);
				bufferedWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				bufferedWriter.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static int paseBingHtml(String entityString, String name) {
		Document doc=Jsoup.parse(entityString);
		List<Element> elements=doc.getElementsByClass("mimg");
		elements.stream().forEach(element->{
			String url=element.attr("src");
			String fileUrl="";
			Integer integer=ApplicationProperties.getDownloadedMap().get(url);
			if(integer!=null&&(integer==0||integer==1)){
				integer=null;
				System.out.println("相同图片");
			}else{
				integer=null;
				ApplicationProperties.getDownloadedMap().put(url, 0);
				ApplicationProperties.setFileNo(ApplicationProperties.getFileNo()+1);
				fileUrl=ApplicationProperties.getDownloadFilePath()+File.separator+name;
				File file=new File(fileUrl);
				if(!file.exists())file.mkdirs();
				fileUrl=fileUrl+File.separator+ApplicationProperties.getFileNo()+".jpeg";
				
				downloadFile(url,fileUrl,name);
			}
			
		});
		return elements==null?0:elements.size();
	}

}
