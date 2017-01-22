package net.bluehat.services;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.bluehat.model.Job;
import net.bluehat.services.model.PostDataRequest;
import net.bluehat.services.model.PostDataResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Robert Whitaker
 */
@Singleton
@Service("JobRunner")
public class JobRunner {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(JobRunner.class);
	
	private ExecutorService executor;
	
	//TODO Setup properties to control numeber of executors
	private static int numThreads=5;
	private static boolean isShutdown=false;
	private static Thread masterThread;
	private static BlockingQueue<Job> jQueue;
	
	@PostConstruct
	public void init() {
		logger.info("Starting Executor ThreadPool");
		this.executor = Executors.newFixedThreadPool(numThreads);
	}
	
	public void setupRunner(BlockingQueue<Job> jobQueue) {
		jQueue = jobQueue;
	}
	private class JobRunable implements Runnable {
	
		private String callbackURI;
		//TODO CHANGE output callback to be configurable
		private String dataURI = "http://localhost:5000";
		private String dataURIPath = "/";
		private PostDataRequest pdata;
		
		public JobRunable(String _callbackURI, int _a, int _b) {
			callbackURI = _callbackURI;
			pdata = new PostDataRequest();
			pdata.a = _a;
			pdata.b = _b;
			
		}
		
		public void run() {
			try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(dataURI).path(dataURIPath);
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(this.pdata));
			if (response.getStatus() == 200) {
				logger.debug("Success calling!");
				PostDataResponse res = response.readEntity(PostDataResponse.class);
				logger.debug("Response" + Integer.toString(res.Answer));
				WebTarget callbackTarget = client.target(callbackURI);
				logger.debug("Sending callback to  " + callbackURI);
				Response callbackResponse = callbackTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(res));
				if (callbackResponse.getStatus() == 200) {
					logger.debug("Success!");
				}
				
			}
			} catch (Exception e) {
				logger.error("Error running Job", e);
			}
		}
	}
	
	private class JobMaster implements Runnable {
		public void run() {
			while (! isShutdown) {
				if ( jQueue != null ){
					try {
						Job myjob = jQueue.take();
						JobRunable jrun = new JobRunable(myjob.callbackURI, myjob.numberA, myjob.numberB);
						executor.submit(jrun);
						
					} catch (InterruptedException e) {
						logger.error("Error Master Interrupted", e);
					} catch (Exception e) {
						logger.error("Error occured running master", e);
					}					
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}
	}
	
	public void startRunner() {
		masterThread = new Thread(new JobMaster());
		logger.info("Starting master thread!");
		masterThread.start();
	}

}
