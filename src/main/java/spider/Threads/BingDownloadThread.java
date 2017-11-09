package spider.Threads;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import spider.entity.QueryParams;
import spider.utils.QueryParamsUtils;

public class BingDownloadThread implements Callable<Boolean> {
	private List<String> persons;
	
	public BingDownloadThread(List<String> persons) {
		this.persons=persons;
	}

	@Override
	public Boolean call() throws Exception {
		//获取每个人的图片路径 
		for(String name:persons){
			QueryParams params=new QueryParams();
			params.setType(3);
			params.setPn(0);
			params.setKeyWord(name);
			int resultNum=0;
			do{
				params.setRn(100);
				Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
				System.out.println(parameters.get("q"));
				System.out.println("啊啊啊啊啊啊啊啊啊.........................");
//				synchronized (BingDownloadThread.class) {
//					resultNum=HttpUtils.sendHtmlGet(ApplicationProperties.getBing(), parameters);
//				}
				params.setPn(params.getPn()+params.getRn());
			}while(resultNum>0);//待定
		}	
		return true;
	}

}
