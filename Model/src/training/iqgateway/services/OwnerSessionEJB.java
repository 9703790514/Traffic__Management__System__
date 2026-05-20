package training.iqgateway.services;

import java.util.List;

import javax.ejb.Remote;

import training.iqgateway.entities.Offense;

@Remote
public interface OwnerSessionEJB {
    List<Offense> getOffenseFindAll();
}
