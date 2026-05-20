package training.iqgateway.entities;

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "VehicleApplication.findAll", query = "select o from VehicleApplication o")
})
@Table(name = "VEHICLE_APPLICATION")
public class VehicleApplication implements Serializable {
    @Id
    @Column(name="APP_NO", nullable = false)
    private Long appNo;
    @Column(name="DATE_OF_PURCHASE", nullable = false)
    private Timestamp dateOfPurchase;
    @Column(name="DISTRUBUTER_NAME", nullable = false, length = 20)
    private String distrubuterName;
    @Column(name="VEH_NO", nullable = false, unique = true, length = 20)
    private String vehNo;
    @OneToMany(mappedBy = "vehicleApplication")
    private List<Offense> offenseList;
    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private Owner owner;
    @ManyToOne
    @JoinColumn(name = "VEH_ID")
    private Vehicle vehicle;

    public VehicleApplication() {
    }

    public VehicleApplication(Long appNo, Timestamp dateOfPurchase,
                              String distrubuterName, Owner owner,
                              Vehicle vehicle,
                              String vehNo) {
        this.appNo = appNo;
        this.dateOfPurchase = dateOfPurchase;
        this.distrubuterName = distrubuterName;
        this.owner = owner;
        this.vehicle = vehicle;
        this.vehNo = vehNo;
    }

    public Long getAppNo() {
        return appNo;
    }

    public void setAppNo(Long appNo) {
        this.appNo = appNo;
    }

    public Timestamp getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(Timestamp dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getDistrubuterName() {
        return distrubuterName;
    }

    public void setDistrubuterName(String distrubuterName) {
        this.distrubuterName = distrubuterName;
    }


    public String getVehNo() {
        return vehNo;
    }

    public void setVehNo(String vehNo) {
        this.vehNo = vehNo;
    }

    public List<Offense> getOffenseList() {
        return offenseList;
    }

    public void setOffenseList(List<Offense> offenseList) {
        this.offenseList = offenseList;
    }

    public Offense addOffense(Offense offense) {
        getOffenseList().add(offense);
        offense.setVehicleApplication(this);
        return offense;
    }

    public Offense removeOffense(Offense offense) {
        getOffenseList().remove(offense);
        offense.setVehicleApplication(null);
        return offense;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    
}
