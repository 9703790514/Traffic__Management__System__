package training.iqgateway.entities;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Entity class representing a system user.
 * Stores user credentials and role information for authentication and authorization.
 * 
 * Security Note: Passwords are currently stored in plain text. 
 * This should be updated to use password hashing (BCrypt/PBKDF2) before production deployment.
 * 
 * @author TMS Development Team
 * @version 1.0
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "Users.findAll", query = "select o from Users o")
})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    @Column(nullable = false, length = 20)
    private String password;
    @Id
    @Column(nullable = false, length = 20)
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "ROLENAME")
    private Roles roles;
    
    
    @OneToMany(mappedBy = "users")
    private List<Offense> offenseList;

    public Users() {
    }

    public Users(String password, Roles roles, String username) {
        this.password = password;
        this.roles = roles;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public List<Offense> getOffenseList() {
        return offenseList;
    }

    public void setOffenseList(List<Offense> offenseList) {
        this.offenseList = offenseList;
    }

    public Offense addOffense(Offense offense) {
        getOffenseList().add(offense);
        offense.setUsers(this);
        return offense;
    }

    public Offense removeOffense(Offense offense) {
        getOffenseList().remove(offense);
        offense.setUsers(null);
        return offense;
    }
}
