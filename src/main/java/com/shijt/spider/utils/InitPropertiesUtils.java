package com.shijt.spider.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shijt.spider.entity.ApplicationProperties;
import com.shijt.spider.entity.QueryParams;
import net.sf.json.JSONObject;

@Service
public class InitPropertiesUtils {
	
	@Autowired
	private CommonUtils commonUtils;
	
	/**
	 * 读取配置文件
	 */
	public void initProperties(){
		Properties properties=new Properties();
		try {
			FileInputStream proInput=new FileInputStream("src/main/resources/application.properties");
//			properties.load(proInput);
			properties.load(new InputStreamReader(proInput, "utf-8"));
			proInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ApplicationProperties.setStarListUrl(properties.getProperty("starListUrl"));
		if(StringUtils.isNotBlank(properties.getProperty("area")))
			ApplicationProperties.setArea(properties.getProperty("area"));
		if(StringUtils.isNotBlank(properties.getProperty("sex")))
			ApplicationProperties.setSex(properties.getProperty("sex"));
		ApplicationProperties.setBaidu(properties.getProperty("baidu"));
		ApplicationProperties.setBing(properties.getProperty("bing"));
		ApplicationProperties.setDownloadFileParentPath(properties.getProperty("downloadFileParentPath"));
		ApplicationProperties.setLogFileSavePath(properties.getProperty("logFileSavePath"));
		ApplicationProperties.setBaiduReferer(properties.getProperty("baiduReferer"));
		ApplicationProperties.setFinishedPersons(properties.getProperty("finishedPersons"));
		ApplicationProperties.setThreadNums(Integer.valueOf(properties.getProperty("threadNum", "1")));
		ApplicationProperties.getPicSize().add("");
		ApplicationProperties.getPicSize().add("9");
		ApplicationProperties.getPicSize().add("3");
		ApplicationProperties.getPicSize().add("2");
		ApplicationProperties.getPicSize().add("1");
		ApplicationProperties.getPicSize().add("0");
		ApplicationProperties.getPicColor().add("");
		ApplicationProperties.getPicColor().add("1");
		ApplicationProperties.getPicColor().add("256");
		ApplicationProperties.getPicColor().add("2");
		ApplicationProperties.getPicColor().add("4");
		ApplicationProperties.getPicColor().add("32");
		ApplicationProperties.getPicColor().add("64");
		ApplicationProperties.getPicColor().add("8");
		ApplicationProperties.getPicColor().add("16");
		ApplicationProperties.getPicColor().add("128");
		ApplicationProperties.getPicColor().add("1024");
		ApplicationProperties.getPicColor().add("512");
		ApplicationProperties.getPicColor().add("2048");
		String specified=properties.getProperty("specified");
		if(StringUtils.isNotBlank(specified)){
			ApplicationProperties.setSpecified(specified.split(","));
		}
		String secondWords=properties.getProperty("secondWords");
		if(StringUtils.isNotBlank(secondWords)){
			List<String> secondWordsList=Arrays.asList(secondWords.split(","));
			ApplicationProperties.getSecondWords().add("empty");
			ApplicationProperties.getSecondWords().addAll(secondWordsList);
		}
	}
	
	/**
	 * 从百度获取明星名单
	 * @param area 
	 * @param sex 
	 */
	public void getStarsList(String sex, String area){
		String[] specified=ApplicationProperties.getSpecified();
		if(specified!=null&&specified.length>0){
			List<String> specifiedList=Arrays.asList(specified);
			ApplicationProperties.setStarsList(specifiedList);
		}else{
			List<String> starsList=new ArrayList<>();
			List<String> subStarsList=new ArrayList<>();
			QueryParams params=new QueryParams();
			params.setType(1);
			params.setPn(0);
			params.setRn(100);
			params.setSex(sex);
			params.setArea(area);
			do{
				Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
				String entityString=HttpUtils.sendGet(ApplicationProperties.getStarListUrl(), parameters,null,0);
				subStarsList=commonUtils.getStartListJson(entityString);
				starsList.addAll(subStarsList);
				params.setPn(params.getPn()+subStarsList.size());
			}while(subStarsList.size()!=0);
			ApplicationProperties.setStarsList(starsList);
		}
	}
	
	public static int getPicNums4Everyone(String name) {
		int result=0;
		QueryParams params=new QueryParams();
		params.setType(2);
		params.setPn(0);
		params.setRn(30);
		params.setKeyWord(name);
		Map<String, String> parameters=QueryParamsUtils.getParamStr(params);
		String entityString=HttpUtils.sendGet(ApplicationProperties.getBaidu(), parameters,name,0);
		JSONObject entityJson=JSONObject.fromObject(entityString);
		result=entityJson.getInt("displayNum");
		return result;
	}

	public static List<String> getAreaList() {
		List<String> areaList=new ArrayList<>();
		areaList.add("内地");
		areaList.add("香港");
		areaList.add("台湾");
		areaList.add("日本");
		areaList.add("韩国");
		areaList.add("欧美");
		return areaList;
	}

}
