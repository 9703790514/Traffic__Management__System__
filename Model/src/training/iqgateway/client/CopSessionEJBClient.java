package training.iqgateway.client;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.naming.NamingException;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;
import training.iqgateway.services.CopSessionEJB;

public class CopSessionEJBClient {
    public static void main(String [] args) {
        try {
            final Context context = getInitialContext();
            CopSessionEJB copSessionEJB = (CopSessionEJB)context.lookup("TmsApp-Model-CopSessionEJB#training.iqgateway.services.CopSessionEJB");
            for (Offense offense : (List<Offense>)copSessionEJB.getOffenseFindAll()) {
                printOffense(offense);
            }
            for (VehicleApplication vehicleapplication : (List<VehicleApplication>)copSessionEJB.getVehicleApplicationFindAll()) {
                printVehicleApplication(vehicleapplication);
            }
            for (Vehicle vehicle : (List<Vehicle>)copSessionEJB.getVehicleFindAll()) {
                printVehicle(vehicle);
            }
            for (OffenseDetails offensedetails : (List<OffenseDetails>)copSessionEJB.getOffenseDetailsFindAll()) {
                printOffenseDetails(offensedetails);
            }
            for (Owner owner : (List<Owner>)copSessionEJB.getOwnerFindAll()) {
                printOwner(owner);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printOffense(Offense offense) {
        System.out.println( "image = " + offense.getImage() );
        System.out.println( "offenceDetailId = " + offense.getOffenceDetailId() );
        System.out.println( "offenceStatus = " + offense.getOffenceStatus() );
        System.out.println( "place = " + offense.getPlace() );
        System.out.println( "time = " + offense.getTime() );
        System.out.println( "vehicleApplication = " + offense.getVehicleApplication() );
        System.out.println( "users = " + offense.getUsers() );
        System.out.println( "offenseDetails = " + offense.getOffenseDetails() );
    }

    private static void printVehicleApplication(VehicleApplication vehicleapplication) {
        System.out.println( "appNo = " + vehicleapplication.getAppNo() );
        System.out.println( "dateOfPurchase = " + vehicleapplication.getDateOfPurchase() );
        System.out.println( "distrubuterName = " + vehicleapplication.getDistrubuterName() );
        System.out.println( "vehNo = " + vehicleapplication.getVehNo() );
        System.out.println( "offenseList = " + vehicleapplication.getOffenseList() );
        System.out.println( "owner = " + vehicleapplication.getOwner() );
        System.out.println( "vehicle = " + vehicleapplication.getVehicle() );
    }

    private static void printVehicle(Vehicle vehicle) {
        System.out.println( "cubicCapacity = " + vehicle.getCubicCapacity() );
        System.out.println( "dateOfManufacture = " + vehicle.getDateOfManufacture() );
        System.out.println( "engineNo = " + vehicle.getEngineNo() );
        System.out.println( "fuelUsed = " + vehicle.getFuelUsed() );
        System.out.println( "manufacturerName = " + vehicle.getManufacturerName() );
        System.out.println( "modelNo = " + vehicle.getModelNo() );
        System.out.println( "noOfCylinders = " + vehicle.getNoOfCylinders() );
        System.out.println( "vehColor = " + vehicle.getVehColor() );
        System.out.println( "vehId = " + vehicle.getVehId() );
        System.out.println( "vehName = " + vehicle.getVehName() );
        System.out.println( "vehType = " + vehicle.getVehType() );
        System.out.println( "vehicleApplicationList = " + vehicle.getVehicleApplicationList() );
    }

    private static void printOffenseDetails(OffenseDetails offensedetails) {
        System.out.println( "offenceId = " + offensedetails.getOffenceId() );
        System.out.println( "offenceType = " + offensedetails.getOffenceType() );
        System.out.println( "penalty = " + offensedetails.getPenalty() );
        System.out.println( "vehType = " + offensedetails.getVehType() );
        System.out.println( "offenseList = " + offensedetails.getOffenseList() );
    }

    private static void printOwner(Owner owner) {
        System.out.println( "addProofName = " + owner.getAddProofName() );
        System.out.println( "dateofbirth = " + owner.getDateofbirth() );
        System.out.println( "fname = " + owner.getFname() );
        System.out.println( "gender = " + owner.getGender() );
        System.out.println( "landlineNo = " + owner.getLandlineNo() );
        System.out.println( "lname = " + owner.getLname() );
        System.out.println( "mobileNo = " + owner.getMobileNo() );
        System.out.println( "occupation = " + owner.getOccupation() );
        System.out.println( "ownerId = " + owner.getOwnerId() );
        System.out.println( "pancardNo = " + owner.getPancardNo() );
        System.out.println( "permAddr = " + owner.getPermAddr() );
        System.out.println( "pincode = " + owner.getPincode() );
        System.out.println( "tempAddr = " + owner.getTempAddr() );
        System.out.println( "vehicleApplicationList = " + owner.getVehicleApplicationList() );
    }

    private static Context getInitialContext() throws NamingException {
        Hashtable env = new Hashtable();
        // WebLogic Server 10.x connection details
        env.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
        env.put(Context.PROVIDER_URL, "t3://localhost:7101");
        return new InitialContext( env );
    }
}
