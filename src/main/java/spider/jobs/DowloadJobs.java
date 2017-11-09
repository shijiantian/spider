package spider.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import spider.entity.ApplicationProperties;
import spider.utils.DownloadUtil;
import spider.utils.InitPropertiesUtils;

@Component
public class DowloadJobs {
	
	@Autowired
	private InitPropertiesUtils initPropertiesUtils;
	@Autowired
	private DownloadUtil downloadUtil;
		
	@Scheduled(fixedDelay=10 * 1000)
	public void downloadJob(){
		//读取配置文件
		initPropertiesUtils.initProperties();
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
		System.out.println("下载开始..........");
		//未设置性别和地区 则全部下载
		if(sexlist!=null&&arealist!=null){
			for(String sex:sexlist){
				for(String area:arealist){
					setDownloadFilePath(sex,area);
					//从百度获取名单
					initPropertiesUtils.getStarsList(sex,area);
					//为每个人创建百度下载任务
					downloadUtil.createBaiduDownloadTask();
				}
			}
		}else if(sexlist!=null&&arealist==null){
		//设置了地区则下载该地区所有性别的明星
			for(String sex:sexlist){
				setDownloadFilePath(sex,ApplicationProperties.getArea());
				//从百度获取名单
				initPropertiesUtils.getStarsList(sex,ApplicationProperties.getArea());
				//为每个人创建百度下载任务
				downloadUtil.createBaiduDownloadTask();
			}
		}else if(sexlist==null&&arealist!=null){
		//设置了性别则下载该性别下所有地区的明星
			for(String area:arealist){
				setDownloadFilePath(ApplicationProperties.getSex(),area);
				//从百度获取名单
				initPropertiesUtils.getStarsList(ApplicationProperties.getSex(),area);
				//为每个人创建百度下载任务
				downloadUtil.createBaiduDownloadTask();
			}
		}else if(sexlist==null&&arealist==null){
		//设置了性别和地区则下载该性别和地区下的明星
			setDownloadFilePath(ApplicationProperties.getSex(),ApplicationProperties.getArea());
			//从百度获取名单
			initPropertiesUtils.getStarsList(ApplicationProperties.getSex(),ApplicationProperties.getArea());
			//为每个人创建百度下载任务
			downloadUtil.createBaiduDownloadTask();
		}
		System.out.println("全部下载完成..........");
	}
	
	private static void setDownloadFilePath(String sex, String area) {
		if(ApplicationProperties.getSpecified()!=null&&ApplicationProperties.getSpecified().length>0){
			ApplicationProperties.setDownloadFilePath(ApplicationProperties.getDownloadFileParentPath());
		}else{
			ApplicationProperties.setDownloadFilePath(ApplicationProperties.getDownloadFileParentPath()+File.separator+sex+File.separator+area);
		}
	}
}
