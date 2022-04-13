/**
 * File: BloodDonationResource.java for CST8277 Assignment 4
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
package bloodbank.rest.resource;

import static bloodbank.utility.MyConstants.ADMIN_ROLE;
import static bloodbank.utility.MyConstants.BLOODDONATION_RESOURCE_NAME;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static bloodbank.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static bloodbank.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.ejb.BloodDonationService;
import bloodbank.entity.BloodDonation;


@Path(BLOODDONATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BloodDonationResource {
	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected BloodDonationService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getBloodDonations() {
        LOG.debug("retrieving all blood donations ...");
        List<BloodDonation> bloodDonations = service.getAllBloodDonations();
        Response response = Response.ok(bloodDonations).build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getBloodDonationById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific blood donation " + id);
        BloodDonation bloodDonation = service.getBloodDonationById(id);
        Response response = Response.ok(bloodDonation).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addBloodDonation(BloodDonation newDonation) {
    	LOG.debug("Adding a new blood donation = {}", newDonation);
        BloodDonation newDonationWithIdTimestamps = service.persistBloodDonation(newDonation);
        Response response = Response.ok(newDonationWithIdTimestamps).build();
        return response;
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateBloodDonation(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, BloodDonation updatingBloodDonation) {
        LOG.debug("Updating a specific blood donation with id = {}", id);
        BloodDonation updatedBloodDonation = service.updateBloodDonationById(id, updatingBloodDonation);
        Response response = Response.ok(updatedBloodDonation).build();
        return response;
    }
    
	@DELETE
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteBloodDonation(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("Deleting a specific blood donation with id = {}", id);
		service.deleteBloodDonationById(id);
		Response response = Response.ok().build();
		return response;
	}
    
}
