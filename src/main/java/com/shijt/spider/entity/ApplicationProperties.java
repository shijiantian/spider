package com.shijt.spider.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationProperties {
	public static final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 4);
		
	private static String starListUrl;//获取明星名单的接口
	private static String area;//地区
	private static String sex;//性别
	private static String baidu;//百度图片
	private static List<String> starsList;//明星名单
	private static String downloadFileParentPath;//下载文件父路径
	private static String downloadFilePath;//下载文件完整保存路径
	private static int fileNo;//下载的图片重新编号
	private static String logFileSavePath;//日志保存路径
	private static String baiduReferer;
	private static String finishedPersons;
	private static Integer threadNums;
	private static String[] specified;
	
//	private static ConcurrentMap<String, Integer> downloadedMap=new ConcurrentHashMap<>();  //一下载图片  1成功 0失败
	private static ConcurrentMap<String, Integer> downloadedSites=new ConcurrentHashMap<>(); //某人已下载过的站 
	private static BlockingQueue<String> wait2downloadqueue=new LinkedBlockingQueue<>();
//	private static List<String> wait2write=new ArrayList<>();
	
	private static List<String> picSize=new ArrayList<>();
	private static List<String> picColor=new ArrayList<>();
	
	private static List<String> secondWords=new ArrayList<>();
			
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
//	public synchronized static ConcurrentMap<String, Integer> getDownloadedMap() {
//		return downloadedMap;
//	}
//	public synchronized static void setDownloadedMap(ConcurrentMap<String, Integer> downloadedMap) {
//		ApplicationProperties.downloadedMap = downloadedMap;
//	}
	public static String getLogFileSavePath() {
		return logFileSavePath;
	}
	public static void setLogFileSavePath(String logFileSavePath) {
		ApplicationProperties.logFileSavePath = logFileSavePath;
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
	public static List<String> getPicSize() {
		return picSize;
	}
	public static void setPicSize(List<String> picSize) {
		ApplicationProperties.picSize = picSize;
	}
	public static List<String> getPicColor() {
		return picColor;
	}
	public static void setPicColor(List<String> picColor) {
		ApplicationProperties.picColor = picColor;
	}
	public static String[] getSpecified() {
		return specified;
	}
	public static void setSpecified(String[] specified) {
		ApplicationProperties.specified = specified;
	}
	public static List<String> getSecondWords() {
		return secondWords;
	}
	public static void setSecondWords(List<String> secondWords) {
		ApplicationProperties.secondWords = secondWords;
	}
//	public static List<String> getWait2write() {
//		return wait2write;
//	}
//	public static void setWait2write(List<String> wait2write) {
//		ApplicationProperties.wait2write = wait2write;
//	}
	public static BlockingQueue<String> getWait2downloadqueue() {
		return wait2downloadqueue;
	}
	public static void setWait2downloadqueue(BlockingQueue<String> wait2downloadqueue) {
		ApplicationProperties.wait2downloadqueue = wait2downloadqueue;
	}
	
	
}
