package net.bluehat.rest;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.bluehat.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.bluehat.services.JobBrokerService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Robert Whitaker
 */

@Path("job")
@Component
public class JobRestService {
	private Logger logger = LoggerFactory.getLogger(JobRestService.class);
	
	@Autowired
	@Qualifier("JobBroker")
	private JobBrokerService jServ;
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public String jobList() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this.jServ.getPendingJobs());
	}
	
	
	//TODO: Update this to handle error and provide feedback
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public String putJob(Job j) throws IOException {
		if (this.jServ.submitJob(j)) {
			return "\"status\": { \"Success\"}";
		} else {
			return "\"status\": { \"Failure\"}";
		}
	}
	public JobBrokerService getjServ() {
		return jServ;
	}

	public void setjServ(JobBrokerService jServ) {
		this.jServ = jServ;
	}
	
}
