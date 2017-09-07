package test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import utils.ConcurrentUtils;

public class LockMain {
	
	int i=0;
	
	ReentrantLock reentrantLock=new ReentrantLock();
	
	public void increment(){
		reentrantLock.lock();
		try {
			this.i++;
		} finally {
			reentrantLock.unlock();
		}
		
		
	}
	
	public static void main(String [] args) {
		ExecutorService service=Executors.newFixedThreadPool(3);
		LockMain lockMain=new LockMain();
		IntStream.range(0, 10000).forEach(i->service.submit(()->lockMain.increment()));
		ConcurrentUtils.stop(service);
//		System.out.println(lockMain.i);

		ForkJoinPool pool=new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		List<Integer> integers=new ArrayList<>();
		for(int i=0;i<10;i++){
			integers.add(i);
		}
		List<ForkJoinTask<Integer>> tasks=new ArrayList<>();
		for(final Integer i:integers){
			ForkJoinTask<Integer> forkJoinTask=pool.submit(()->{
				return i;
			});
			tasks.add(forkJoinTask);
		}
		for(ForkJoinTask<Integer> task:tasks){
			System.out.println(task.join());
		}
		
	}
}
