/**
 * File: BloodBankService.java
 * Course materials (22W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 5
 * 040991296, Feiqiong Deng
 * 040911749, Juan Ni 
 * 040991653, Sophie Sun
 * 040994750, Jing Zhao
 *
 */
package bloodbank.ejb;

import static bloodbank.entity.BloodBank.ALL_BLOODBANKS_QUERY_NAME;
import static bloodbank.entity.BloodBank.SPECIFIC_BLOODBANKS_QUERY_NAME;
import static bloodbank.entity.BloodBank.IS_DUPLICATE_QUERY_NAME;
import static bloodbank.entity.Person.ALL_PERSONS_QUERY_NAME;
import static bloodbank.utility.MyConstants.DEFAULT_KEY_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.DEFAULT_SALT_SIZE;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static bloodbank.utility.MyConstants.DEFAULT_USER_PREFIX;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PROPERTY_ALGORITHM;
import static bloodbank.utility.MyConstants.PROPERTY_ITERATIONS;
import static bloodbank.utility.MyConstants.PROPERTY_KEYSIZE;
import static bloodbank.utility.MyConstants.PROPERTY_SALTSIZE;
import static bloodbank.utility.MyConstants.PU_NAME;
import static bloodbank.utility.MyConstants.USER_ROLE;

import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.OWNER_BY_NAME_QUERY;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.Contact;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;

@SuppressWarnings("unused")

/**
 * Stateless Singleton ejb Bean - BloodBankService
 */
@Singleton
public class BloodBankService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    public List<Person> getAllPeople() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        cq.select(cq.from(Person.class));
        return em.createQuery(cq).getResultList();
    }

    public Person getPersonId(int id) {
        return em.find(Person.class, id);
    }

    @Transactional
    public Person persistPerson(Person newPerson) {
        em.persist(newPerson);
        return newPerson;
    }

    @Transactional
    public void buildUserForNewPerson(Person newPerson) {
        SecurityUser userForNewPerson = new SecurityUser();
        userForNewPerson.setUsername(
            DEFAULT_USER_PREFIX + "_" + newPerson.getFirstName() + "." + newPerson.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewPerson.setPwHash(pwHash);
        userForNewPerson.setPerson(newPerson);
        SecurityRole userRole =em.createNamedQuery(ROLE_BY_NAME_QUERY, SecurityRole.class).setParameter(PARAM1, USER_ROLE).getSingleResult(); /* TODO - use NamedQuery on SecurityRole to find USER_ROLE */
        userForNewPerson.getRoles().add(userRole);
        userRole.getUsers().add(userForNewPerson);
        em.persist(userForNewPerson);
    }

    @Transactional
    public Address setAddressForPersonPhone(int personId, int phoneId, Address newAddress) {
        Person personToBeUpdated = em.find(Person.class, personId);
        if (personToBeUpdated != null) { // Person exists
            Set<Contact> contacts = personToBeUpdated.getContacts();
            contacts.forEach(c -> {
                if (c.getPhone().getId() == phoneId) {
                    if (c.getAddress() != null) { // Address exists
                        Address addr = em.find(Address.class, c.getAddress().getId());
                        addr.setAddress(newAddress.getStreetNumber(),
                                        newAddress.getStreet(),
                                        newAddress.getCity(),
                                        newAddress.getProvince(),
                                        newAddress.getCountry(),
                                        newAddress.getZipcode());
                        em.merge(addr);
                    }
                    else { // Address does not exist
                        c.setAddress(newAddress);
                        em.merge(personToBeUpdated);
                    }
                }
            });
            return newAddress;
        }
        else return null;  // Person doesn't exists
    }

    /**
     * to update a person
     * 
     * @param id - id of entity to update
     * @param personWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Person updatePersonById(int id, Person personWithUpdates) {
        Person personToBeUpdated = getPersonId(id);
        if (personToBeUpdated != null) {
            em.refresh(personToBeUpdated);
            em.merge(personWithUpdates);
            em.flush();
        }
        return personToBeUpdated;
    }

    /**
     * to delete a person by id
     * 
     * @param id - person id to delete
     */
    @Transactional
    public void deletePersonById(int id) {
        Person person = getPersonId(id);
        if (person != null) {
            em.refresh(person);
            TypedQuery<SecurityUser> findUser = em.createNamedQuery(OWNER_BY_NAME_QUERY,  SecurityUser.class);
                /* TODO - use NamedQuery on SecurityRole to find this related Person
                   so that when we remove it, the relationship from SECURITY_USER table
                   is not dangling
                */            
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(person);
        }
    }
    
    public List<BloodBank> getAllBloodBanks() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BloodBank> cq = cb.createQuery(BloodBank.class);
        cq.select(cq.from(BloodBank.class));
        return em.createQuery(cq).getResultList();
    }

    // why not use the build-in em.find? The named query SPECIFIC_BLOODBANKS_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
    public BloodBank getBloodBankById(int id) {
        TypedQuery<BloodBank> specificBloodBankQuery = em.createNamedQuery(SPECIFIC_BLOODBANKS_QUERY_NAME, BloodBank.class);
        specificBloodBankQuery.setParameter(PARAM1, id);
        return specificBloodBankQuery.getSingleResult();
    }
    
    // These methods are more generic.

    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        return allQuery.getSingleResult();
    }

    @Transactional
    public BloodBank deleteBloodBank(int id) {
        //BloodBank bb = getBloodBankById(id);
        BloodBank bb = getById(BloodBank.class, BloodBank.SPECIFIC_BLOODBANKS_QUERY_NAME, id);
        if (bb != null) {
            Set<BloodDonation> donations = bb.getDonations();
            List<BloodDonation> list = new LinkedList<>();
            donations.forEach(list::add);
            list.forEach(bd -> {
                if (bd.getRecord() != null) {
                    DonationRecord dr = getById(DonationRecord.class, DonationRecord.ID_RECORD_QUERY_NAME, bd.getRecord().getId());
                    dr.setDonation(null);
                }
                bd.setRecord(null);
                em.merge(bd);
            });
            em.remove(bb);
            return bb;
        }
        return null;
    }
    
    // Please study & use the methods below in your Test Suites
    
    public boolean isDuplicated(BloodBank newBloodBank) {
        TypedQuery<Long> allBloodBankQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allBloodBankQuery.setParameter(PARAM1, newBloodBank.getName());
        return (allBloodBankQuery.getSingleResult() >= 1);
    }

    @Transactional
    public BloodBank persistBloodBank(BloodBank newBloodBank) {
        em.persist(newBloodBank);
        return newBloodBank;
    }

    @Transactional
    public BloodBank updateBloodBank(int id, BloodBank updatingBloodBank) {
        BloodBank bloodBankToBeUpdated = getBloodBankById(id);
        if (bloodBankToBeUpdated != null) {
            em.refresh(bloodBankToBeUpdated);
            em.merge(updatingBloodBank);
            em.flush();
        }
        return bloodBankToBeUpdated;
    }
    
    @Transactional
    public BloodDonation persistBloodDonation(BloodDonation newBloodDonation) {
        em.persist(newBloodDonation);
        return newBloodDonation;
    }

    public BloodDonation getBloodDonationById(int prodId) {
        TypedQuery<BloodDonation> allBloodDonationQuery = em.createNamedQuery(BloodDonation.FIND_BY_ID, BloodDonation.class);
        allBloodDonationQuery.setParameter(PARAM1, prodId);
        return allBloodDonationQuery.getSingleResult();
    }

    @Transactional
    public BloodDonation updateBloodDonation(int id, BloodDonation bloodDonationWithUpdates) {
        BloodDonation bloodDonationToBeUpdated = getBloodDonationById(id);
        if (bloodDonationToBeUpdated != null) {
            em.refresh(bloodDonationToBeUpdated);
            em.merge(bloodDonationWithUpdates);
            em.flush();
        }
        return bloodDonationToBeUpdated;
    }
    
}