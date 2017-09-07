package serviceImpl;

import service.Calculator;

public class ForLoopCalculator implements Calculator {
	public long sum(long[] numbers) {
        long total = 0;
        for (long i : numbers) {
            total += i;
        }
        return total;
    }
}
