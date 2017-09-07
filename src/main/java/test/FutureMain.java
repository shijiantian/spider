package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.CallableTask;
import utils.Task;

public class FutureMain {
	
	public static void main(String[] args){
		Runnable task=()->{
			String threadName=Thread.currentThread().getName();
			System.out.println("hello:"+threadName);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("wake up");
		};
//		task.run();
		Thread thread=new Thread(task);
		thread.start();
		System.out.println("Done");
		Task task2=new Task();
		Thread thread2=new Thread(task2);
		thread2.start();
		
		ExecutorService pool=Executors.newSingleThreadExecutor();
		pool.submit(()->{
			System.out.println("pool:"+Thread.currentThread().getName());
		});
		
		Callable<Integer> callable=()->{
			return 111;
		};
		Future<Integer> future=pool.submit(callable);
		System.out.println("future is done:"+future.isDone());
		Integer integer=0;
		try {
			integer=future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("future is done:"+future.isDone());
		System.out.println(integer);
		
		Future<Integer> future2=pool.submit(()->{
			TimeUnit.SECONDS.sleep(1);
			return 124;
		});
		try {
			System.out.println(future2.get(2, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		ExecutorService pool2=Executors.newWorkStealingPool();
		List<CallableTask> tasks=new ArrayList<>();
		tasks.add(new CallableTask(1245));
		tasks.add(new CallableTask(1235));
		try {
			List<Future<Integer>> futures=pool2.invokeAll(tasks);
			futures.stream().map(vo->{
				int i = 0;
				try {
					i=vo.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				return i;
			}).forEach(va->{
				System.out.println(va);
			});
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<CallableTask> tasks2=Arrays.asList(new CallableTask(1289),new CallableTask(1209));
		
		try {
			Integer results=pool2.invokeAny(tasks2);
			System.out.println(results);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		Runnable tasktt = () -> System.out.println("Scheduling: " + System.nanoTime());

		int initialDelay = 0;
		int period = 1;
		executor.scheduleAtFixedRate(tasktt, initialDelay, period, TimeUnit.SECONDS);
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		executor.shutdown();
		try {
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
