package input;

import utils.DownloadUtil;
import utils.InitPropertiesUtils;

/**
 * 爬虫入口
 * @author 
 *
 */
public class Entrance {
	
	public static void main(String [] args){
		//读取配置文件
		InitPropertiesUtils.initProperties();
		//从百度获取明星名单
		InitPropertiesUtils.getStarsList();
		//为每个人创建下载任务
		DownloadUtil.createDownloadTask();
	}
}
