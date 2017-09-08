package entity;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationProperties {
	public static final ExecutorService pool=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+4);
	
	private static String starListUrl;
	private static String area;
	private static String sex;
	private static String baidu;
	private static List<String> starsList;
	
	private static ConcurrentMap<String, String> imageMap=new ConcurrentHashMap<>();//已下载
	private static BlockingQueue<String> waitToDownLoad=new LinkedBlockingQueue<>();
	
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
	public static ConcurrentMap<String, String> getImageMap() {
		return imageMap;
	}
	public static void setImageMap(ConcurrentMap<String, String> imageMap) {
		ApplicationProperties.imageMap = imageMap;
	}
	public static BlockingQueue<String> getWaitToDownLoad() {
		return waitToDownLoad;
	}
	public static void setWaitToDownLoad(BlockingQueue<String> waitToDownLoad) {
		ApplicationProperties.waitToDownLoad = waitToDownLoad;
	}
}
