package training.iqgateway.services;

import java.util.List;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Users;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;

@Stateless(name = "ClerkSessionEJB", mappedName = "TmsApp-Model-ClerkSessionEJB")
public class ClerkSessionEJBBean implements ClerkSessionEJB,
                                            ClerkSessionEJBLocal {
    @PersistenceContext(unitName="Model")
    private EntityManager em;

    public ClerkSessionEJBBean() {
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

    public Offense persistOffense(Offense offense) {
        em.persist(offense);
        return offense;
    }

    public Offense mergeOffense(Offense offense) {
        return em.merge(offense);
    }

    public void removeOffense(Offense offense) {
        offense = em.find(Offense.class, offense.getOffenceDetailId());
        em.remove(offense);
    }

    /** <code>select o from Offense o</code> */
    public List<Offense> getOffenseFindAll() {
        return em.createNamedQuery("Offense.findAll").getResultList();
    }

    /** <code>select o from Vehicle o</code> */
    public List<Vehicle> getVehicleFindAll() {
        return em.createNamedQuery("Vehicle.findAll").getResultList();
    }

    /** <code>select o from OffenseDetails o</code> */
    public List<OffenseDetails> getOffenseDetailsFindAll() {
        return em.createNamedQuery("OffenseDetails.findAll").getResultList();
    }

    /** <code>select o from Owner o</code> */
    public List<Owner> getOwnerFindAll() {
        return em.createNamedQuery("Owner.findAll").getResultList();
    }
    
    public Vehicle findVehicleByNo(String vehNo) {
           try {
               return em.createQuery("SELECT v FROM Vehicle v WHERE v.vehNo = :vehNo", Vehicle.class)
                        .setParameter("vehNo", vehNo)
                        .getSingleResult();
           } catch (javax.persistence.NoResultException e) {
               return null;
           }
       }

       public Users findUserByUsername(String username) {
           return (Users) em.createQuery("SELECT u FROM Users u WHERE u.username = :username")
                            .setParameter("username", username)
                            .getSingleResult();

       }

       public OffenseDetails findOffenseDetailsById(Long offenceId) {
           return em.find(OffenseDetails.class, offenceId);
       }
       
    
    
    @Override
        public List<Offense> findOffensesByVehicleNo(String vehNo) {
            Query query = em.createQuery("SELECT o FROM Offense o WHERE o.vehicleApplication.vehNo = :vehNo");
            query.setParameter("vehNo", vehNo);
            return query.getResultList();
        }
    
   


    public VehicleApplication findVehicleApplicationByVehNo(String vehNo) {
        try {
            return (VehicleApplication) em.createQuery("SELECT v FROM VehicleApplication v WHERE v.vehNo = :vehNo")
                                         .setParameter("vehNo", vehNo)
                                         .getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    
    


}
