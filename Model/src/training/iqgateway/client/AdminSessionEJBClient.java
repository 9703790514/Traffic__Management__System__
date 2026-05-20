package training.iqgateway.client;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.naming.NamingException;

import training.iqgateway.entities.Roles;
import training.iqgateway.entities.Users;
import training.iqgateway.services.AdminSessionEJB;

public class AdminSessionEJBClient {
    public static void main(String [] args) {
        try {
            final Context context = getInitialContext();
            AdminSessionEJB adminSessionEJB = (AdminSessionEJB)context.lookup("TmsApp-Model-AdminSessionEJB#training.iqgateway.services.AdminSessionEJB");
            for (Users users : (List<Users>)adminSessionEJB.getUsersFindAll()) {
                printUsers(users);
            }
            for (Roles roles : (List<Roles>)adminSessionEJB.getRolesFindAll()) {
                printRoles(roles);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printUsers(Users users) {
        System.out.println( "password = " + users.getPassword() );
        System.out.println( "username = " + users.getUsername() );
        System.out.println( "roles = " + users.getRoles() );
        System.out.println( "offenseList = " + users.getOffenseList() );
    }

    private static void printRoles(Roles roles) {
        System.out.println( "rolename = " + roles.getRolename() );
        System.out.println( "roleDesc = " + roles.getRoleDesc() );
        System.out.println( "usersList = " + roles.getUsersList() );
    }

    private static Context getInitialContext() throws NamingException {
        Hashtable env = new Hashtable();
        // WebLogic Server 10.x connection details
        env.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
        env.put(Context.PROVIDER_URL, "t3://localhost:7101");
        return new InitialContext( env );
    }
}
