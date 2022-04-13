/**
 * File: BloodDonationService.java for CST8277 Assignment 4
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
package bloodbank.ejb;

import static bloodbank.utility.MyConstants.PU_NAME;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;

@SuppressWarnings("unused")

/**
 * Stateless Singleton ejb Bean - BloodDonationService
 */
@Singleton
public class BloodDonationService implements Serializable {
	private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<BloodDonation> getAllBloodDonations() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BloodDonation> cq = cb.createQuery(BloodDonation.class);
        cq.select(cq.from(BloodDonation.class));
        return em.createQuery(cq).getResultList();
    }
    

    public BloodDonation getBloodDonationById(int id) {
		return em.find(BloodDonation.class, id);
    }

    @Transactional
    public BloodDonation persistBloodDonation(BloodDonation newBloodDonation) {
    	BloodBank bloodBank = em.find(BloodBank.class, newBloodDonation.getBank().getId());
		newBloodDonation.setBank(bloodBank);
        em.persist(newBloodDonation);
        return newBloodDonation;
    }

    /**
     * to update a blood donation
     * 
     * @param id - id of entity to update
     * @param donationWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public BloodDonation updateBloodDonationById(int id, BloodDonation donationWithUpdates) {
    	BloodDonation donationToBeUpdated = getBloodDonationById(id);
        if (donationToBeUpdated != null) {
            em.refresh(donationToBeUpdated);
            donationToBeUpdated.setMilliliters(donationWithUpdates.getMilliliters());
            donationToBeUpdated.setBank(donationWithUpdates.getBank());
            donationToBeUpdated.setBloodType(donationWithUpdates.getBloodType());
            em.merge(donationToBeUpdated);
            em.flush();
        }
        return donationToBeUpdated;
    }

    /**
     * to delete a blood donation by id
     * 
     * @param id - blood donation id to delete
     */
    @Transactional
    public void deleteBloodDonationById(int id) {
    	BloodDonation donationToBeDeleted = getBloodDonationById(id);
		if (donationToBeDeleted != null) {
			em.refresh(donationToBeDeleted);
			em.remove(donationToBeDeleted);
		}
    }
}
