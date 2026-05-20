package training.iqgateway.services;

import java.util.List;

import javax.ejb.Remote;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;

@Remote
public interface CopSessionEJB {
    Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

    Offense persistOffense(Offense offense);

    Offense mergeOffense(Offense offense);

    void removeOffense(Offense offense);

    List<Offense> getOffenseFindAll();

    List<VehicleApplication> getVehicleApplicationFindAll();

    List<Vehicle> getVehicleFindAll();

    List<OffenseDetails> getOffenseDetailsFindAll();

    List<Owner> getOwnerFindAll();
}
