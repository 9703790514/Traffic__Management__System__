package training.iqgateway.services;

import java.util.List;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import training.iqgateway.entities.Offense;

@Stateless(name = "OwnerSessionEJB", mappedName = "TmsApp-Model-OwnerSessionEJB")
public class OwnerSessionEJBBean implements OwnerSessionEJB,
                                            OwnerSessionEJBLocal {
    @PersistenceContext(unitName="Model")
    private EntityManager em;

    public OwnerSessionEJBBean() {
    }

    /** <code>select o from Offense o</code> */
    public List<Offense> getOffenseFindAll() {
        return em.createNamedQuery("Offense.findAll").getResultList();
    }
}
