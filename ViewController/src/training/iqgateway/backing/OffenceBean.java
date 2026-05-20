package training.iqgateway.backing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
//import javax.servlet.http.Part; // Import for file upload

import javax.servlet.http.Part;

import training.iqgateway.entities.Offense;
import training.iqgateway.entities.OffenseDetails;
import training.iqgateway.entities.Users;
import training.iqgateway.entities.VehicleApplication;
import training.iqgateway.services.ClerkSessionEJBLocal;

public class OffenceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Offense> offenses;
    private List<Offense> pendingOffenses;

    private Offense selectedOffense = new Offense();
    private boolean editMode = false;
    private String pageMode = "none";
    private boolean confirmDeleteMode = false;
    private String username;

    // Form fields
    private Long offenceDetailId;
    private String offenceStatus;
    private String place;
    private Timestamp time;
    private VehicleApplication vehicleApplication = new VehicleApplication();
    private Users users = new Users();
    private OffenseDetails offenseDetails = new OffenseDetails();
    private byte[] image;

    // NEW: For file upload
    private Part uploadedFile; // To hold the uploaded file

    private Long selectedOffenseId;

    private String searchVehNo;

    public OffenceBean() {
        // no-arg constructor required by JSF!
    }


    @PostConstruct
    public void init() {

        // Get username from session
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String sessionUsername = (String) facesContext.getExternalContext().getSessionMap().get("loggedInUsername");
        if (sessionUsername != null) {
            if (users == null) users = new Users();
            users.setUsername(sessionUsername);
        }
        // ... rest of your initialization ...

        // Ensure nested beans are initialized
        if (vehicleApplication == null) vehicleApplication = new VehicleApplication();
        if (users == null) users = new Users();
        if (offenseDetails == null) offenseDetails = new OffenseDetails();
        refreshOffenses();
    }

    public OffenceBean(String username){
        this.username = username;
    }


    private void refreshOffenses() {
        try {
            offenses = getSessionBean().getOffenseFindAll();
        } catch (Exception e) {
            offenses = new ArrayList();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading offenses: " + e.getMessage(), null));
        }
    }

    public void addOffense() {
        FacesContext context = FacesContext.getCurrentInstance();
        this.pageMode = "add"; // Stay on add page in case of error
        InputStream input = null; // Declare InputStream outside try block

        try {
            // Fetch OffenseDetails using selectedOffenseId
            OffenseDetails selectedDetails = getSessionBean().findOffenseDetailsById(selectedOffenseId);
            this.offenseDetails = selectedDetails;

            // Fetch full VehicleApplication and Users objects
            VehicleApplication fullVehicleApp = getSessionBean().findVehicleApplicationByVehNo(vehicleApplication.getVehNo());
            // It's good practice to check if fullVehicleApp is null after fetching
            if (fullVehicleApp == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Vehicle Number.", null));
                return; // Stop processing if vehicle not found
            }

            Users fullUser = getSessionBean().findUserByUsername(users.getUsername());
            // It's good practice to check if fullUser is null after fetching
            if (fullUser == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Reported By Username.", null));
                return; // Stop processing if user not found
            }


            // Create Offense object
            Offense newOffense = new Offense();
            newOffense.setPlace(place);
            newOffense.setTime(time);
            newOffense.setVehicleApplication(fullVehicleApp);
            newOffense.setUsers(fullUser);
            newOffense.setOffenseDetails(selectedDetails);
            newOffense.setOffenceStatus("Pending"); // or your logic
            
            

            // Process uploaded image using traditional try-finally
            if (uploadedFile != null) {
                try {
                    input = uploadedFile.getInputStream();
                    newOffense.setImage(input.readAllBytess(input));
                } catch (IOException e) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error reading image file: " + e.getMessage(), null));
                    return; // Stop processing if image read fails
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            // Log this error, but don't prevent the main operation
                            System.err.println("Error closing input stream: " + e.getMessage());
                        }
                    }
                }
            } else {
                newOffense.setImage(null); // Ensure no old image is used if not uploaded
            }


            getSessionBean().persistOffense(newOffense);
            refreshOffenses();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Offense added successfully!", null));
            resetFormFields();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add failed: " + e.getMessage(), null));
            // No change to pageMode, stays on "add" for correction
        }
    }
    
    public static byte[] readAllBytess(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }


    public void startEdit() {
        this.editMode = true;
        this.pageMode = "update";
    }
    
    public static byte[] readAllBytess(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }


    public void saveEdit() {
        this.pageMode = "update"; // Ensure correct page mode after save
        try {
            getSessionBean().mergeOffense(selectedOffense);
            refreshOffenses();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Offense updated successfully!", null));
            this.editMode = false;
            this.selectedOffense = new Offense();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update failed: " + e.getMessage(), null));
            this.editMode = true; // Stay in edit mode if update fails
        }
    }

    public void cancelEdit() {
        this.editMode = false;
        this.selectedOffense = new Offense();
        this.pageMode = "update";
    }

    public void askDelete() {
        this.confirmDeleteMode = true;
        this.pageMode = "delete";
    }

    public void confirmDelete() {
        this.pageMode = "delete";
        try {
            getSessionBean().removeOffense(selectedOffense);
            refreshOffenses();
            confirmDeleteMode = false;
            selectedOffense = new Offense();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Offense deleted!", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete failed: " + e.getMessage(), null));
        }
    }

    public void cancelDelete() {
        confirmDeleteMode = false;
        selectedOffense = new Offense();
        this.pageMode = "delete";
    }

    private void resetFormFields() {
        offenceDetailId = null;
        offenceStatus = null;
        place = null;
        time = null;
        vehicleApplication = new VehicleApplication();
        users = new Users();
        offenseDetails = new OffenseDetails();
        image = null; // Reset image field
        uploadedFile = null; // Reset uploaded file
        selectedOffenseId = null;
    }

    public void searchByVehicleNumber() {
        if (searchVehNo == null || searchVehNo.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Please enter a vehicle number.", null));
            pendingOffenses = new ArrayList();
            return;
        }
        try {
            List<Offense> allOffenses = getSessionBean().findOffensesByVehicleNo(searchVehNo.trim());
            if (allOffenses == null) {
                allOffenses = new ArrayList();
            }
            List<Offense> filtered = new ArrayList();
            for (Offense o : allOffenses) {
                if (o.getOffenceStatus() != null && o.getOffenceStatus().equalsIgnoreCase("pending")) {
                    filtered.add(o);
                }
            }
            pendingOffenses = filtered;

            if (pendingOffenses.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No pending offenses found for vehicle: " + searchVehNo, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading offenses: " + e.getMessage(), null));
            pendingOffenses = new ArrayList();
        }
    }

    public void fetchOffenceByVehicleNo() {
        if (searchVehNo == null || searchVehNo.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Please enter a vehicle number.", null));
            pendingOffenses = new ArrayList();
            return;
        }
        try {
            List<Offense> allOffenses = getSessionBean().findOffensesByVehicleNo(searchVehNo.trim());
            if (allOffenses == null) {
                allOffenses = new ArrayList();
            }
            pendingOffenses = allOffenses;

            if (pendingOffenses.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No offenses found for vehicle: " + searchVehNo, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading offenses: " + e.getMessage(), null));
            pendingOffenses = new ArrayList();
        }
    }

    public void clearSelectedOffenses() {
        if (pendingOffenses == null || pendingOffenses.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "No offenses available.", null));
            return;
        }
        int count = 0;
        try {
            ClerkSessionEJBLocal sessionBean = getSessionBean();
            for (Offense offense : pendingOffenses) {
                if (offense.isSelected()) {
                    offense.setOffenceStatus("resolved");
                    sessionBean.mergeOffense(offense);
                    count++;
                }
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, count + " offenses marked as resolved.", null));
            searchByVehicleNumber();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating offenses: " + e.getMessage(), null));
        }
    }

    private ClerkSessionEJBLocal getSessionBean() throws NamingException {
        InitialContext ic = new InitialContext();
        return (ClerkSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/ClerkSessionEJB");
    }

    // Getters and setters

    public List<Offense> getOffenses() {
        return offenses;
    }

    public void setOffenses(List<Offense> offenses) {
        this.offenses = offenses;
    }

    public List<Offense> getPendingOffenses() {
        return pendingOffenses;
    }

    public void setPendingOffenses(List<Offense> pendingOffenses) {
        this.pendingOffenses = pendingOffenses;
    }

    public Offense getSelectedOffense() {
        return selectedOffense;
    }

    public void setSelectedOffense(Offense selectedOffense) {
        this.selectedOffense = selectedOffense;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getPageMode() {
        return pageMode;
    }

    public void setPageMode(String pageMode) {
        this.pageMode = pageMode;
    }

    public boolean isConfirmDeleteMode() {
        return confirmDeleteMode;
    }

    public void setConfirmDeleteMode(boolean confirmDeleteMode) {
        this.confirmDeleteMode = confirmDeleteMode;
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
        if (vehicleApplication == null) vehicleApplication = new VehicleApplication();
        return vehicleApplication;
    }

    public void setVehicleApplication(VehicleApplication vehicleApplication) {
        this.vehicleApplication = vehicleApplication;
    }

    public Users getUsers() {
        if (users == null) users = new Users();
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public OffenseDetails getOffenseDetails() {
        if (offenseDetails == null) offenseDetails = new OffenseDetails();
        return offenseDetails;
    }

    public void setOffenseDetails(OffenseDetails offenseDetails) {
        this.offenseDetails = offenseDetails;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    // NEW Getters and Setters for uploadedFile
    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    // END NEW Getters and Setters for uploadedFile

    public String getSearchVehNo() {
        return searchVehNo;
    }

    public void setSearchVehNo(String searchVehNo) {
        this.searchVehNo = searchVehNo;
    }

    public String prepareClear() {
        this.pageMode = "update";
        return "offence";
    }

    public String prepareAdd() {
        this.pageMode = "add";
        return "offence";
    }

    public String showOwnerDetails() {
        this.pageMode = "owner";
        return "details";
    }

    public String showVehicleDetails() {
        this.pageMode = "vehicle";
        return "details";
    }

    public String showOffenceDetails() {
        this.pageMode = "offence";
        return "details";
    }

    public String setPageModeOwner() {
        this.pageMode = "owner";
        return null;
    }

    public String setPageModeVehicle() {
        this.pageMode = "vehicle";
        return null;
    }

    public String setPageModeOffence() {
        this.pageMode = "offence";
        return null;
    }

    private List<OffenseDetails> offenceItems;
    private Long offenceId;

    public List<OffenseDetails> getOffenceItems() throws NamingException {
        if (offenceItems == null) {
            offenceItems = getSessionBean().getOffenseDetailsFindAll();
        }
        return offenceItems;
    }

    public Long getSelectedOffenseId() {
        return selectedOffenseId;
    }

    public void setSelectedOffenseId(Long selectedOffenseId) {
        this.selectedOffenseId = selectedOffenseId;
    }

    private List<SelectItem> offenceSelectItems;

    public List<SelectItem> getOffenceSelectItems() throws NamingException {
        if (offenceSelectItems == null) {
            offenceSelectItems = new ArrayList();
            for (OffenseDetails od : getSessionBean().getOffenseDetailsFindAll()) {
                offenceSelectItems.add(new SelectItem(od.getOffenceId(), od.getOffenceType()));
            }
        }
        return offenceSelectItems;
    }

    private double totalPenalty;

    public double getTotalPenalty() {
        return totalPenalty;
    }

    public void setTotalPenalty(double totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    // This method recalculates the total penalty for selected offenses
    public void calculateTotalPenalty() {
        totalPenalty = 0.0;
        if (pendingOffenses != null) {
            for (Offense o : pendingOffenses) {
                if (o.isSelected() && o.getOffenseDetails() != null) {
                    // Assuming penalty is a numeric type (double/BigDecimal)
                    totalPenalty += o.getOffenseDetails().getPenalty();
                }
            }
        }
    }

    private boolean selected;

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    private boolean showConfirmClear = false;

    public boolean isShowConfirmClear() {
        return showConfirmClear;
    }
    public void setShowConfirmClear(boolean showConfirmClear) {
        this.showConfirmClear = showConfirmClear;
    }

    // Step 1: Show confirmation panel
    public void askClearSelectedOffenses() {
        calculateTotalPenalty(); // Make sure totalPenalty is up-to-date
        showConfirmClear = true;
    }

    // Step 2: Actually clear selected offenses
    public void confirmClearSelectedOffenses() throws NamingException{
        // Your existing clearSelectedOffenses logic here
        int count = 0;
        if (pendingOffenses != null) {
            for (Offense offense : pendingOffenses) {
                if (offense.isSelected()) {
                    offense.setOffenceStatus("resolved");
                    getSessionBean().mergeOffense(offense);
                    count++;
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, count + " offenses marked as resolved.", null));
        searchByVehicleNumber();
        showConfirmClear = false;
    }

    // Step 3: Cancel confirmation
    public void cancelClearSelectedOffenses() {
        showConfirmClear = false;
    }
}