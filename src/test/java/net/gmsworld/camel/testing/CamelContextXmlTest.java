package net.gmsworld.camel.testing;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelContextXmlTest extends CamelSpringTestSupport {

	// TODO Create test message bodies that work for the route(s) being tested
	// Expected message bodies
	protected Object[] expectedBodies = {
			"1" };
	// Templates to send to input endpoints
	
	@EndpointInject(uri = "mock:output")
	protected MockEndpoint outputEndpoint;
	
	
	//@Produce(uri = "direct:putToCacheMulti")
	//protected ProducerTemplate inputEndpoint;
	
	//@Produce(uri = "direct:getFromCacheUnlimited")
	//protected ProducerTemplate input2Endpoint;
	
	@Produce(uri = "direct:getFromCacheCount")
	protected ProducerTemplate input3Endpoint;
	
	//@Produce(uri = "direct:getByQueryFromCacheUnlimited")
	//protected ProducerTemplate input4Endpoint;
	
	//@Produce(uri = "direct:getByIdFromCache")
	//protected ProducerTemplate input5Endpoint;
	
	//@Produce(uri = "direct:getNearbyFromCache")
	//protected ProducerTemplate input6Endpoint;
	
	//@Produce(uri = "direct:getNearbyFromCacheCount")
	//protected ProducerTemplate input7Endpoint;
	
	//@Produce(uri = "seda:getNearbyFromCache")
	//protected ProducerTemplate input8Endpoint;
	
	//@Produce(uri = "direct:deleteFromCache")
	//protected ProducerTemplate input9Endpoint;*/

	@Produce(uri = "direct:getCheapestNearbyFromCache")
	protected ProducerTemplate input10Endpoint;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	//@DirtiesContext
	@Test
	public void testCamelRoute() throws Exception {	
		// Define some expectations

		// For now, let's just wait for some messages// TODO Add some expectations here
		// Send some messages to input endpoints
		//for (Object expectedBody : expectedBodies) {	
		//}
		//outputEndpoint.expectedBodiesReceivedInAnyOrder(expectedBodies);
		//input3Endpoint.sendBodyAndHeader(expectedBodies, "cid", "test");
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("distance", "10000");
		headers.put("limit", "5");
		headers.put("cid", "hotels");
		headers.put("lat", "52.25");
		headers.put("lng", "20.95");
			
		String response = template.requestBodyAndHeaders("direct:getCheapestNearbyFromCache", null, headers, String.class);
	
		System.out.println(response);
		
		headers.put("cid", "test");
		response = template.requestBodyAndHeaders("direct:getFromCacheCount", null, headers, String.class);
		
		// Validate our expectations
		assertEquals("1", response);
		
		//assertMockEndpointsSatisfied();
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		
		return new ClassPathXmlApplicationContext("/camelContext.xml");
	}

}
