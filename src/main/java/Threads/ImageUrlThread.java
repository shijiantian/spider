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
	private int pn;
	private int end;
	
	public ImageUrlThread(String name,int pn,int end) {
		this.name=name;
		this.pn=pn;
		this.end=end;
	}

	@Override
	public Boolean call() throws Exception {
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(pn);
		params.setName(name);
		do{
			int rn =Math.min(36, end-params.getPn());
			params.setRn(rn);
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
			String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters);
			CommonUtils.parseBaiduImageUrl(entityString);
			params.setPn(params.getPn()+params.getRn());
		}while(params.getPn()<end);
		return true;
	}

	
	
}
