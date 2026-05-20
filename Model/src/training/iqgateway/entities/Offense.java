package training.iqgateway.entities;

import java.io.Serializable;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

/**
 * Entity class representing a traffic offense record.
 * Stores information about traffic violations including place, time, status, and evidence.
 * 
 * @author TMS Development Team
 * @version 1.0
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "Offense.findAll", query = "select o from Offense o")
})
public class Offense implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Transient
       private boolean selected;

       // Getter and setter for selected
       public boolean isSelected() {
           return selected;
       }

       public void setSelected(boolean selected) {
           this.selected = selected;
       }
    private byte[] image;
    @Id
    @Column(name="OFFENCE_DETAIL_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Offense_id_Seq")
    @SequenceGenerator(name = "Offense_id_Seq", sequenceName = "OFFENCE_ID_SEQ")
    private Long offenceDetailId;
    @Column(name="OFFENCE_STATUS", length = 20)
    private String offenceStatus;
    @Column(nullable = false, length = 20)
    private String place;
    @Column(nullable = false)
    private Timestamp time;
    @ManyToOne
    @JoinColumn(name = "VEH_NO", referencedColumnName = "VEH_NO")
    private VehicleApplication vehicleApplication;
    @ManyToOne
    @JoinColumn(name = "REPORTED_BY")
    private Users users;
    //@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OFFENCE_ID")
    private OffenseDetails offenseDetails;

    public Offense() {
    }

    public Offense(Long offenceDetailId, OffenseDetails offenseDetails, String offenceStatus,
                   String place, Users users, Timestamp time,
                   VehicleApplication vehicleApplication) {
        this.offenceDetailId = offenceDetailId;
        this.offenseDetails = offenseDetails;
        this.offenceStatus = offenceStatus;
        this.place = place;
        this.users = users;
        this.time = time;
        this.vehicleApplication = vehicleApplication;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getOffenceDetailId() {
        return offenceDetailId;
    }

    public void setOffenceDetailId(Long offenceDetailId) {
        this.offenceDetailId = offenceDetailId;
    }


    public String getOffenceStatus() {
        return offenceStatus;
    }

    public void setOffenceStatus(String offenceStatus) {
        this.offenceStatus = offenceStatus;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }


    public VehicleApplication getVehicleApplication() {
        return vehicleApplication;
    }

    public void setVehicleApplication(VehicleApplication vehicleApplication) {
        this.vehicleApplication = vehicleApplication;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public OffenseDetails getOffenseDetails() {
        return offenseDetails;
    }

    public void setOffenseDetails(OffenseDetails offenseDetails) {
        this.offenseDetails = offenseDetails;
    }
}
