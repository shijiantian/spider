package test;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import utils.CalculateSumTask;

public class Main {
    public static void main(String[] args) {
    	int[] intarray=IntStream.rangeClosed(1, 10000).toArray();
    	ForkJoinPool pool=new ForkJoinPool(Runtime.getRuntime().availableProcessors()+1);
    	CalculateSumTask task1=new CalculateSumTask(intarray,1,10000-1);
    	Integer result=pool.invoke(task1);
		System.out.println(result);
		
    }
}