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
import training.iqgateway.services.ClerkSessionEJB;

public class ClerkSessionEJBClient {
    public static void main(String [] args) {
        try {
            final Context context = getInitialContext();
            ClerkSessionEJB clerkSessionEJB = (ClerkSessionEJB)context.lookup("TmsApp-Model-ClerkSessionEJB#training.iqgateway.services.ClerkSessionEJB");
            for (Offense offense : (List<Offense>)clerkSessionEJB.getOffenseFindAll()) {
                printOffense(offense);
            }
            for (Vehicle vehicle : (List<Vehicle>)clerkSessionEJB.getVehicleFindAll()) {
                printVehicle(vehicle);
            }
            for (OffenseDetails offensedetails : (List<OffenseDetails>)clerkSessionEJB.getOffenseDetailsFindAll()) {
                printOffenseDetails(offensedetails);
            }
            for (Owner owner : (List<Owner>)clerkSessionEJB.getOwnerFindAll()) {
                printOwner(owner);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prints offense details for debugging purposes.
     * Note: In production, use a proper logging framework like SLF4J/Logback.
     * @param offense The offense object to print
     */
    private static void printOffense(Offense offense) {
        // TODO: Replace System.out with proper logging framework
        System.out.println( "image = " + offense.getImage() );
        System.out.println( "offenceDetailId = " + offense.getOffenceDetailId() );
        System.out.println( "offenceStatus = " + offense.getOffenceStatus() );
        System.out.println( "place = " + offense.getPlace() );
        System.out.println( "time = " + offense.getTime() );
        System.out.println( "vehicleApplication = " + offense.getVehicleApplication() );
        System.out.println( "users = " + offense.getUsers() );
        System.out.println( "offenseDetails = " + offense.getOffenseDetails() );
    }

    /**
     * Prints vehicle details for debugging purposes.
     * Note: In production, use a proper logging framework like SLF4J/Logback.
     * @param vehicle The vehicle object to print
     */
    private static void printVehicle(Vehicle vehicle) {
        // TODO: Replace System.out with proper logging framework
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
