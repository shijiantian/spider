package intellif.utils;

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intellif.entity.ApplicationProperties;
import intellif.entity.QueryParams;

@Service
public class DownloadUtil {
	
	@Autowired
	private CommonUtils commonUtils;
	
	/**
	 * 为每个人创建百度下载任务
	 */
	public void createBaiduDownloadTask() {
		List<String> persons=ApplicationProperties.getStarsList();
		List<ForkJoinTask<String>> taskList=new ArrayList<ForkJoinTask<String>>();
		//获取每个人的图片路径 
		for(String name:persons){
			System.out.println(name+":图片下载开始!");
			//检查下载日志，载入已下载图片信息
			if(checkLogInfo(name))
				continue;
			ApplicationProperties.setFileNo(0);
			loadHistory(name);
			int picNums=InitPropertiesUtils.getPicNums4Everyone(name);
//			int picNums=2000;			//获取一个人的图片总数，由于百度每个关键词图片数量不超过2000所以不在获取直接设置为2000
//			int size=Runtime.getRuntime().availableProcessors()+3;//要开启的线程数量
			int size=ApplicationProperties.getThreadNums();
			final int picNum4Thread=picNums/size;  //每个线程要处理的数量
			for(String secondWord:ApplicationProperties.getSecondWords()){
				String keyWord=name;
				if(!"empty".equals(secondWord)){
					keyWord=name+" "+secondWord;
				}
				for(String picSize:ApplicationProperties.getPicSize()){
					for(String picColor:ApplicationProperties.getPicColor()){
						for (int i = 0; i < picNums; i += picNum4Thread) {
				            // 当前任务起始位置（包含）
				            final int pn = i;
				            // 当前任务结束位置（不包含
				            final int end = Math.min(i + picNum4Thread, picNums);
				            // 提交任务，并将任务加入任务列表
				            ImageUrlThread thread=new ImageUrlThread(keyWord, pn, end,picSize,picColor,name);
				            taskList.add(ApplicationProperties.pool.submit(thread));
				        }
						for(int i=0;i<taskList.size();i++){
							try {
								System.out.println(taskList.get(i).get());
							} catch (InterruptedException | ExecutionException e) {
								System.out.println("线程异常：");
								e.printStackTrace();
							}
							System.out.println("taskList:"+i+"完成！");
						}
						taskList.clear();
						ApplicationProperties.getWait2downloadqueue().clear();
						ApplicationProperties.getDownloadedSites().clear();
					}
				}
			}
			
			//下载结束清除
			System.out.println(name+":图片下载结束！");
			clear(taskList,name);
		}	
	}

	/**
	 * 将下载历史加载到内存中
	 * @param name
	 */
	private void loadHistory(String name) {
		String fileName=ApplicationProperties.getLogFileSavePath()+File.separator+name;
		File historyFile=new File(fileName);
		if(historyFile.exists()){
			String[] files=historyFile.list();
			ApplicationProperties.setFileNo(files.length);
		}
		
	}

	/**
	 * 检查该人图片是否已下载完成 已下载过返回true 未下载返回false
	 * @param name
	 * @return
	 */
	private boolean checkLogInfo(String name) {
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

	private void clear(List<ForkJoinTask<String>> taskList, String name) {
//		ApplicationProperties.getDownloadedMap().clear();
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

	public void writeLine2File(String filePath, String name) {
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
	public void createBingDownloadTask() {
		List<String> persons=ApplicationProperties.getStarsList();
		List<ForkJoinTask<String>> taskList=new ArrayList<ForkJoinTask<String>>();
		//获取每个人的图片路径 
		for(String name:persons){
			//获取图片链接
			QueryParams params=new QueryParams();
			params.setType(3);
			params.setPn(0);
			params.setKeyWord(name);
			int resultNum=0;
			do{
				params.setRn(30);
				Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
				String entityString=HttpUtils.sendGet(ApplicationProperties.getBing(), parameters,name,1);
				resultNum=commonUtils.paseBingHtml(entityString,name);
				params.setPn(params.getPn()+params.getRn());
			}while(resultNum>0);//待定
			for(int i=0;i<taskList.size();i++){
				taskList.get(i).join();
			}
			clear(taskList, name);
		}	
	}
	
	private class ImageUrlThread implements Callable<String> {
		
		private String keyword;
		private int pn;
		private int end;
		private String picSize;
		private String picColor;
		private String name;
			
		public ImageUrlThread(String keyword,int pn,int end,String picSize,String picColor,String name) {
			this.keyword=keyword;
			this.pn=pn;
			this.end=end;
			this.picColor=picColor;
			this.picSize=picSize;
			this.name=name;
		}

		@Override
		public String call() throws Exception {
			BlockingQueue<String> queue=ApplicationProperties.getWait2downloadqueue();
			QueryParams params=new QueryParams();
			params.setType(2);
			params.setPn(pn);
			params.setKeyWord(keyword);
			params.setRn(30);
			params.setPicColor(picColor);
			params.setPicSize(picSize);
			do{
				System.out.println("当前线程1："+Thread.currentThread().getName());
				Map<String, String> parameters=QueryParamsUtils.getParamStr(params); //设置参数
				String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters,keyword,1);//发送请求
				if(entityString!=null&&StringUtils.isNotBlank(entityString)){
					try {
						commonUtils.parseBaiduImageUrl(entityString,picSize,picColor,keyword,queue,name);//解析结果
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				while (queue.size()>0) {
					System.out.println("当前线程2："+Thread.currentThread().getName()+"  "+"获取site");
					String site = queue.poll(20,TimeUnit.MINUTES);
					if(StringUtils.isNotBlank(site)){
						System.out.println("当前线程2："+Thread.currentThread().getName()+"  "+site+"下载开始........!");
						commonUtils.downloadSite(site,keyword,picSize,picColor,queue,name);
						System.out.println("当前线程2："+Thread.currentThread().getName()+"  "+site+"下载结束........!");
					}
				}
				System.out.println("当前线程1："+Thread.currentThread().getName()+" 翻页。");
				params.setPn(params.getPn()+params.getRn());
			}while(params.getPn()<end);
			return "当前线程："+Thread.currentThread().getName()+"  结束返回。";
		}
	}
}
