/**
 * File: TestDonationRecord.java for CST8277 Assignment 4
 * @author Jing Zhao
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
import static bloodbank.utility.MyConstants.PERSON_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.DONATION_RECORD_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.BLOODDONATION_RESOURCE_NAME;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.print.Printable;
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
import org.hamcrest.collection.IsEmptyCollection;
import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.BloodType;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.PrivateBloodBank;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestDonationRecord {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;
    
    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    
    private static DonationRecord record;
    private static Person person;
	private static BloodDonation donation;
	private static BloodType bloodType;
	private static final boolean tested = true;


	
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
       
   }

    protected WebTarget webTarget;
    protected WebTarget newWebTarget;
    
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
        
        newWebTarget = client.target(uri);
    }
    
	
    @Test
    @Order(1)
    public void test01_all_donation_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(DONATION_RECORD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<DonationRecord> records = response.readEntity(new GenericType<List<DonationRecord>>(){});
        assertThat(records, is(not(empty())));


    }
    
    @Test
    @Order(2)
    public void test2_all_donation_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(DONATION_RECORD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));

    }
  

	@Test
	@Order(3)
	public void test03_get_donation_record_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME +"/1")
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        DonationRecord record = response.readEntity(DonationRecord.class);
	        assertEquals(record.getOwner().getId(), 1);
	        assertEquals(record.getDonation().getId(), 1);
	}
	
	@Test
	@Order(4)
	public void test04_get_donation_record_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            .register(userAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME +"/1")
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(200));
	        DonationRecord record = response.readEntity(DonationRecord.class);
	        assertEquals(record.getOwner().getId(), 1);
	        assertEquals(record.getDonation().getId(), 1);
	}
	

	@Test
	@Order(5)
	public void test05_add_donation_record_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            //.register(userAuth)
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME)
	            .request()
	            .get();
		assertThat(response.getStatus(), is(200));
		List<DonationRecord> recordsBeforeAdd = response.readEntity(new GenericType<List<DonationRecord>>() {
		});
		
		Response responsePerson = webTarget
	            .register(adminAuth)
	            .path(PERSON_RESOURCE_NAME+ "/1")
	            .request()
	            .get();
		Person person1 = responsePerson.readEntity(Person.class);

		
		Response responseDonation = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME+ "/1")
	            .request()
	            .get();
		
	    BloodDonation donation1 = responseDonation.readEntity(BloodDonation.class);


		DonationRecord newRecord = new DonationRecord();
		newRecord.setOwner(person1);
		newRecord.setTested(tested);
		newRecord.setDonation(donation1);
				
		
		Response response1 = webTarget
				.register(adminAuth)
				.path(DONATION_RECORD_RESOURCE_NAME)
				.request()
				.post(Entity.json(newRecord));
		
		assertThat(response1.getStatus(), is(200));
		
		Response response2 = webTarget
				.register(adminAuth)
				.path(DONATION_RECORD_RESOURCE_NAME)
				.request()
				.get();
		
		assertThat(response2.getStatus(), is(200));
		List<DonationRecord> recordsAfterAdd = response2.readEntity(new GenericType<List<DonationRecord>>() {
		});

		assertEquals(recordsBeforeAdd.size()+ 1, recordsAfterAdd.size());
		
	}
		
	@Test
	@Order(6)
	public void test06_update_donation_record_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response1 = webTarget
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME)
	            .request()
	            .get();
		assertThat(response1.getStatus(), is(200));
		List<DonationRecord> recordsBeforeUpdate = response1.readEntity(new GenericType<List<DonationRecord>>() {
		});
		
		DonationRecord donationRecordToUpdate =  recordsBeforeUpdate.get(recordsBeforeUpdate.size()-1);
		int tempId = donationRecordToUpdate.getId();
		donationRecordToUpdate.setTested(!(tested));
	
		
	    Response response2 = webTarget
				.register(adminAuth)
				.path(DONATION_RECORD_RESOURCE_NAME+"/"+tempId)
				.request()
				.put(Entity.json(donationRecordToUpdate));

		assertThat(response2.getStatus(), is(200));
	
		Response response3 = webTarget
				.register(adminAuth)
				.path(DONATION_RECORD_RESOURCE_NAME + "/"+tempId)
				.request()
				.get();
		assertThat(response3.getStatus(), is(200));
		
		DonationRecord recordsAfterUpdate = response3.readEntity(DonationRecord.class);
		
		assertEquals(recordsAfterUpdate.getTested(), 0);
		
	}
	
	@Test
	@Order(7)
	public void test07_update_donation_record_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME)
	            .request()
	            .get();
		assertThat(response.getStatus(), is(200));
		List<DonationRecord> recordsBeforeUpdate = response.readEntity(new GenericType<List<DonationRecord>>() {
		});
		
	    DonationRecord donationRecordToUpdate =  recordsBeforeUpdate.get(recordsBeforeUpdate.size()-1);
		int tempId = donationRecordToUpdate.getId();

		donationRecordToUpdate.setTested(tested);
		
	    Response response1 = newWebTarget
				.register(userAuth)
				.path(DONATION_RECORD_RESOURCE_NAME+"/"+tempId)
				.request()
				.put(Entity.json(donationRecordToUpdate));
		assertThat(response1.getStatus(), is(403));
	
	}
	
	@Test
	@Order(8)
	public void test08_update_Dependency_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            //.register(userAuth)
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME)
	            .request()
	            .get();
		assertThat(response.getStatus(), is(200));
		List<DonationRecord> recordsBeforeUpdate = response.readEntity(new GenericType<List<DonationRecord>>() {
		});
		
	    DonationRecord donationRecordToUpdate =  recordsBeforeUpdate.get(recordsBeforeUpdate.size()-1);
		int tempId = donationRecordToUpdate.getId();	
		int tempDonationID = donationRecordToUpdate.getDonation().getId();
		
		Response responseDonation = webTarget
	            .register(adminAuth)
	            .path(BLOODDONATION_RESOURCE_NAME + "/"+(tempDonationID+1) )  //next donationID
	            .request()
	            .get();
		BloodDonation donation1 = responseDonation.readEntity(BloodDonation.class);

		donationRecordToUpdate.setDonation(donation1);
		
		Response response2 = webTarget
				.register(adminAuth)
				.path(DONATION_RECORD_RESOURCE_NAME+"/"+tempId)
				.request()
				.put(Entity.json(donationRecordToUpdate));

		assertThat(response2.getStatus(), is(200));
	
		Response response3 = webTarget
				.register(adminAuth)
				.path(DONATION_RECORD_RESOURCE_NAME + "/"+tempId)
				.request()
				.get();
		assertThat(response3.getStatus(), is(200));
		
		DonationRecord recordsAfterUpdate = response3.readEntity(DonationRecord.class);
		
		assertEquals(recordsAfterUpdate.getDonation().getId(), (tempDonationID+1));
	}
	
	
	@Test
	@Order(9)
	public void test09_delete_donation_record_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME)
	            .request()
	            .get();
		
		assertThat(response.getStatus(), is(200));

		List<DonationRecord> recordsBeforeDelete = response.readEntity(new GenericType<List<DonationRecord>>() {
		});
	     
		DonationRecord recordTobeDelete = recordsBeforeDelete.get(recordsBeforeDelete.size()-1);
		int tempId = recordTobeDelete.getId();

		Response response1 = webTarget
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME+"/" +tempId)
	            .request()
	            .delete();
		assertThat(response1.getStatus(), is(200));
		
	}
	
	@Test
	@Order(10)
	public void test10_delete_donation_record_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
	            .register(adminAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME)
	            .request()
	            .get();
		
		assertThat(response.getStatus(), is(200));

		List<DonationRecord> recordsBeforeDelete = response.readEntity(new GenericType<List<DonationRecord>>() {
		});
	     
		DonationRecord recordTobeDelete = recordsBeforeDelete.get(recordsBeforeDelete.size()-1);
		int tempId = recordTobeDelete.getId();
		
		Response response1 = newWebTarget
	            .register(userAuth)
	            .path(DONATION_RECORD_RESOURCE_NAME+"/" + tempId)
	            .request()
	            .delete();
		assertThat(response1.getStatus(), is(403));
	}

}
