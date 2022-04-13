/**
 * File: CustomIdentityStoreJPAHelper.java Course materials (22W) CST 8277
 * 
 * @author Teddy Yap
 * @author Mike Norman
 * 
 * Updated by:  Group 5
 * 040991296, Feiqiong Deng
 * 040911749, Juan Ni 
 * 040991653, Sophie Sun
 * 040994750, Jing Zhao
 * 
 */
package bloodbank.security;

import static bloodbank.entity.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import static bloodbank.utility.MyConstants.PARAM1;
import static bloodbank.utility.MyConstants.PU_NAME;

import static java.util.Collections.emptySet;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.log.Log;

import bloodbank.entity.SecurityRole;
import bloodbank.entity.SecurityUser;
import static bloodbank.entity.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@SuppressWarnings("unused")

@Singleton
public class CustomIdentityStoreJPAHelper {

    private static final Logger LOG = LogManager.getLogger();

    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;

    public SecurityUser findUserByName(String username) {
        LOG.debug("find a SecurityUser by name = {}", username);
        SecurityUser user = null;
        /* TODO
         *  Call the entity manager's createNamedQuery() method to call a named query on SecurityUser
         *  The named query should be labeled "SecurityUser.userByName" and accepts a parameter called "param1"
         *  
         *  Call getSingleResult() inside a try-catch statement (NoResultException)
         *  
         *  Note: until this method is complete, the Basic Authentication for all HTTP
         *        requests will fail, none of the REST'ful Endpoints will work.
         *  
         */
        try {
            TypedQuery<SecurityUser> q = em.createNamedQuery(SECURITY_USER_BY_NAME_QUERY , SecurityUser.class);
            q.setParameter(PARAM1, username);
            user = q.getSingleResult();
        }
        catch (NoResultException e) {
        	LOG.debug(e);
        }
        
        return user;
    }

    public Set<String> findRoleNamesForUser(String username) {
        LOG.debug("find Roles For Username={}", username);
        Set<String> roleNames = emptySet();
        SecurityUser securityUser = findUserByName(username);
        if (securityUser != null) {
            roleNames = securityUser.getRoles().stream().map(s -> s.getRoleName()).collect(Collectors.toSet());
        }
        return roleNames;
    }

    @Transactional
    public void saveSecurityUser(SecurityUser user) {
        LOG.debug("adding new user={}", user);
        em.persist(user);
    }

    @Transactional
    public void saveSecurityRole(SecurityRole role) {
        LOG.debug("adding new role={}", role);
        em.persist(role);
    }
}