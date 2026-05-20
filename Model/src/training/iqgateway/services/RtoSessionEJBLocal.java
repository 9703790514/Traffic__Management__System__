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
public interface RtoSessionEJBLocal {
    Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

    Offense persistOffense(Offense offense);

    Offense mergeOffense(Offense offense);

    void removeOffense(Offense offense);

    List<Offense> getOffenseFindAll();

    VehicleApplication persistVehicleApplication(VehicleApplication vehicleApplication);

    VehicleApplication mergeVehicleApplication(VehicleApplication vehicleApplication);

    void removeVehicleApplication(VehicleApplication vehicleApplication);

    List<VehicleApplication> getVehicleApplicationFindAll();

    Vehicle persistVehicle(Vehicle vehicle);

    Vehicle mergeVehicle(Vehicle vehicle);

    void removeVehicle(Vehicle vehicle);

    List<Vehicle> getVehicleFindAll();

    OffenseDetails persistOffenseDetails(OffenseDetails offenseDetails);

    OffenseDetails mergeOffenseDetails(OffenseDetails offenseDetails);

    void removeOffenseDetails(OffenseDetails offenseDetails);

    List<OffenseDetails> getOffenseDetailsFindAll();

    Owner persistOwner(Owner owner);

    Owner mergeOwner(Owner owner);

    void removeOwner(Owner owner);

    List<Owner> getOwnerFindAll();
    
    public VehicleApplication findVehicleByNo(String vehNo);
    
    public Users findUserByUsername(String username);
    
    public OffenseDetails findOffenseDetailsById(Long offenceId);
    
    public Owner findOwnerByVehicleNo(String vehNo);
    
    public Vehicle findVehicleDetailsByVehNo(String vehNo);
    
    public Owner findOwnerById(Long ownerId) ;
    
    public Vehicle findVehicleById(Long vehicleId);
    
    public Owner findOwnerByPanCardNo(String panCardNo);



}
