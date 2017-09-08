package utils;

import java.util.HashMap;
import java.util.Map;

import entity.QueryParams;

public class QueryParamsUtils {
	
	public static Map<String, String> getParamStr(QueryParams queryParams){
		Map<String, String> params=new HashMap<>();
		switch (queryParams.getType()) {
		case 1:
			params.put("resource_id", "28266");
            params.put("from_mid","1");
            params.put("format", "json");
            params.put("ie", "utf-8");
            params.put("oe", "utf-8");
            params.put("query", "明星");
            params.put("sort_key", "");
            params.put("sort_type", "1");
            params.put("stat0", queryParams.getSex());//性别
            params.put("stat1", queryParams.getArea());//地区
            params.put("stat2", "");
            params.put("stat3", "");
            params.put("pn", ""+queryParams.getPn());//偏移量
            params.put("rn", ""+queryParams.getRn());//每页人数 最大100
			break;
		case 2:
			params.put("tn","resultjson_com");
			params.put("ipn","rj");
			params.put("ct","201326592");
			params.put("is","");
			params.put("fp","");
			params.put("queryWord","");
			params.put("cl","");
			params.put("lm","");
			params.put("ie","");
			params.put("oe","");
			params.put("adpicid","");
			params.put("st","");
			params.put("word",queryParams.getName());
			params.put("z","");
			params.put("ic","");
			params.put("s","");
			params.put("se","");
			params.put("tab","");
			params.put("width","");
			params.put("height","");
			params.put("face","");
			params.put("istype","");
			params.put("qc","");
			params.put("nc","");
			params.put("fr","");
			params.put("step_word",queryParams.getName());
			params.put("pn",""+queryParams.getPn());
			params.put("rn",""+queryParams.getRn());
			params.put("gsm","1e");
		default:
			
			break;
		}
		return params;	
		
	}
}
