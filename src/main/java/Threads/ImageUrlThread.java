package Threads;

import java.util.Map;
import java.util.concurrent.Callable;

import entity.ApplicationProperties;
import entity.QueryParams;
import utils.CommonUtils;
import utils.HttpUtils;
import utils.QueryParamsUtils;

/**
 * 获取图片url线程
 * @author a
 *
 */
public class ImageUrlThread implements Callable<Boolean> {
	
	private String name;
	
	public ImageUrlThread(String name) {
		this.name=name;
	}

	@Override
	public Boolean call() throws Exception {
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(0);
		params.setRn(30);
		params.setName(name);
		boolean notEnd=true;
		do{
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
			String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters);
			notEnd=CommonUtils.parseImageUrl(entityString);
			params.setPn(params.getPn()+params.getRn());
		}while(notEnd);
		return true;
	}

	
	
}
