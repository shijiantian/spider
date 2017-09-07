package serviceImpl;

import java.util.concurrent.ForkJoinPool;

import service.Calculator;
import utils.SumTask;

public class ForkJoinCalculator implements Calculator {

	private ForkJoinPool pool;

    public ForkJoinCalculator() {
        // 也可以使用公用的 ForkJoinPool：
        // pool = ForkJoinPool.commonPool()
        pool = new ForkJoinPool();
    }

    @Override
    public long sum(long[] numbers) {
        return pool.invoke(new SumTask(numbers, 0, numbers.length-1));
    }

}
