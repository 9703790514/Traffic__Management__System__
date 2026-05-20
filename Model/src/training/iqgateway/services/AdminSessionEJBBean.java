package training.iqgateway.services;

import java.util.List;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import training.iqgateway.entities.Roles;
import training.iqgateway.entities.Users;

/**
 * Stateless session bean providing administrative services.
 * Handles user management, role management, and administrative operations.
 * 
 * @author TMS Development Team
 * @version 1.0
 */
@Stateless(name = "AdminSessionEJB", mappedName = "TmsApp-Model-AdminSessionEJB")
public class AdminSessionEJBBean implements AdminSessionEJB,
                                            AdminSessionEJBLocal {
    @PersistenceContext(unitName="Model")
    private EntityManager em;

    public AdminSessionEJBBean() {
    }

    public Object queryByRange(String jpqlStmt, int firstResult,
                               int maxResults) {
        Query query = em.createQuery(jpqlStmt);
        if (firstResult > 0) {
            query = query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query = query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public Users persistUsers(Users users) {
        em.persist(users);
        return users;
    }

    public Users mergeUsers(Users users) {
        return em.merge(users);
    }

    public void removeUsers(Users users) {
        users = em.find(Users.class, users.getUsername());
        em.remove(users);
    }

    /** <code>select o from Users o</code> */
    public List<Users> getUsersFindAll() {
        return em.createNamedQuery("Users.findAll").getResultList();
    }

    public Roles persistRoles(Roles roles) {
        em.persist(roles);
        return roles;
    }

    public Roles mergeRoles(Roles roles) {
        return em.merge(roles);
    }

    public void removeRoles(Roles roles) {
        roles = em.find(Roles.class, roles.getRolename());
        em.remove(roles);
    }

    /** <code>select o from Roles o</code> */
    public List<Roles> getRolesFindAll() {
        return em.createNamedQuery("Roles.findAll").getResultList();
    }
    
    public Users findUserByUsername(String username) {
            return em.find(Users.class, username);     
        }
    
    public Roles findRoleByRolename(String rolename) {
            return em.find(Roles.class, rolename);     
        }
}
