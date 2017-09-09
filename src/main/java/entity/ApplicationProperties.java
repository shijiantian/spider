package entity;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ApplicationProperties {
	public static final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 4);
		
	private static String starListUrl;
	private static String area;
	private static String sex;
	private static String baidu;
	private static List<String> starsList;
	private static String downloadFilePath;
		
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
}
