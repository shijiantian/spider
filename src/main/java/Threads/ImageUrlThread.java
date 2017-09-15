package Threads;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

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
		params.setRn(30);
		do{
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params); //设置参数
			String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters,name,1);//发送请求
			if(entityString!=null&&StringUtils.isNotBlank(entityString)){
				CommonUtils.parseBaiduImageUrl(entityString);//解析结果
			}
			params.setPn(params.getPn()+params.getRn());
		}while(params.getPn()<end);
		return true;
	}

	
	
}
