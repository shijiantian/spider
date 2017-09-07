package serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import service.Calculator;

public class ExecutorServiceCalculator implements Calculator {

    private int parallism;
    private ExecutorService pool;

    public ExecutorServiceCalculator() {
        parallism = Runtime.getRuntime().availableProcessors(); // CPU�ĺ�����
        pool = Executors.newFixedThreadPool(parallism);
    }

    private static class SumTask implements Callable<Long> {
        private long[] numbers;
        private int from;
        private int to;

        public SumTask(long[] numbers, int from, int to) {
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        @Override
        public Long call() throws Exception {
            long total = 0;
            for (int i = from; i <= to; i++) {
                total += numbers[i];
            }
            return total;
        }
    }

    @Override
    public long sum(long[] numbers) {
        List<Future<Long>> results = new ArrayList<>();

        // ������ֽ�Ϊ n �ݣ����� n ���̴߳���
        int part = numbers.length / parallism;
        for (int i = 0; i < parallism; i++) {
            int from = i * part;
            int to = (i == parallism - 1) ? numbers.length - 1 : (i + 1) * part - 1;
            results.add(pool.submit(new SumTask(numbers, from, to)));
        }

        // ��ÿ���̵߳Ľ����ӣ��õ����ս��
        long total = 0L;
        for (Future<Long> f : results) {
            try {
                total += f.get();
            } catch (Exception ignore) {}
        }

        return total;
    }

}
