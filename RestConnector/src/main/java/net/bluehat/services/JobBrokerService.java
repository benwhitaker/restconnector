package net.bluehat.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.springframework.stereotype.Service;
import net.bluehat.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Robert Whitaker
 */
@Singleton
@Service("JobBroker")
public class JobBrokerService {
	private Logger logger = LoggerFactory.getLogger(JobBrokerService.class);
		
	@Autowired
	@Qualifier("JobRunner")
	private JobRunner jobrunner;
	
	private BlockingQueue<Job> jobQueue;
	
	@PostConstruct
	private void init() {
		jobQueue = new LinkedBlockingQueue<>();
		jobrunner.setupRunner(jobQueue);
		jobrunner.startRunner();
	}
	
	public boolean submitJob(Job j) {
		return this.jobQueue.offer(j);
	}
	
	public List<Job> getPendingJobs() {
		Job[] a = null;
		if (!this.jobQueue.isEmpty()) {
			a = new Job[this.jobQueue.size()];
			this.jobQueue.toArray(a);
		}
		if ( a != null) {
			return new ArrayList<>(Arrays.asList(a));
		} else {
			List b = new LinkedList<>();
			Job ab = new Job();
			b.add(ab);
			return b;
		}
	}
	public String testJobList() {
		try {
			List b = this.getPendingJobs();
			if (!b.isEmpty()) {
				return "Need to Serialize";
			} else {
			return "No Pending jobs!";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "No Pending jobs!";
		}
	}
}
