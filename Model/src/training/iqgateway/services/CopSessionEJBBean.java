package training.iqgateway.services;

import java.util.List;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;

@Stateless(name = "CopSessionEJB", mappedName = "TmsApp-Model-CopSessionEJB")
public class CopSessionEJBBean implements CopSessionEJB, CopSessionEJBLocal {
    @PersistenceContext(unitName="Model")
    private EntityManager em;

    public CopSessionEJBBean() {
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

    /** <code>select o from VehicleApplication o</code> */
    public List<VehicleApplication> getVehicleApplicationFindAll() {
        return em.createNamedQuery("VehicleApplication.findAll").getResultList();
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
}
