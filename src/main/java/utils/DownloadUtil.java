package utils;

import java.util.List;
import java.util.concurrent.Future;

import Threads.ImageUrlThread;
import entity.ApplicationProperties;

public class DownloadUtil {
	
	/**
	 * 为每个人创建下载任务
	 */
	public static void createDownloadTask(){
		List<String> persons=ApplicationProperties.getStarsList();
		//获取每个人的图片路径 
		ApplicationProperties.pool.submit(()->{
			for(String name:persons){
				ImageUrlThread imageUrlThread=new ImageUrlThread(name);
				Future<Boolean> future=ApplicationProperties.pool.submit(imageUrlThread);
				try {
					future.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}
