/**
 * File: TestPhone.java for CST8277 Assignment 4
 * @author Sophie Sun
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
import static bloodbank.utility.MyConstants.PHONE_RESOURCE_NAME;
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

import bloodbank.entity.Phone;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestPhone {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    
//    `phone_id` INT NOT NULL AUTO_INCREMENT,
//    `country_code` VARCHAR(10) NOT NULL DEFAULT '1',
//    `area_code` VARCHAR(10) NOT NULL,
//    `number` VARCHAR(10) NOT NULL,
//    `created` DATETIME NULL,
//    `updated` DATETIME NULL,
//    `version` BIGINT NOT NULL DEFAULT 1,
//    PRIMARY KEY (`phone_id`)
    
    
    
    private final int ID = 1;
    private static Phone phoneSample;
	private static final String COUNTRY_CODE = "1";
	private static final String AREA_CODE = "613";
	private static final String NUMBER = "6139999";
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
        
        phoneSample = new Phone(AREA_CODE, COUNTRY_CODE, NUMBER);
	
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
    public void test01_all_phones_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PHONE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Phone> phones = response.readEntity(new GenericType<List<Phone>>(){});
        assertThat(phones, is(not(empty())));
        assertThat(phones, hasSize(2));
    }
    
    @Test
	@Order(2)
    public void test02_phoneById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PHONE_RESOURCE_NAME + "/" + ID)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Phone phone = response.readEntity(new GenericType<Phone>(){});
        assertThat(phone, notNullValue());
        assertThat(phone.getId(), equalTo(ID));
    }
    
    @Test
	@Order(3)
    public void test03_phoneById_with_userRole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PHONE_RESOURCE_NAME + "/" + ID)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(200));
        Phone phone = response.readEntity(new GenericType<Phone>(){});
        assertThat(phone, notNullValue());
        assertThat(phone.getId(), equalTo(ID));
    }
    
   @Test
   @Order(4)
   public void test04_addPhone_with_adminrole() throws JsonMappingException, JsonProcessingException {
   		Entity<Phone> add = Entity.json(phoneSample);
   	
   		Response response = webTarget
            .register(adminAuth)
            .path(PHONE_RESOURCE_NAME)
            .request()
            .post(add);
        assertThat(response.getStatus(), is(200));
        
        Phone phone = response.readEntity(new GenericType<Phone>(){});
        sampleId = phone.getId();

        assertThat(phone, notNullValue());
        assertThat(phone.getAreaCode(), equalTo(AREA_CODE));
        assertThat(phone.getCountryCode(), equalTo(COUNTRY_CODE));
        assertThat(phone.getNumber(), equalTo(NUMBER));
    }
   
   @Test
   @Order(5)
   public void test05_addInvalidPhone_with_adminrole() throws JsonMappingException, JsonProcessingException {
	   Phone invalid = new Phone();
   		invalid.setNumber(null, null, null);
	   
	   Entity<Phone> addInvalid = Entity.json(invalid);
   	
   		Response response = webTarget
            .register(adminAuth)
            .path(PHONE_RESOURCE_NAME)
            .request()
            .post(addInvalid);
   		
        assertThat(response.getStatus(), is(500));
    }
      
   @Test
   @Order(6)
   public void test06_updatePhone_with_adminrole() throws JsonMappingException, JsonProcessingException {
	   Response response = webTarget
	            .register(adminAuth)
	            .path(PHONE_RESOURCE_NAME + "/" + sampleId)
	            .request()
	            .get();
	   
	   Phone phone = response.readEntity(new GenericType<Phone>(){});
	   
	    String newCountryCode = "1";
		String newAreaCode = "514";
		String newNumber = "9616666";
		
		phone.setNumber(newCountryCode, newAreaCode, newNumber);
		
   		Entity<Phone> update = Entity.json(phone);
   	
   		response = webTarget
            .register(adminAuth)
            .path(PHONE_RESOURCE_NAME + "/" + sampleId)
            .request()
            .put(update);
   		
        assertThat(response.getStatus(), is(200));
        
        phone = response.readEntity(new GenericType<Phone>(){});
        int testId = phone.getId();

        assertThat(testId,  equalTo(sampleId));
        assertThat(phone.getAreaCode(), equalTo(newAreaCode));
        assertThat(phone.getCountryCode(), equalTo(newCountryCode));
        assertThat(phone.getNumber(), equalTo(newNumber));
    }
   
   @Test
   @Order(7)
   public void test07_updateInvalidPhone_with_adminrole() throws JsonMappingException, JsonProcessingException {
	   Response response = webTarget
	            .register(adminAuth)
	            .path(PHONE_RESOURCE_NAME + "/" + sampleId)
	            .request()
	            .get();
	   
	   Phone phone = response.readEntity(new GenericType<Phone>(){});
	   
	    String newCountryCode = null;
		String newAreaCode = "514";
		String newNumber = "9616666";
		
		phone.setNumber(newCountryCode, newAreaCode, newNumber);
		
   		Entity<Phone> update = Entity.json(phone);
   	
   		response = webTarget
            .register(adminAuth)
            .path(PHONE_RESOURCE_NAME + "/" + sampleId)
            .request()
            .put(update);
   		
        assertThat(response.getStatus(), is(500));
    }
   
    @Test
  	@Order(8)
    public void test08_deletePhone_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	  Response response = webTarget
    	            .register(adminAuth)
    	            .path(PHONE_RESOURCE_NAME)
    	            .request()
    	            .get();
	        assertThat(response.getStatus(), is(200));
	        List<Phone> phones = response.readEntity(new GenericType<List<Phone>>(){});
	        assertThat(phones, is(not(empty())));
	        assertThat(phones, hasSize(3));
    	
    	    response = webTarget
              .register(adminAuth)
              .path(PHONE_RESOURCE_NAME + "/" + sampleId)
              .request()
              .delete();
          
    	    assertThat(response.getStatus(), is(200));
          
            response = webTarget
  	            .register(adminAuth)
  	            .path(PHONE_RESOURCE_NAME)
  	            .request()
  	            .get();
          
            phones = response.readEntity(new GenericType<List<Phone>>(){});
	        assertThat(phones, hasSize(2));
      }
}
