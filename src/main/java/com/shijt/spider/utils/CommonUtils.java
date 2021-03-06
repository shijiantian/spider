package com.shijt.spider.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.stereotype.Service;

import com.shijt.spider.entity.ApplicationProperties;
import com.shijt.spider.entity.QueryParams;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class CommonUtils {
	
	private Lock lock = new ReentrantLock();
	
	public List<String> getStartListJson(String entityString){
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
			System.out.println("获取明星名单失败");
			e.printStackTrace();
		}
		return result;
		
	}

	/**
	 * 从返回结果中解析图片路径
	 * @param imageMap
	 * @param entityString
	 * @param picColor 
	 * @param picSize 
	 * @param queryExt 
	 * @param queue 
	 */
	public boolean parseBaiduImageUrl(String entityString, String picSize, String picColor, String queryExt, BlockingQueue<String> queue,String name) {
		System.out.println("解析imageURl开始");
		JSONObject entity=null;
		try{
			entity=JSONObject.fromObject(new String(entityString.getBytes(),"utf-8"));
		}catch (Throwable e) {
			entity=null;
			System.out.println("解析返回json错误！"+entityString);
			e.printStackTrace();
		}
		if(entity==null)
			return false;
		if(StringUtils.isBlank(queryExt))
			return false;
		entityString=null;
		if(queryExt.contains("site"))
			queryExt=queryExt.substring(0, queryExt.indexOf(" "));
		JSONArray data=null;
		try {
			data=entity.getJSONArray("data");
		} catch (Throwable e) {
			data=null;
			System.out.println("解析data数组失败！"+entityString);
			e.printStackTrace();
		}
		entity=null;
		if(data==null||data.isEmpty())
			return false;
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iterator=data.iterator();
		while(iterator.hasNext()){
			try {
				JSONObject jsonObject=iterator.next();
				if(jsonObject==null||jsonObject.isEmpty()||jsonObject.isNullObject())
					continue;
				String middleURL=jsonObject.getString("middleURL");
				if(StringUtils.isBlank(middleURL))
					continue;
				String fromURLHost=jsonObject.getString("fromURLHost");
				data.remove(jsonObject);
				jsonObject=null;
				Integer isSiteDownloaded=null;
				boolean isDownload=false;
				String fileUrl="";
				lock.lock();
				try{
					isSiteDownloaded=ApplicationProperties.getDownloadedSites().get("picSize"+picSize+"picColor"+picColor+fromURLHost);
					//图片链接是否已下载
					Integer integer=null;
					String urlMD5=MD5.digest(middleURL);
					File existFile=new File(ApplicationProperties.getLogFileSavePath()+File.separator+name+File.separator+urlMD5.substring(4));
					if(existFile.exists()){
						integer=1;
					}else{
						integer=null;
						createFile(name,urlMD5,null);
					}
//					integer=ApplicationProperties.getDownloadedMap().get(middleURL);
					if(integer==null||integer!=1){
						fileUrl=ApplicationProperties.getDownloadFilePath()+File.separator+name;
						File file=new File(fileUrl);
						if(!file.exists())file.mkdirs();
//						ApplicationProperties.getDownloadedMap().put(middleURL, 1);
						ApplicationProperties.setFileNo(ApplicationProperties.getFileNo()+1);
						fileUrl=fileUrl+File.separator+ApplicationProperties.getFileNo()+middleURL.substring(middleURL.lastIndexOf("."));
						isDownload=true;
					}
					//website未访问
					if(isSiteDownloaded==null||isSiteDownloaded!=1){
						ApplicationProperties.getDownloadedSites().put("picSize"+picSize+"picColor"+picColor+fromURLHost, 1);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					lock.unlock();
				}
				if(isDownload){
					downloadFile(middleURL,fileUrl,queryExt,name);
				}
				//website未访问
				if(isSiteDownloaded==null||isSiteDownloaded!=1){
					queue.offer(fromURLHost, 20, TimeUnit.MINUTES);
//					downloadSite(fromURLHost,queryExt,picSize,picColor);
				}
			} catch (Throwable e) {
				System.out.println("下载图片错误!");
				e.printStackTrace();
			}
		}
		System.out.println("解析imageURl结束");
		return true;
	}

	public boolean downloadSite(String fromURLHost, String keyword, String picSize, String picColor, BlockingQueue<String> queue,String name) {
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(0);
		params.setKeyWord(keyword+" site:"+fromURLHost);
		params.setRn(30);
		params.setPicColor(picColor);
		params.setPicSize(picSize);
		do{
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
			String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters,keyword,1);
			if(entityString!=null&&StringUtils.isNotBlank(entityString)){
				this.parseBaiduImageUrl(entityString,picSize,picColor,keyword,queue,name);
			}
			params.setPn(params.getPn()+params.getRn());
		}while(params.getPn()<2000);
		return true;
	}

	private void downloadFile(String urlStr,String fileName,String queryExt,String name){
		System.out.println("下载开始！");
		CloseableHttpClient httpClient=null;
		OutputStream outstream=null;
		CloseableHttpResponse response=null;
		try {
            httpClient=HttpClients.createDefault();
            URIBuilder uri=new URIBuilder(urlStr);
            
            //创建httpGet对象
            HttpGet hg = new HttpGet(uri.build());
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
//                	ApplicationProperties.getDownloadedMap().put(urlStr, 1);
//                	String d5=MD5.digest(urlStr);
//                	createFile(name,d5);
//                	write2File(name,urlStr,1);
                    //获取返回实例entity
                    HttpEntity entity=response.getEntity();
                    //输出
                    outstream=new FileOutputStream(fileName);
                    entity.writeTo(outstream);
                    EntityUtils.consume(entity);
                    System.out.println("下载成功！");
                }else {
                    //输出
//                	ApplicationProperties.getDownloadedMap().put(urlStr, 0);
                	String d5=MD5.digest(urlStr);
                	createFile(name,d5,"rm");
                	System.out.println("下载失败！"+uri.toString());
                } 
            }
            
		} catch (Exception e) {
			System.out.println("下载失败");
			e.printStackTrace();
		}finally {
			try {
				//关闭response
				response.close();
				if(outstream!=null)
					outstream.close();
				httpClient.close();
			} catch (IOException e) {
				System.out.println("关闭下载失败");
				e.printStackTrace();
			}
		}
		
	}

	private void createFile(String name, String d5, String opration) {
		String fileName=d5.substring(4);
		File logParentPath=new File(ApplicationProperties.getLogFileSavePath()+File.separator+name);
		if(!logParentPath.exists())
			logParentPath.mkdirs();
		String logPath=ApplicationProperties.getLogFileSavePath()+File.separator+name+File.separator+fileName;
		File logFile=new File(logPath);
		if("rm".equals(opration)){
			logFile.delete();
		}else{
			if(!logFile.exists()){
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

//	private synchronized void write2File(String name, String urlStr, int value) {
//		File logParentPath=new File(ApplicationProperties.getLogFileSavePath());
//		if(!logParentPath.exists())
//			logParentPath.mkdirs();
//		String logPath=ApplicationProperties.getLogFileSavePath()+File.separator+name;
//		File logFile=new File(logPath);
//		FileOutputStream outputStream=null;
//		OutputStreamWriter outputStreamWriter=null;
//		BufferedWriter bufferedWriter=null;
//		String logContent=urlStr+","+value;
//		ApplicationProperties.getWait2write().add(logContent);
//		if(ApplicationProperties.getWait2write().size()>=1000){
//			try {
//				if(logFile.exists()){
//					outputStream=new FileOutputStream(logFile,true);
//				}else{
//					outputStream=new FileOutputStream(logFile);
//				}
//				outputStreamWriter=new OutputStreamWriter(outputStream);
//				bufferedWriter=new BufferedWriter(outputStreamWriter);
//				try {
//					bufferedWriter.write(logContent);
//					bufferedWriter.newLine();
//				} catch (IOException e) {
//					System.out.println("写入"+name+"文件错误1:");
//					e.printStackTrace();
//				}
//			} catch (FileNotFoundException e) {
//				System.out.println("写入"+name+"文件错误2:");
//				e.printStackTrace();
//			}finally {
//				try {
//					bufferedWriter.close();
//					outputStreamWriter.close();
//					outputStream.close();
//				} catch (IOException e) {
//					System.out.println("关闭写入"+name+"文件错误");
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	public int paseBingHtml(String entityString, String name) {
		return 1;
//		Document doc=Jsoup.parse(entityString);
//		List<Element> elements=doc.getElementsByClass("mimg");
//		elements.stream().forEach(element->{
//			String url=element.attr("src");
//			String fileUrl="";
//			Integer integer=ApplicationProperties.getDownloadedMap().get(url);
//			if(integer!=null&&(integer==0||integer==1)){
//				integer=null;
//				System.out.println("相同图片");
//			}else{
//				integer=null;
//				ApplicationProperties.getDownloadedMap().put(url, 0);
//				ApplicationProperties.setFileNo(ApplicationProperties.getFileNo()+1);
//				fileUrl=ApplicationProperties.getDownloadFilePath()+File.separator+name;
//				File file=new File(fileUrl);
//				if(!file.exists())file.mkdirs();
//				fileUrl=fileUrl+File.separator+ApplicationProperties.getFileNo()+".jpeg";
//				
////				downloadFile(url,fileUrl,name);
//			}
//			
//		});
//		return elements==null?0:elements.size();
	}

}
