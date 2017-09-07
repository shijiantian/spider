package utils;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable {
	@Override
	public void run() {
		String threadName=Thread.currentThread().getName();
		System.out.println("hello world:"+threadName);
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("wake up 2");
	}
}
