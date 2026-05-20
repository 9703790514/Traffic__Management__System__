package training.iqgateway.services;

import java.util.List;

import javax.ejb.Local;

import training.iqgateway.entities.Roles;
import training.iqgateway.entities.Users;

@Local
public interface AdminSessionEJBLocal {
    Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

    Users persistUsers(Users users);

    Users mergeUsers(Users users);

    void removeUsers(Users users);

    List<Users> getUsersFindAll();

    Roles persistRoles(Roles roles);

    Roles mergeRoles(Roles roles);
    
    public Users findUserByUsername(String username);


    void removeRoles(Roles roles);

    List<Roles> getRolesFindAll();
    
    public Roles findRoleByRolename(String rolename);

}
