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
	private String picSize;
	private String picColor;
	
	public ImageUrlThread(String name,int pn,int end,String picSize,String picColor) {
		this.name=name;
		this.pn=pn;
		this.end=end;
		this.picColor=picColor;
		this.picSize=picSize;
	}

	@Override
	public Boolean call() throws Exception {
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(pn);
		params.setName(name);
		params.setRn(30);
		params.setPicColor(picColor);
		params.setPicSize(picSize);
		do{
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params); //设置参数
			String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters,name,1);//发送请求
			if(entityString!=null&&StringUtils.isNotBlank(entityString)){
				CommonUtils.parseBaiduImageUrl(entityString,picSize,picColor,name);//解析结果
			}
			params.setPn(params.getPn()+params.getRn());
		}while(params.getPn()<end);
		return true;
	}

	
	
}
