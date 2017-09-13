package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinTask;

import Threads.ImageUrlThread;
import entity.ApplicationProperties;
import entity.QueryParams;

public class DownloadUtil {
	
	
	
	/**
	 * 为每个人创建百度下载任务
	 */
	public static void createBaiduDownloadTask() {
		List<String> persons=ApplicationProperties.getStarsList();
		List<ForkJoinTask<Boolean>> taskList=new ArrayList<>();
		//获取每个人的图片路径 
		for(String name:persons){
			//获取一个人的图片总数
			ApplicationProperties.setFileNo(0);
//			int picNums=InitPropertiesUtils.getPicNums4Everyone(name);
			int picNums=2000;
			int size=Runtime.getRuntime().availableProcessors()+3;
			final int picNum4Thread=picNums/size;
			for (int i = 0; i < picNums; i += picNum4Thread) {
	            // 当前任务起始位置（包含）
	            final int pn = i;
	            // 当前任务结束位置（不包含
	            final int end = Math.min(i + picNum4Thread, picNums);
	            // 提交任务，并将任务加入任务列表
	            ImageUrlThread thread=new ImageUrlThread(name, pn, end);
	            taskList.add(ApplicationProperties.pool.submit(thread));
	        }
			for(int i=0;i<taskList.size();i++){
				taskList.get(i).join();
			}
			FileOutputStream outputStream=null;
			BufferedWriter bufferedWriter=null;
			try {
				File logParentPath=new File(ApplicationProperties.getLogFileSavePath());
				if(!logParentPath.exists())
					logParentPath.mkdirs();
				String logPath=ApplicationProperties.getLogFileSavePath()+File.separator+name;
				File logFile=new File(logPath);
				if(logFile.exists()){
					outputStream=new FileOutputStream(logFile,true);
				}else{
					outputStream=new FileOutputStream(logFile);
				}
				bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
				ConcurrentMap<String,Integer> historyMap=ApplicationProperties.getDownloadedMap();
				for(String key:historyMap.keySet()){
					String logContent="fileUrl:"+key+",  Status："+historyMap.get(key);
					try {
						bufferedWriter.write(logContent);
						bufferedWriter.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				ApplicationProperties.getDownloadedMap().clear();
				taskList.clear();
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
	}

	/**
	 * 为每个人创建必应下载任务
	 */
	public static void createBingDownloadTask() {
		List<String> persons=ApplicationProperties.getStarsList();
		List<ForkJoinTask<Boolean>> taskList=new ArrayList<>();
		//获取每个人的图片路径 
		for(String name:persons){
			//获取图片链接
			QueryParams params=new QueryParams();
			params.setType(3);
			params.setPn(0);
			params.setName(name);
			int resultNum=0;
			do{
				params.setRn(30);
				Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
				String entityString=HttpUtils.sendGet(ApplicationProperties.getBing(), parameters);
				resultNum=CommonUtils.paseBingHtml(entityString,name);
				params.setPn(params.getPn()+params.getRn());
			}while(resultNum>0);//待定
			for(int i=0;i<taskList.size();i++){
				taskList.get(i).join();
			}
			FileOutputStream outputStream=null;
			BufferedWriter bufferedWriter=null;
			try {
				File logParentPath=new File(ApplicationProperties.getLogFileSavePath());
				if(!logParentPath.exists())
					logParentPath.mkdirs();
				String logPath=ApplicationProperties.getLogFileSavePath()+File.separator+name;
				File logFile=new File(logPath);
				if(logFile.exists()){
					outputStream=new FileOutputStream(logFile,true);
				}else{
					outputStream=new FileOutputStream(logFile);
				}
				bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
				ConcurrentMap<String,Integer> historyMap=ApplicationProperties.getDownloadedMap();
				for(String key:historyMap.keySet()){
					String logContent="fileUrl:"+key+",  Status："+historyMap.get(key);
					try {
						bufferedWriter.write(logContent);
						bufferedWriter.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				ApplicationProperties.getDownloadedMap().clear();
				taskList.clear();
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
	}
}
