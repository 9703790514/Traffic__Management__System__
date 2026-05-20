package training.iqgateway.entities;

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

/**
 * Entity class representing a vehicle owner.
 * Stores owner personal information, contact details, and identification documents.
 * 
 * @author TMS Development Team
 * @version 1.0
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "Owner.findAll", query = "select o from Owner o")
})
public class Owner implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name="ADD_PROOF_NAME", nullable = false, length = 20)
    private String addProofName;
    @Column(nullable = false)
    private Timestamp dateofbirth;
    @Column(nullable = false, length = 20)
    private String fname;
    @Column(nullable = false)
    private String gender;
    @Column(name="LANDLINE_NO", length = 20)
    private String landlineNo;
    @Column(nullable = false, length = 20)
    private String lname;
    @Column(name="MOBILE_NO", length = 10)
    private String mobileNo;
    @Column(length = 20)
    private String occupation;
    @Id
    @Column(name="OWNER_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OwnerSeq")
    @SequenceGenerator(name = "OwnerSeq", sequenceName = "OWNER_ID_SEQ", allocationSize = 1)
    private Long ownerId;
    @Column(name="PANCARD_NO", nullable = false, length = 20)
    private String pancardNo;
    @Column(name="PERM_ADDR", nullable = false, length = 20)
    private String permAddr;
    @Column(nullable = false)
    private Long pincode;
    @Column(name="TEMP_ADDR", length = 20)
    private String tempAddr;
    @OneToMany(mappedBy = "owner")
    private List<VehicleApplication> vehicleApplicationList;

    public Owner() {
    }

    public Owner(String addProofName, Timestamp dateofbirth, String fname,
                 String gender, String landlineNo, String lname,
                 String mobileNo, String occupation, Long ownerId,
                 String pancardNo, String permAddr, Long pincode,
                 String tempAddr) {
        this.addProofName = addProofName;
        this.dateofbirth = dateofbirth;
        this.fname = fname;
        this.gender = gender;
        this.landlineNo = landlineNo;
        this.lname = lname;
        this.mobileNo = mobileNo;
        this.occupation = occupation;
        this.ownerId = ownerId;
        this.pancardNo = pancardNo;
        this.permAddr = permAddr;
        this.pincode = pincode;
        this.tempAddr = tempAddr;
    }

    public String getAddProofName() {
        return addProofName;
    }

    public void setAddProofName(String addProofName) {
        this.addProofName = addProofName;
    }

    public Timestamp getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(Timestamp dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLandlineNo() {
        return landlineNo;
    }

    public void setLandlineNo(String landlineNo) {
        this.landlineNo = landlineNo;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPancardNo() {
        return pancardNo;
    }

    public void setPancardNo(String pancardNo) {
        this.pancardNo = pancardNo;
    }

    public String getPermAddr() {
        return permAddr;
    }

    public void setPermAddr(String permAddr) {
        this.permAddr = permAddr;
    }

    public Long getPincode() {
        return pincode;
    }

    public void setPincode(Long pincode) {
        this.pincode = pincode;
    }

    public String getTempAddr() {
        return tempAddr;
    }

    public void setTempAddr(String tempAddr) {
        this.tempAddr = tempAddr;
    }

    public List<VehicleApplication> getVehicleApplicationList() {
        return vehicleApplicationList;
    }

    public void setVehicleApplicationList(List<VehicleApplication> vehicleApplicationList) {
        this.vehicleApplicationList = vehicleApplicationList;
    }

    public VehicleApplication addVehicleApplication(VehicleApplication vehicleApplication) {
        getVehicleApplicationList().add(vehicleApplication);
        vehicleApplication.setOwner(this);
        return vehicleApplication;
    }

    public VehicleApplication removeVehicleApplication(VehicleApplication vehicleApplication) {
        getVehicleApplicationList().remove(vehicleApplication);
        vehicleApplication.setOwner(null);
        return vehicleApplication;
    }
}
