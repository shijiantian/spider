package intellif.Threads;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import intellif.entity.ApplicationProperties;
import intellif.entity.QueryParams;
import intellif.utils.CommonUtils;
import intellif.utils.HttpUtils;
import intellif.utils.QueryParamsUtils;

/**
 * 获取图片url线程
 * @author a
 *
 */
public class ImageUrlThread implements Callable<String> {
	
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
				CommonUtils.parseBaiduImageUrl(entityString,picSize,picColor,keyword,queue,name);//解析结果
			}
			while (queue.size()>0) {
				System.out.println("当前线程2："+Thread.currentThread().getName()+"  "+"获取site");
				String site = queue.poll(20,TimeUnit.MINUTES);
				if(StringUtils.isNotBlank(site)){
					System.out.println("当前线程2："+Thread.currentThread().getName()+"  "+site+"下载开始........!");
					CommonUtils.downloadSite(site,keyword,picSize,picColor,queue,name);
					System.out.println("当前线程2："+Thread.currentThread().getName()+"  "+site+"下载结束........!");
				}
			}
			System.out.println("当前线程1："+Thread.currentThread().getName()+" 翻页。");
			params.setPn(params.getPn()+params.getRn());
		}while(params.getPn()<end);
		return "当前线程："+Thread.currentThread().getName()+"  结束返回。";
	}
}
