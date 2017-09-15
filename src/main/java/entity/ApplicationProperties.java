package entity;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public class ApplicationProperties {
	public static final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 4);
		
	private static String starListUrl;//获取明星名单的接口
	private static String area;//地区
	private static String sex;//性别
	private static String baidu;//百度图片
	private static String bing;//必应图片
	private static List<String> starsList;//明星名单
	private static String downloadFileParentPath;//下载文件父路径
	private static String downloadFilePath;//下载文件完整保存路径
	private static int fileNo;//下载的图片重新编号
	private static String logFileSavePath;//日志保存路径
	private static String baiduReferer;
	private static String finishedPersons;
	private static Integer threadNums;
	
	private static ConcurrentMap<String, Integer> downloadedMap=new ConcurrentHashMap<>();  //一下载图片  1成功 0失败
	private static ConcurrentMap<String, Integer> downloadedSites=new ConcurrentHashMap<>(); //某人已下载过的站 
			
	public static String getStarListUrl() {
		return starListUrl;
	}
	public static void setStarListUrl(String starListUrl) {
		ApplicationProperties.starListUrl = starListUrl;
	}
	public static String getArea() {
		return area;
	}
	public static void setArea(String area) {
		ApplicationProperties.area = area;
	}
	public static String getSex() {
		return sex;
	}
	public static void setSex(String sex) {
		ApplicationProperties.sex = sex;
	}
	public static String getBaidu() {
		return baidu;
	}
	public static void setBaidu(String baidu) {
		ApplicationProperties.baidu = baidu;
	}
	public static List<String> getStarsList() {
		return starsList;
	}
	public static void setStarsList(List<String> starsList) {
		ApplicationProperties.starsList = starsList;
	}
	
	public static String getDownloadFilePath() {
		return downloadFilePath;
	}
	public static void setDownloadFilePath(String downloadFilePath) {
		ApplicationProperties.downloadFilePath = downloadFilePath;
	}
	public static int getFileNo() {
		return fileNo;
	}
	public static void setFileNo(int fileNo) {
		ApplicationProperties.fileNo = fileNo;
	}
	public synchronized static ConcurrentMap<String, Integer> getDownloadedMap() {
		return downloadedMap;
	}
	public synchronized static void setDownloadedMap(ConcurrentMap<String, Integer> downloadedMap) {
		ApplicationProperties.downloadedMap = downloadedMap;
	}
	public static String getLogFileSavePath() {
		return logFileSavePath;
	}
	public static void setLogFileSavePath(String logFileSavePath) {
		ApplicationProperties.logFileSavePath = logFileSavePath;
	}
	public static String getBing() {
		return bing;
	}
	public static void setBing(String bing) {
		ApplicationProperties.bing = bing;
	}
	public synchronized static ConcurrentMap<String, Integer> getDownloadedSites() {
		return downloadedSites;
	}
	public synchronized static void setDownloadedSites(ConcurrentMap<String, Integer> downloadedSites) {
		ApplicationProperties.downloadedSites = downloadedSites;
	}
	public static String getDownloadFileParentPath() {
		return downloadFileParentPath;
	}
	public static void setDownloadFileParentPath(String downloadFileParentPath) {
		ApplicationProperties.downloadFileParentPath = downloadFileParentPath;
	}
	public static String getBaiduReferer() {
		return baiduReferer;
	}
	public static void setBaiduReferer(String baiduReferer) {
		ApplicationProperties.baiduReferer = baiduReferer;
	}
	public static String getFinishedPersons() {
		return finishedPersons;
	}
	public static void setFinishedPersons(String finishedPersons) {
		ApplicationProperties.finishedPersons = finishedPersons;
	}
	public static Integer getThreadNums() {
		return threadNums;
	}
	public static void setThreadNums(Integer threadNums) {
		ApplicationProperties.threadNums = threadNums;
	}
}
