package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
		ApplicationProperties.setDownloadFileParentPath(properties.getProperty("downloadFileParentPath").toString());
		ApplicationProperties.setLogFileSavePath(properties.getProperty("logFileSavePath").toString());
		ApplicationProperties.setBaiduReferer(properties.getProperty("baiduReferer"));
		ApplicationProperties.setFinishedPersons(properties.getProperty("finishedPersons").toString());
		ApplicationProperties.setThreadNums(Integer.valueOf(properties.getProperty("threadNum", "1")));
		ApplicationProperties.getPicSize().add("9");
		ApplicationProperties.getPicSize().add("3");
		ApplicationProperties.getPicSize().add("2");
		ApplicationProperties.getPicSize().add("1");
		ApplicationProperties.getPicSize().add("0");
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
	}
	
	/**
	 * 从百度获取明星名单
	 * @param area 
	 * @param sex 
	 */
	public static void getStarsList(String sex, String area){
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
