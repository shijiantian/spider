package utils;

import java.util.concurrent.RecursiveTask;

public class CalculateSumTask extends RecursiveTask<Integer> {
	
	private int from;
	private int to;
	private int[] intarray;
	
	public CalculateSumTask(int[] intarray,int from,int to) {
		this.from=from;
		this.to=to;
		this.intarray=intarray;
	}

	@Override
	protected Integer compute() {
		if(to-from<6){
			Integer result=0;
			for(int i=from;i<=to;i++){
				result+=intarray[i];
			}
			return result;
		}else{
			int middle = (from + to) / 2;
			CalculateSumTask task1=new CalculateSumTask(intarray, from, middle);
			CalculateSumTask task2=new CalculateSumTask(intarray, middle+1, to);
			task1.fork();
			task2.fork();
			return task1.join()+task2.join();
		}
		
	}

}
