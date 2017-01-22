package net.bluehat;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Robert Whitaker
 */

@Component
@ApplicationPath("/")
public class ConnectorProvider extends ResourceConfig{
	private Logger logger = LoggerFactory.getLogger(ConnectorProvider.class);

	public ConnectorProvider() {
		logger.info("Connector Provider Started");
		packages("net.bluehat.rest");
		register(JacksonFeature.class);
	}
}
