package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
			//检查下载日志，载入已下载图片信息
			if(checkLogInfo(name))
				continue;
			ApplicationProperties.setFileNo(0);
			loadHistory(name);
//			int picNums=InitPropertiesUtils.getPicNums4Everyone(name);
			int picNums=2000;			//获取一个人的图片总数，由于百度每个关键词图片数量不超过2000所以不在获取直接设置为2000
//			int size=Runtime.getRuntime().availableProcessors()+3;//要开启的线程数量
			int size=ApplicationProperties.getThreadNums();
			final int picNum4Thread=picNums/size;  //每个线程要处理的数量
			for(String picSize:ApplicationProperties.getPicSize()){
				for(String picColor:ApplicationProperties.getPicColor()){
					for (int i = 0; i < picNums; i += picNum4Thread) {
			            // 当前任务起始位置（包含）
			            final int pn = i;
			            // 当前任务结束位置（不包含
			            final int end = Math.min(i + picNum4Thread, picNums);
			            // 提交任务，并将任务加入任务列表
			            ImageUrlThread thread=new ImageUrlThread(name, pn, end,picSize,picColor);
			            taskList.add(ApplicationProperties.pool.submit(thread));
			        }
					for(int i=0;i<taskList.size();i++){
						taskList.get(i).join();
					}
				}
			}
			//下载结束清除
			clear(taskList,name);
			
		}	
	}

	/**
	 * 将下载历史加载到内存中
	 * @param name
	 */
	private static void loadHistory(String name) {
		String fileName=ApplicationProperties.getLogFileSavePath()+File.separator+name;
		File historyFile=new File(fileName);
		if(historyFile.exists()){
			ConcurrentMap<String, Integer> downloadedMap=ApplicationProperties.getDownloadedMap();
			FileInputStream inputStream=null;
			BufferedReader reader=null;
			try {
				inputStream=new FileInputStream(historyFile);
				reader=new BufferedReader(new InputStreamReader(inputStream));
				String line=null;
				int lineNo=0;
				while ((line=reader.readLine())!=null) {
					lineNo++;
					int seperateIndex=line.lastIndexOf(",");
					String key=line.substring(0, seperateIndex);
					String value=line.substring(seperateIndex+1);
					downloadedMap.put(key, Integer.parseInt(value));
				}
				ApplicationProperties.setFileNo(lineNo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 检查该人图片是否已下载完成 已下载过返回true 为下载返回false
	 * @param name
	 * @return
	 */
	private static boolean checkLogInfo(String name) {
		boolean isfinished=false;
		String fileName=ApplicationProperties.getFinishedPersons();
		File file=new File(ApplicationProperties.getLogFileSavePath()+File.separator+fileName);
		if(!file.exists())
			return false;
		FileInputStream inputStream=null;
		BufferedReader reader=null;
		try {
			inputStream=new FileInputStream(file);
			reader=new BufferedReader(new InputStreamReader(inputStream));
			String line=null;
			while((line=reader.readLine())!=null){
				if(line.equals(name)){
					isfinished=true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				reader.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return isfinished;
	}

	private static void clear(List<ForkJoinTask<Boolean>> taskList, String name) {
		ApplicationProperties.getDownloadedMap().clear();
		ApplicationProperties.getDownloadedSites().clear();
		taskList.clear();
		//写入已完成人员文件
		File logPath=new File(ApplicationProperties.getLogFileSavePath());
		if(!logPath.exists())
			logPath.mkdirs();
		String filePath=logPath+File.separator+ApplicationProperties.getFinishedPersons();
		//将名字写入文件
		writeLine2File(filePath,name);
		
	}

	public static void writeLine2File(String filePath, String name) {
		File finishedPersons=new File(filePath);
		FileOutputStream outputStream=null;
		BufferedWriter bufferedWriter=null;
		try {
			if(finishedPersons.exists()){
				outputStream=new FileOutputStream(finishedPersons,true);
			}else{
				outputStream=new FileOutputStream(finishedPersons);
			}
			bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
			try {
				bufferedWriter.write(name);
				bufferedWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
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
				String entityString=HttpUtils.sendGet(ApplicationProperties.getBing(), parameters,name,1);
				resultNum=CommonUtils.paseBingHtml(entityString,name);
				params.setPn(params.getPn()+params.getRn());
			}while(resultNum>0);//待定
			for(int i=0;i<taskList.size();i++){
				taskList.get(i).join();
			}
			clear(taskList, name);
		}	
	}
}
