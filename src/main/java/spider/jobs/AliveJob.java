package spider.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AliveJob {
		
	@Scheduled(fixedDelay=10*60 * 1000)
	public void downloadJob(){
		System.out.println("还活着....");
	}
}
