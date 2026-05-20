package training.iqgateway.backing;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import training.iqgateway.entities.Offense;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;
import training.iqgateway.services.RtoSessionEJBLocal;

public class VehicleApplicationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Form fields
    private Long appNo;
    private Timestamp dateOfPurchase;
    private String distrubuterName;
    private String vehNo; // Vehicle number for application form

    // Owner and Vehicle selection
    private Long selectedOwnerId;
    private Long selectedVehicleId;

    // For transfer operation
    private String transferVehicleNumber; // Vehicle number for transfer search
    private Long selectedNewOwnerId;      // New owner for transfer
    private Owner currentOwner;           // Current owner in transfer
    private Vehicle currentVehicle;       // Current vehicle in transfer

    // Lists for dropdowns
    private List<SelectItem> ownerSelectItems;
    private List<SelectItem> vehicleSelectItems;

    // Lists for search results (optional)
    private List<Owner> ownerSearchResults;
    private List<Vehicle> vehicleSearchResults;

    public VehicleApplicationBean() {}

    @PostConstruct
    public void init() {
        loadOwnerSelectItems();
        loadVehicleSelectItems();
    }

    private RtoSessionEJBLocal getSessionBean() throws NamingException {
        InitialContext ic = new InitialContext();
        return (RtoSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/RtoSessionEJB");
    }

    // --- Dropdown population ---
    private void loadOwnerSelectItems() {
        ownerSelectItems = new ArrayList();
        try {
            List<Owner> owners = getSessionBean().getOwnerFindAll();
            for (Owner owner : owners) {
                ownerSelectItems.add(new SelectItem(owner.getOwnerId(), owner.getFname() + " " + owner.getLname()));
            }
        } catch (Exception e) {
            ownerSelectItems = new ArrayList();
        }
    }
    private void loadVehicleSelectItems() {
        vehicleSelectItems = new ArrayList();
        try {
            List<Vehicle> vehicles = getSessionBean().getVehicleFindAll();
            for (Vehicle vehicle : vehicles) {
                vehicleSelectItems.add(new SelectItem(vehicle.getVehId(), vehicle.getVehName()));
            }
        } catch (Exception e) {
            vehicleSelectItems = new ArrayList();
        }
    }

    // --- Application Save ---
    public String saveApplication() {
        try {
            RtoSessionEJBLocal ejb = getSessionBean();

            if (selectedOwnerId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select an owner.", null));
                return null;
            }
            if (selectedVehicleId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a vehicle.", null));
                return null;
            }

            Owner ownerEntity = ejb.findOwnerById(selectedOwnerId);
            Vehicle vehicleEntity = ejb.findVehicleById(selectedVehicleId);

            if (ownerEntity == null || vehicleEntity == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Owner or Vehicle not found!", null));
                return null;
            }

            VehicleApplication application = new VehicleApplication();
            application.setAppNo(appNo);
            application.setDateOfPurchase(dateOfPurchase);
            application.setDistrubuterName(distrubuterName);
            application.setVehNo(vehNo);
            application.setOwner(ownerEntity);
            application.setVehicle(vehicleEntity);

            ejb.persistVehicleApplication(application);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle Application added successfully!", null));

            // Reset form fields
            this.appNo = null;
            this.dateOfPurchase = null;
            this.distrubuterName = null;
            this.selectedOwnerId = null;
            this.selectedVehicleId = null;
            this.vehNo = null;

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving application: " + e.getMessage(), null));
        }
        return null;
    }

    // --- Ownership Transfer ---
    public String submitTransfer() {
        try {
            if (transferVehicleNumber == null || transferVehicleNumber.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter a vehicle number.", null));
                return null;
            }
            if (selectedNewOwnerId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a new owner.", null));
                return null;
            }

            RtoSessionEJBLocal ejb = getSessionBean();

            // 1. Find VehicleApplication by vehicle number
            VehicleApplication app = ejb.findVehicleByNo(transferVehicleNumber);
            if (app == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "No application found for vehicle number: " + transferVehicleNumber, null));
                return null;
            }

            // 2. Find new owner
            Owner newOwner = ejb.findOwnerById(selectedNewOwnerId);
            if (newOwner == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Selected new owner not found.", null));
                return null;
            }

            // 3. Update VehicleApplication's owner
            app.setOwner(newOwner);
            ejb.mergeVehicleApplication(app);

            // 4. Update bean state
            this.currentOwner = newOwner;
            this.currentVehicle = app.getVehicle();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Vehicle ownership transferred successfully!", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Transfer failed: " + e.getMessage(), null));
        }
        return null;
    }

    // --- Getters and Setters ---

    public Long getAppNo() { return appNo; }
    public void setAppNo(Long appNo) { this.appNo = appNo; }
    public Timestamp getDateOfPurchase() { return dateOfPurchase; }
    public void setDateOfPurchase(Timestamp dateOfPurchase) { this.dateOfPurchase = dateOfPurchase; }
    public String getDistrubuterName() { return distrubuterName; }
    public void setDistrubuterName(String distrubuterName) { this.distrubuterName = distrubuterName; }
    public String getVehNo() { return vehNo; }
    public void setVehNo(String vehNo) { this.vehNo = vehNo; }

    public Long getSelectedOwnerId() { return selectedOwnerId; }
    public void setSelectedOwnerId(Long selectedOwnerId) { this.selectedOwnerId = selectedOwnerId; }
    public Long getSelectedVehicleId() { return selectedVehicleId; }
    public void setSelectedVehicleId(Long selectedVehicleId) { this.selectedVehicleId = selectedVehicleId; }

    public List<SelectItem> getOwnerSelectItems() { return ownerSelectItems; }
    public List<SelectItem> getVehicleSelectItems() { return vehicleSelectItems; }

    public String getTransferVehicleNumber() { return transferVehicleNumber; }
    public void setTransferVehicleNumber(String transferVehicleNumber) { this.transferVehicleNumber = transferVehicleNumber; }

    public Long getSelectedNewOwnerId() { return selectedNewOwnerId; }
    public void setSelectedNewOwnerId(Long selectedNewOwnerId) { this.selectedNewOwnerId = selectedNewOwnerId; }

    public Owner getCurrentOwner() { return currentOwner; }
    public void setCurrentOwner(Owner currentOwner) { this.currentOwner = currentOwner; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(Vehicle currentVehicle) { this.currentVehicle = currentVehicle; }

    // Optionally: other getters/setters for search results etc.
}
