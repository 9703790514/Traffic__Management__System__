package training.iqgateway.client;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.naming.NamingException;

import training.iqgateway.entities.Offense;
import training.iqgateway.services.OwnerSessionEJB;

public class OwnerSessionEJBClient {
    public static void main(String [] args) {
        try {
            final Context context = getInitialContext();
            OwnerSessionEJB ownerSessionEJB = (OwnerSessionEJB)context.lookup("TmsApp-Model-OwnerSessionEJB#training.iqgateway.services.OwnerSessionEJB");
            for (Offense offense : (List<Offense>)ownerSessionEJB.getOffenseFindAll()) {
                printOffense(offense);
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

    private static Context getInitialContext() throws NamingException {
        Hashtable env = new Hashtable();
        // WebLogic Server 10.x connection details
        env.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
        env.put(Context.PROVIDER_URL, "t3://localhost:7101");
        return new InitialContext( env );
    }
}
