package utils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CallableTask implements Callable<Integer> {
	Integer result;
	public CallableTask(Integer result) {
		this.result=result;
	}

	@Override
	public Integer call() throws Exception {
		TimeUnit.SECONDS.sleep(2);
		return result;
	}
	
}
