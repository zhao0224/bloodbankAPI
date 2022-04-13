/**
 * File: TestAddress.java for CST8277 Assignment 4
 * @author Feiqong Deng
 * 
 * CST8277 Assignment 4 Group - 5
 * Team Members(order by last name)
 * 040991296, Feiqiong Deng
 * 040911749, Juan Ni 
 * 040991653, Sophie Sun
 * 040994750, Jing Zhao
 * 
 */
package bloodbank;

import static bloodbank.utility.MyConstants.APPLICATION_API_VERSION;
import static bloodbank.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static bloodbank.utility.MyConstants.CUSTOMER_ADDRESS_SUBRESOURCE_NAME;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bloodbank.entity.Address;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestAddress {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    
    private final int ID = 1;
    private static Address addressSample;
	private static final String STREETNUM = "123";
	private static final String ST = "cde ln";
	private static final String CITY = "toronto";
	private static final String PROVINCE = "ON";
	private static final String COUNTRY = "CA";
	private static final String ZIPCODE = "A2A9R4";
	private static int sampleId;
	
    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
        
        addressSample = new Address();
        addressSample.setAddress(STREETNUM, ST, CITY, PROVINCE, COUNTRY, ZIPCODE);
	
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }
    
    @Test
	@Order(1)
    public void test01_all_addresses_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Address> addresses = response.readEntity(new GenericType<List<Address>>(){});
        assertThat(addresses, is(not(empty())));
        assertThat(addresses, hasSize(1));
    }
    
    @Test
	@Order(2)
    public void test02_addressById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + ID)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Address address = response.readEntity(new GenericType<Address>(){});
        assertThat(address, notNullValue());
        assertThat(address.getId(), equalTo(ID));
    }
    
    @Test
	@Order(3)
    public void test03_addressById_with_userRole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + ID)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(200));
        Address address = response.readEntity(new GenericType<Address>(){});
        assertThat(address, notNullValue());
        assertThat(address.getId(), equalTo(ID));
    }
    
   @Test
   @Order(4)
   public void test04_addAddress_with_adminrole() throws JsonMappingException, JsonProcessingException {
   		Entity<Address> add = Entity.json(addressSample);
   	
   		Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .post(add);
        assertThat(response.getStatus(), is(200));
        
        Address address = response.readEntity(new GenericType<Address>(){});
        sampleId = address.getId();

        assertThat(address, notNullValue());
        assertThat(address.getStreetNumber(), equalTo(STREETNUM));
        assertThat(address.getStreet(), equalTo(ST));
        assertThat(address.getCity(), equalTo(CITY));
        assertThat(address.getProvince(), equalTo(PROVINCE));
        assertThat(address.getCountry(), equalTo(COUNTRY));
        assertThat(address.getZipcode(), equalTo(ZIPCODE));
    }
   
   @Test
   @Order(5)
   public void test05_addInvalidAddress_with_adminrole() throws JsonMappingException, JsonProcessingException {
   		Address invalid = new Address();
   		invalid.setAddress(null, STREETNUM, CITY, PROVINCE, COUNTRY, ZIPCODE);
	   
	   Entity<Address> addInvalid = Entity.json(invalid);
   	
   		Response response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .post(addInvalid);
   		
        assertThat(response.getStatus(), is(500));
    }
      
   @Test
   @Order(6)
   public void test06_updateAddress_with_adminrole() throws JsonMappingException, JsonProcessingException {
	   Response response = webTarget
	            .register(adminAuth)
	            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + sampleId)
	            .request()
	            .get();
	   
	    Address address = response.readEntity(new GenericType<Address>(){});
	   
	    String newStreetNum = "666";
		String newStreet = "Hello Rd";
		String newCity = "boston";
		String newProvinve = "MA";
		String newCountry = "US";
		String newZipcode = "MAB789";
		
		address.setAddress(newStreetNum, newStreet, newCity, newProvinve, newCountry, newZipcode);
		
   		Entity<Address> update = Entity.json(address);
   	
   		response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + sampleId)
            .request()
            .put(update);
   		
        assertThat(response.getStatus(), is(200));
        
        address = response.readEntity(new GenericType<Address>(){});
        int testId = address.getId();

        assertThat(testId,  equalTo(sampleId));
        assertThat(address.getStreetNumber(), equalTo(newStreetNum));
        assertThat(address.getStreet(), equalTo(newStreet));
        assertThat(address.getCity(), equalTo(newCity));
        assertThat(address.getProvince(), equalTo(newProvinve));
        assertThat(address.getCountry(), equalTo(newCountry));
        assertThat(address.getZipcode(), equalTo(newZipcode));
    }
   
   @Test
   @Order(7)
   public void test07_updateInvalidAddress_with_adminrole() throws JsonMappingException, JsonProcessingException {
	   Response response = webTarget
	            .register(adminAuth)
	            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + sampleId)
	            .request()
	            .get();
	   
	    Address address = response.readEntity(new GenericType<Address>(){});
	   
	    String newStreetNum = "666";
		String newStreet = null;
		String newCity = "boston";
		String newProvinve = "MA";
		String newCountry = "US";
		String newZipcode = "MAB789";
		
		address.setAddress(newStreetNum, newStreet, newCity, newProvinve, newCountry, newZipcode);
		
   		Entity<Address> update = Entity.json(address);
   	
   		response = webTarget
            .register(adminAuth)
            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + sampleId)
            .request()
            .put(update);
   		
        assertThat(response.getStatus(), is(500));
    }
   
    @Test
  	@Order(8)
    public void test08_deleteAddress_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	  Response response = webTarget
    	            .register(adminAuth)
    	            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
    	            .request()
    	            .get();
	        assertThat(response.getStatus(), is(200));
	        List<Address> addresses = response.readEntity(new GenericType<List<Address>>(){});
	        assertThat(addresses, is(not(empty())));
	        assertThat(addresses, hasSize(2));
    	
    	    response = webTarget
              .register(adminAuth)
              .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME + "/" + sampleId)
              .request()
              .delete();
          
    	    assertThat(response.getStatus(), is(200));
          
            response = webTarget
  	            .register(adminAuth)
  	            .path(CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
  	            .request()
  	            .get();
          
            addresses = response.readEntity(new GenericType<List<Address>>(){});
	        assertThat(addresses, hasSize(1));
      }
}
