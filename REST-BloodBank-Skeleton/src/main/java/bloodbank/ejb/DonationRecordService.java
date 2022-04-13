/**
 * File: DonationRecordService.java for CST8277 Assignment 4
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

import bloodbank.entity.BloodDonation;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;

@SuppressWarnings("unused")

/**
 * Stateless Singleton ejb Bean - DonationRecordService
 */
@Singleton
public class DonationRecordService implements Serializable {
private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<DonationRecord> getAllDonationRecords() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DonationRecord> cq = cb.createQuery(DonationRecord.class);
        cq.select(cq.from(DonationRecord.class));
        return em.createQuery(cq).getResultList();
    }
    

    public DonationRecord getDonationRecordById(int id) {
		return em.find(DonationRecord.class, id);
    }

    @Transactional
    public DonationRecord persistDonationRecord(DonationRecord newDonationRecord) {
    	Person person = em.find(Person.class, newDonationRecord.getOwner().getId());
    	BloodDonation bloodDonation = em.find(BloodDonation.class, newDonationRecord.getDonation().getId());
  
		newDonationRecord.setOwner(person);
		newDonationRecord.setDonation(bloodDonation);
        em.persist(newDonationRecord);
        return newDonationRecord;
    }

    /**
     * to update a donation record
     * 
     * @param id - id of entity to update
     * @param donationRecordWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public DonationRecord updateDonationRecordById(int id, DonationRecord donationRecordWithUpdates) {
    	DonationRecord donationRecordToBeUpdated = getDonationRecordById(id);
        if (donationRecordToBeUpdated != null) {
            em.refresh(donationRecordToBeUpdated);
            donationRecordToBeUpdated.setOwner(donationRecordWithUpdates.getOwner());
            donationRecordToBeUpdated.setDonation(donationRecordWithUpdates.getDonation());
			if(donationRecordWithUpdates.getTested()!=0) {
				donationRecordToBeUpdated.setTested(true);
			}else {
				donationRecordToBeUpdated.setTested(false);
			}
            em.merge(donationRecordToBeUpdated);
            em.flush();
        }
        return donationRecordToBeUpdated;
    }

    /**
     * to delete a donation record by id
     * 
     * @param id - donation record id to delete
     */
    @Transactional
    public void deleteDonationRecordById(int id) {
    	DonationRecord donationRecordToBeDeleted = getDonationRecordById(id);
		if (donationRecordToBeDeleted != null) {
			em.refresh(donationRecordToBeDeleted);
			em.remove(donationRecordToBeDeleted);
		}
    }

}
