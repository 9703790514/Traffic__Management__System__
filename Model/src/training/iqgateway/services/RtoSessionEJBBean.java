package training.iqgateway.services;

import java.util.List;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Users;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;

@Stateless(name = "RtoSessionEJB", mappedName = "TmsApp-Model-RtoSessionEJB")
public class RtoSessionEJBBean implements RtoSessionEJB, RtoSessionEJBLocal {
    @PersistenceContext(unitName="Model")
    private EntityManager em;

    public RtoSessionEJBBean() {
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

    public VehicleApplication persistVehicleApplication(VehicleApplication vehicleApplication) {
        em.persist(vehicleApplication);
        return vehicleApplication;
    }

    public VehicleApplication mergeVehicleApplication(VehicleApplication vehicleApplication) {
        return em.merge(vehicleApplication);
    }

    public void removeVehicleApplication(VehicleApplication vehicleApplication) {
        vehicleApplication = em.find(VehicleApplication.class, vehicleApplication.getAppNo());
        em.remove(vehicleApplication);
    }

    /** <code>select o from VehicleApplication o</code> */
    public List<VehicleApplication> getVehicleApplicationFindAll() {
        return em.createNamedQuery("VehicleApplication.findAll").getResultList();
    }

    public Vehicle persistVehicle(Vehicle vehicle) {
        em.persist(vehicle);
        return vehicle;
    }

    public Vehicle mergeVehicle(Vehicle vehicle) {
        return em.merge(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicle = em.find(Vehicle.class, vehicle.getVehId());
        em.remove(vehicle);
    }

    /** <code>select o from Vehicle o</code> */
    public List<Vehicle> getVehicleFindAll() {
        return em.createNamedQuery("Vehicle.findAll").getResultList();
    }

    public OffenseDetails persistOffenseDetails(OffenseDetails offenseDetails) {
        em.persist(offenseDetails);
        return offenseDetails;
    }

    public OffenseDetails mergeOffenseDetails(OffenseDetails offenseDetails) {
        return em.merge(offenseDetails);
    }

    public void removeOffenseDetails(OffenseDetails offenseDetails) {
        offenseDetails = em.find(OffenseDetails.class, offenseDetails.getOffenceId());
        em.remove(offenseDetails);
    }

    /** <code>select o from OffenseDetails o</code> */
    public List<OffenseDetails> getOffenseDetailsFindAll() {
        return em.createNamedQuery("OffenseDetails.findAll").getResultList();
    }

    public Owner persistOwner(Owner owner) {
        em.persist(owner);
        return owner;
    }

    public Owner mergeOwner(Owner owner) {
        return em.merge(owner);
    }

    public void removeOwner(Owner owner) {
        owner = em.find(Owner.class, owner.getOwnerId());
        em.remove(owner);
    }

    /** <code>select o from Owner o</code> */
    public List<Owner> getOwnerFindAll() {
        return em.createNamedQuery("Owner.findAll").getResultList();
    }
    
    public VehicleApplication findVehicleByNo(String vehNo) {
        try {
            Query query = em.createQuery("SELECT v FROM VehicleApplication v WHERE v.vehNo = :vehNo");
            query.setParameter("vehNo", vehNo);
            return (VehicleApplication) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }


    public Users findUserByUsername(String username) {
        try {
            Query query = em.createQuery("SELECT u FROM Users u WHERE u.username = :username");
            query.setParameter("username", username);
            return (Users) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }


       public OffenseDetails findOffenseDetailsById(Long offenceId) {
           return em.find(OffenseDetails.class, offenceId);
       }
       
    public Owner findOwnerByVehicleNo(String vehNo) {
        try {
            Query query = em.createQuery(
                "SELECT v.owner FROM VehicleApplication v WHERE v.vehNo = :vehNo"
            );
            query.setParameter("vehNo", vehNo);
            return (Owner) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    
    public Vehicle findVehicleDetailsByVehNo(String vehNo) {
        try {
            Query query = em.createQuery("SELECT va FROM VehicleApplication va WHERE va.vehNo = :vehNo");
            query.setParameter("vehNo", vehNo);
            VehicleApplication va = (VehicleApplication) query.getSingleResult();
            return va.getVehicle(); // adjust if your getter is named differently
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }
    
    // ... existing code ...

    public Owner findOwnerById(Long ownerId) {
        return em.find(Owner.class, ownerId);
    }

    public Vehicle findVehicleById(Long vehicleId) {
        return em.find(Vehicle.class, vehicleId);
    }

    @Override
    public Owner findOwnerByPanCardNo(String panCardNo) {
        
        Query query = em.createQuery("SELECT o FROM Owner o WHERE o.pancardNo = :panCardNo");
        query.setParameter("panCardNo", panCardNo);
        Owner owner = (Owner) query.getSingleResult();
        return owner;

    }






}
