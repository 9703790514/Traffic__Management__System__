package training.iqgateway.backing;

import java.io.Serializable;

import java.util.List;

import javax.faces.context.FacesContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import training.iqgateway.entities.Users;
import training.iqgateway.services.AdminSessionEJBLocal;


/**
 * Backing bean for user authentication and login functionality.
 * Handles user login validation and navigation to role-based dashboards.
 * 
 * Note: Password validation is currently done in plain text. 
 * This should be updated to use password hashing (BCrypt) for security.
 * 
 * @author TMS Development Team
 * @version 1.0
 */
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    private AdminSessionEJBLocal getSessionBean() throws NamingException {
        InitialContext ic = new InitialContext();
        Object lookupObject = ic.lookup("java:comp/env/ejb/local/AdminSessionEJB");
        return (AdminSessionEJBLocal) lookupObject;
    }
              
    
    public Users handleLogin(String username, String password)throws NamingException {
        
            List<Users> users = getSessionBean().getUsersFindAll();
            Users foundUser = null;

            for (Users user : users) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    foundUser = user;
                    break;
                }
            }
            if (foundUser == null) {
                return null;
            }

            if (!foundUser.getPassword().equals(password)) {
                return null;
            }

            return foundUser;
        }
        
        
        public String login()throws NamingException {
           
           Users result = handleLogin(username,  password);
            if(result==null)return "login";
            else if ( result.getRoles().getRolename().equalsIgnoreCase("admin")) return "admin";
            else if ( result.getRoles().getRolename().equalsIgnoreCase("clerk")){
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedInUsername", username);
                return "clerk";
            }
            else if ( result.getRoles().getRolename().equalsIgnoreCase("cop")) return "cop";
            else if ( result.getRoles().getRolename().equalsIgnoreCase("rto")) return "rto";
            else if ( result.getRoles().getRolename().equalsIgnoreCase("owner")) return "owner";
            else return "login";
        }
        
        public String logout(){
            return "logout";
        }
        
       

}



















