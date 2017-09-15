package input;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import entity.ApplicationProperties;
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
		List<String> sexlist=null;
		List<String> arealist=null;
		if(StringUtils.isBlank(ApplicationProperties.getSex())){
			sexlist=new ArrayList<>();
			sexlist.add("男");
			sexlist.add("女");
		}
		if(StringUtils.isBlank(ApplicationProperties.getArea())){
			arealist=InitPropertiesUtils.getAreaList();
		}
		//未设置性别和地区 则全部下载
		if(sexlist!=null&&arealist!=null){
			for(String sex:sexlist){
				for(String area:arealist){
					ApplicationProperties.setDownloadFilePath(ApplicationProperties.getDownloadFileParentPath()+File.separator+sex+File.separator+area);
					//从百度获取名单
					InitPropertiesUtils.getStarsList(sex,area);
					//为每个人创建百度下载任务
					DownloadUtil.createBaiduDownloadTask();
				}
			}
		}else if(sexlist!=null&&arealist==null){
		//设置了地区则下载该地区所有性别的明星
			for(String sex:sexlist){
				ApplicationProperties.setDownloadFilePath(ApplicationProperties.getDownloadFileParentPath()+File.separator+sex+File.separator+ApplicationProperties.getArea());

				//从百度获取名单
				InitPropertiesUtils.getStarsList(sex,ApplicationProperties.getArea());
				//为每个人创建百度下载任务
				DownloadUtil.createBaiduDownloadTask();
			}
		}else if(sexlist==null&&arealist!=null){
		//设置了性别则下载该性别下所有地区的明星
			for(String area:arealist){
				ApplicationProperties.setDownloadFilePath(ApplicationProperties.getDownloadFileParentPath()+File.separator+ApplicationProperties.getSex()+File.separator+area);
				//从百度获取名单
				InitPropertiesUtils.getStarsList(ApplicationProperties.getSex(),area);
				//为每个人创建百度下载任务
				DownloadUtil.createBaiduDownloadTask();
			}
		}else if(sexlist==null&&arealist==null){
		//设置了性别和地区则下载该性别和地区下的明星
			ApplicationProperties.setDownloadFilePath(ApplicationProperties.getDownloadFileParentPath()+File.separator+ApplicationProperties.getSex()+File.separator+ApplicationProperties.getArea());

			//从百度获取名单
			InitPropertiesUtils.getStarsList(ApplicationProperties.getSex(),ApplicationProperties.getArea());
			//为每个人创建百度下载任务
			DownloadUtil.createBaiduDownloadTask();
		}
		System.out.println("全部下载完成");
		
		//为每个人创建必应下载任务
//		DownloadUtil.createBingDownloadTask();
	}
}
