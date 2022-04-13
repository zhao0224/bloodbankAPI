/**
 * File: PhoneService.java for CST8277 Assignment 4
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
import bloodbank.entity.Phone;

@SuppressWarnings("unused")

/**
 * Stateless Singleton ejb Bean - PhoneService
 */
@Singleton
public class PhoneService implements Serializable {
private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Phone> getAllPhones() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
        cq.select(cq.from(Phone.class));
        return em.createQuery(cq).getResultList();
    }
    

    public Phone getPhoneById(int id) {
		return em.find(Phone.class, id);
    }

    @Transactional
    public Phone persistPhone(Phone newPhone) {
        em.persist(newPhone);
        return newPhone;
    }

    /**
     * to update a phone
     * 
     * @param id - id of entity to update
     * @param phoneWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Phone updatePhoneById(int id, Phone phoneWithUpdates) {
    	Phone phoneToBeUpdated = getPhoneById(id);
        if (phoneToBeUpdated != null) {
            em.refresh(phoneToBeUpdated);
            em.merge(phoneWithUpdates);
            em.flush();
        }
        return phoneToBeUpdated;
    }

    /**
     * to delete a phone by id
     * 
     * @param id - phone id to delete
     */
    @Transactional
    public void deletePhoneById(int id) {
    	Phone phoneToBeDeleted = getPhoneById(id);
		if (phoneToBeDeleted != null) {
			em.refresh(phoneToBeDeleted);
			em.remove(phoneToBeDeleted);
		}
    } 

}
