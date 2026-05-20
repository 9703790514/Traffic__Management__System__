package training.iqgateway.services;

import java.util.List;

import javax.ejb.Local;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Users;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;

@Local
public interface ClerkSessionEJBLocal {
    Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

    Offense persistOffense(Offense offense);

    Offense mergeOffense(Offense offense);

    void removeOffense(Offense offense);

    List<Offense> getOffenseFindAll();

    List<Vehicle> getVehicleFindAll();

    List<OffenseDetails> getOffenseDetailsFindAll();

    List<Owner> getOwnerFindAll();
    
    public Vehicle findVehicleByNo(String vehNo);
    
    public Users findUserByUsername(String username);
    
    public OffenseDetails findOffenseDetailsById(Long offenceId);
    
    public List<Offense> findOffensesByVehicleNo(String vehNo);
    
    public VehicleApplication findVehicleApplicationByVehNo(String vehNo);


}
