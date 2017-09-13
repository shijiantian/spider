package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import entity.ApplicationProperties;
import entity.QueryParams;
import net.sf.json.JSONObject;

public class InitPropertiesUtils {
	
	/**
	 * 读取配置文件
	 */
	public static void initProperties(){
		Properties properties=new Properties();
		try {
			FileInputStream proInput=new FileInputStream("application.properties");
			properties.load(proInput);
			proInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ApplicationProperties.setStarListUrl(properties.getProperty("starListUrl").toString());
		ApplicationProperties.setArea(properties.getProperty("area").toString());
		ApplicationProperties.setSex(properties.getProperty("sex").toString());
		ApplicationProperties.setBaidu(properties.getProperty("baidu").toString());
		ApplicationProperties.setBing(properties.getProperty("bing").toString());
		String sexStr=StringUtils.isBlank(ApplicationProperties.getSex())?"全部":ApplicationProperties.getSex();
		String areaStr=StringUtils.isBlank(ApplicationProperties.getArea())?"全部":ApplicationProperties.getArea();
		ApplicationProperties.setDownloadFilePath(properties.getProperty("downloadFilePath").toString()+File.separator+sexStr+File.separator+areaStr);
		ApplicationProperties.setLogFileSavePath(properties.getProperty("logFileSavePath").toString());
	}
	
	/**
	 * 从百度获取明星名单
	 */
	public static void getStarsList(){
		List<String> starsList=new ArrayList<>();
		List<String> subStarsList=new ArrayList<>();
		QueryParams params=new QueryParams();
		params.setType(1);
		params.setPn(0);
		params.setRn(100);
		params.setSex(ApplicationProperties.getSex());
		params.setArea(ApplicationProperties.getArea());
		do{
			Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
			String entityString=HttpUtils.sendGet(ApplicationProperties.getStarListUrl(), parameters);
			subStarsList=CommonUtils.getStartListJson(entityString);
			starsList.addAll(subStarsList);
			params.setPn(params.getPn()+subStarsList.size());
		}while(subStarsList.size()!=0);
		ApplicationProperties.setStarsList(starsList);
	}
	
	public static int getPicNums4Everyone(String name) {
		int result=0;
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(0);
		params.setRn(30);
		params.setName(name);
		Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
		String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters);
		JSONObject entityJson=JSONObject.fromObject(entityString);
		result=entityJson.getInt("displayNum");
		return result;
	}
}
