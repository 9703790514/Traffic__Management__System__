package training.iqgateway.services;

import java.util.List;

import javax.ejb.Remote;

import training.iqgateway.entities.Roles;
import training.iqgateway.entities.Users;

@Remote
public interface AdminSessionEJB {
    Object queryByRange(String jpqlStmt, int firstResult, int maxResults);

    Users persistUsers(Users users);

    Users mergeUsers(Users users);

    void removeUsers(Users users);

    List<Users> getUsersFindAll();

    Roles persistRoles(Roles roles);

    Roles mergeRoles(Roles roles);

    void removeRoles(Roles roles);

    List<Roles> getRolesFindAll();
    
    public Users findUserByUsername(String username);
    
    public Roles findRoleByRolename(String rolename);

}
