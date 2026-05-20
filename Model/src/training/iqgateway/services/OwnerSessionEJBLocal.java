package training.iqgateway.services;

import java.util.List;

import javax.ejb.Local;

import training.iqgateway.entities.Offense;

@Local
public interface OwnerSessionEJBLocal {
    List<Offense> getOffenseFindAll();
}
