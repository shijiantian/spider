package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entity.ApplicationProperties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommonUtils {
	public static List<String> getStartListJson(String entityString){
		List<String> result=new ArrayList<>();
		try {
			JSONObject entity=JSONObject.fromObject(entityString);
			JSONArray data=entity.getJSONArray("data");
			if(data==null||data.isEmpty())
				return result;
			JSONObject dataObj=data.getJSONObject(0);
			if(dataObj.isEmpty()||dataObj.isNullObject())
				return result;
			JSONArray resultArray=dataObj.getJSONArray("result");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator=resultArray.iterator();
			while (iterator.hasNext()) {
				JSONObject object=iterator.next();
				result.add(object.getString("ename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}

	/**
	 * 从返回结果中解析图片路径
	 * @param imageMap
	 * @param entityString
	 */
	public static boolean parseImageUrl(String entityString) {
		JSONObject entity=JSONObject.fromObject(entityString);
		JSONArray data=entity.getJSONArray("data");
		if(data==null||data.isEmpty())
			return false;
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iterator=data.iterator();
		while(iterator.hasNext()){
			JSONObject jsonObject=iterator.next();
			if(jsonObject==null||jsonObject.isEmpty()||jsonObject.isNullObject())
				continue;
			String middleURL=jsonObject.getString("middleURL");
			try {
				ApplicationProperties.getWaitToDownLoad().put(middleURL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
		}
		return true;
	}
}
