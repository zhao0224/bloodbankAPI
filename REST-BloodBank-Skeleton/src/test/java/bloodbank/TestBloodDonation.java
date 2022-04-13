/**
 * File: TestBloodDonation.java for CST8277 Assignment 4
 * @author Juan Ni
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
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER;
import static bloodbank.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.BLOODDONATION_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.BLOODBANK_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.BloodType;
import bloodbank.entity.PublicBloodBank;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBloodDonation {
	 private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	 private static final Logger logger = LogManager.getLogger(_thisClaz);

	 static final String HTTP_SCHEMA = "http";
	 static final String HOST = "localhost";
	    static final int PORT = 8080;

	    // test fixture(s)
	    static URI uri;
	    static HttpAuthenticationFeature adminAuth;
	    static HttpAuthenticationFeature userAuth;
	    
	    static BloodDonation bloodDonation;
	    static BloodBank publicBloodBank;
		static BloodType bloodType;

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
	        
	        publicBloodBank = new PublicBloodBank();
			publicBloodBank.setName("Test Public Bank");
			
			bloodType = new BloodType();
			bloodType.setType("A", "+");
			
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
	    public void test01_all_blood_donations_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget.register(adminAuth).path(BLOODDONATION_RESOURCE_NAME).request().get();
			assertThat(response.getStatus(), is(200));
			List<BloodDonation> donations = response.readEntity(new GenericType<List<BloodDonation>>() {
			});
			assertThat(donations, is(not(empty())));
			assertThat(donations, hasSize(2));
	    }
	    
	    @Test
	    @Order(2)
	    public void test02_all_blood_donations_with_userrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget.register(userAuth).path(BLOODDONATION_RESOURCE_NAME).request().get();
			assertThat(response.getStatus(), is(200));
			List<BloodDonation> donations = response.readEntity(new GenericType<List<BloodDonation>>() {
			});
			assertThat(donations, is(not(empty())));
			assertThat(donations, hasSize(2));
	    }
	    
	    @Test
	    @Order(3)
	    public void test03_get_blood_donation_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
	        Response response = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME + "/1")
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        BloodDonation donation = response.readEntity(BloodDonation.class);
	        assertEquals(donation.getId(), 1);
	        assertEquals(donation.getBank().getId(), 2);
	        assertEquals(donation.getMilliliters(), 10);
	        assertEquals(donation.getBloodType().getBloodGroup(), "B");
	        assertEquals(donation.getBloodType().getRhd(), 0);
	    }
	    
	    @Test
	    @Order(4)
	    public void test04_get_blood_donation_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
	        Response response = webTarget
	            .register(userAuth)
	            .path(BLOODDONATION_RESOURCE_NAME + "/1")
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        BloodDonation donation = response.readEntity(BloodDonation.class);
	        assertEquals(donation.getId(), 1);
	        assertEquals(donation.getBank().getId(), 2);
	        assertEquals(donation.getMilliliters(), 10);
	        assertEquals(donation.getBloodType().getBloodGroup(), "B");
	        assertEquals(donation.getBloodType().getRhd(), 0);
	    }
	    
	    @Test
	    @Order(5)
	    public void test05_add_invalid_blood_donation_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	BloodDonation donationInvalid = new BloodDonation();
			donationInvalid.setBloodType(bloodType);
			donationInvalid.setMilliliters(1);
			donationInvalid.setBank(null);
			// we expect a failure because bank cannot be null
			Response getResponse = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME)
	            .request()
	            .post(Entity.json(donationInvalid));
	        assertThat(getResponse.getStatus(), is(500));
	    }
	    

	    @Test
	    @Order(6)
	    public void test06_add_blood_donation_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	// Get all blood donation list after adding the new blood donation
	    	Response getResponse = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(getResponse.getStatus(), is(200));
	        List<BloodDonation> donationsBeforeAdd = getResponse.readEntity(new GenericType<List<BloodDonation>>() {
			});
	        
	    	// Get blood bank from database
	    	Response response = webTarget
	            .register(adminAuth)
	            .path(BLOODBANK_RESOURCE_NAME + "/1")
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
		    BloodBank bank = response.readEntity(BloodBank.class);
	    	
		    // Create a new blood donation 
	    	bloodDonation = new BloodDonation();
			bloodDonation.setBank(bank);
			bloodDonation.setBloodType(bloodType);
			bloodDonation.setMilliliters(10);
			
			Response postResponse = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME)
	            .request()
	            .post(Entity.json(bloodDonation));
	        assertThat(postResponse.getStatus(), is(200));
	        BloodDonation newBloodDonation = postResponse.readEntity(BloodDonation.class);
	    	
	        Response getResponse2 = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(getResponse2.getStatus(), is(200));
	        List<BloodDonation> donationsAfterAdd = getResponse2.readEntity(new GenericType<List<BloodDonation>>() {
			});
	     
	        assertEquals(donationsBeforeAdd.size() + 1, donationsAfterAdd.size());
	        
	        Response deleteResponse = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME + "/" + newBloodDonation.getId())
	            .request()
	            .delete();
	        assertThat(deleteResponse.getStatus(), is(200));
	    }
	    
	    
	    @Test
	    @Order(7)
	    public void test07_update_blood_donation_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME + "/2")
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        BloodDonation donationToBeUpdated = response.readEntity(BloodDonation.class);
	        
	        BloodType newBloodType = new BloodType();
	        newBloodType.setType("AB", "+");
	        donationToBeUpdated.setBloodType(newBloodType);
	        donationToBeUpdated.setMilliliters(11);
	        
	        Response updateResponse = webTarget
	        		.register(adminAuth)
	        		.path(BLOODDONATION_RESOURCE_NAME + "/2")
	        		.request()
	        		.put(Entity.json(donationToBeUpdated));
	        assertThat(updateResponse.getStatus(), is(200));
	        BloodDonation donationUpdated = updateResponse.readEntity(BloodDonation.class);
	        assertEquals(donationUpdated.getId(), 2);
	        assertEquals(donationUpdated.getBank().getId(), 2);
	        assertEquals(donationUpdated.getMilliliters(), 11);
	        assertEquals(donationUpdated.getBloodType().getBloodGroup(), "AB");
	        assertEquals(donationUpdated.getBloodType().getRhd(), 1);
	    }	
	    
	    @Test
	    @Order(9)
	    public void test09_delete_blood_donation_dependency_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME)
	            .request()
	            .get();
			assertThat(response.getStatus(), is(200));
			List<BloodDonation > bloodDonations = response.readEntity(new GenericType<List<BloodDonation >>() {
			});

			BloodDonation donation = bloodDonations.get(bloodDonations.size()-1);
			int tempId = donation.getId();
 	        donation.setBank(null);
 	        
 	        response = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME + "/" + tempId)
	            .request()
	            .put(Entity.json(donation));
 	       // bank cannot be null so exceptions will be thrown
	        assertThat(response.getStatus(), is(500));

	    }
	    
	    @Test
	    @Order(10)
	    public void test10_delete_blood_donation_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget
	 	            .register(adminAuth)
	 	            .path(BLOODDONATION_RESOURCE_NAME + "/2")
	 	            .request()
	 	            .get();
	 	        assertThat(response.getStatus(), is(200));
	 	        BloodDonation donation = response.readEntity(BloodDonation.class);
	 	        donation.setBank(null);
	 	       response = webTarget
		            .register(adminAuth)
		            .path(BLOODDONATION_RESOURCE_NAME + "/2")
		            .request()
		            .put(Entity.json(donation));
	 	       // bank cannot be null so exceptions will be thrown
		        assertThat(response.getStatus(), is(500));
	    }
}
