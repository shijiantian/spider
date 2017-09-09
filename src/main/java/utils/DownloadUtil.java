package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;

import Threads.ImageUrlThread;
import entity.ApplicationProperties;

public class DownloadUtil {
	
	
	
	/**
	 * 为每个人创建下载任务
	 */
	public static void createDownloadTask() {
		List<String> persons=ApplicationProperties.getStarsList();
		List<ForkJoinTask<Boolean>> taskList=new ArrayList<>();
		//获取每个人的图片路径 
		for(String name:persons){
			//获取一个人的图片总数
			int picNums=InitPropertiesUtils.getPicNums4Everyone(name);
			int size=Runtime.getRuntime().availableProcessors()+1;
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
			taskList.clear();
		}	
	}
}
