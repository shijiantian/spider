package utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mysql.fabric.xmlrpc.base.Data;

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
			params.put("fp","result");
			params.put("queryWord","");
			params.put("cl","2");//图片搜索
//			params.put("lm","0");//时间限制
			params.put("ie","utf-8");//查询关键词的编码
			params.put("oe","utf-8");
			params.put("adpicid","");
//			params.put("st","");
			params.put("gpc", "stf=1505232000,1505318398|stftype=2");
			params.put("word",queryParams.getName());
			params.put("z","");
			params.put("ic","0");
			params.put("s","");
			params.put("se","");
			params.put("tab","");
			params.put("width","");
			params.put("height","");
			params.put("face","0");
			params.put("istype","2");
			params.put("qc","");
			params.put("nc","1");
			params.put("fr","");
			params.put("step_word",queryParams.getName());
			params.put("pn",""+queryParams.getPn());
			params.put("rn",""+queryParams.getRn());//搜索结果显示条数，缺省设置rn=10，取值范围:10-100
			params.put("gsm","78&1504948216405");
		case 3:
			params.put("q",queryParams.getName());
			params.put("first",""+queryParams.getPn());
			params.put("count",""+queryParams.getRn());
			params.put("relo","2");
			params.put("relp","10");
			params.put("lostate","c");
			params.put("mmasync","1");
			params.put("dgState","c*9_y*1182s1065s1146s1034s970s1014s1020s1051s1177_i*38_w*200");
			params.put("IG","A81D0C924FFD433CB8684FA28A335D2E");
			params.put("SFX","2");
			params.put("iid","images.5753");
		default:
			
			break;
		}
		return params;	
		
	}
}
