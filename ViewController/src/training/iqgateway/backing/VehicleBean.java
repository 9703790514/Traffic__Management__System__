package training.iqgateway.backing;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import training.iqgateway.entities.Vehicle;
import training.iqgateway.services.RtoSessionEJBLocal;

public class VehicleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Vehicle> vehicles;
    private Vehicle selectedVehicle = new Vehicle();

    private boolean editMode = false;
    private String pageMode = "dashboard"; // Default to dashboard on load
    private boolean confirmDeleteMode = false;

    // Form fields corresponding to Vehicle entity - these hold the input values
    private Long cubicCapacity;
    private Timestamp dateOfManufacture;
    private String engineNo;
    private String fuelUsed;
    private String manufacturerName;
    private String modelNo;
    private Long noOfCylinders;
    private String vehColor;
    private Long vehId; // Used for identifying in update/delete, but not set for add (auto-generated)
    private String vehName;
    private String vehType;

    // Search field
    private String searchVehNo;

    // --- Modal Message Properties ---
    private boolean showMessageModal = false;
    private String messageTitle;
    private String messageContent;
    private FacesMessage.Severity messageSeverity; // To indicate success/error/warning styling if needed in modal

    @PostConstruct
    public void init() {
        refreshVehicles();
        this.pageMode = "dashboard"; // Default to dashboard on load
    }

       public String getPageMode() {
           return pageMode;
       }

       // Individual action methods for each page mode
       public String navigateToDashboard() {
           this.pageMode = "dashboard";
           return null; // Return null to stay on the same page and re-render
       }

       public String navigateToAddVehicle() {
           this.pageMode = "add";
           return null;
       }

       public String navigateToUpdateVehicle() {
           this.pageMode = "update";
           return null;
       }

       public String navigateToDeleteVehicle() {
           this.pageMode = "delete";
           return null;
       }

       public String navigateToViewVehicles() {
           this.pageMode = "view";
           return null;
       }

    private void refreshVehicles() {
        try {
            vehicles = getSessionBean().getVehicleFindAll();
            if (vehicles == null) {
                vehicles = new ArrayList(); // Use diamond operator for cleaner code
            }
        } catch (Exception e) {
            vehicles = new ArrayList();
            // Avoid modal directly in PostConstruct as page might not be fully rendered.
            // Using FacesMessage here for initial load errors.
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading vehicles: " + e.getMessage(), null));
        }
    }

    // --- Modal Message Helper Methods ---
    /**
     * Displays a custom modal message.
     * @param title The title of the message.
     * @param content The main content/body of the message.
     * @param severity The severity of the message (e.g., FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_ERROR).
     */
    public void showModalMessage(String title, String content, FacesMessage.Severity severity) {
        this.messageTitle = title;
        this.messageContent = content;
        this.messageSeverity = severity;
        this.showMessageModal = true;
        // Optionally add FacesMessage to context for debugging or if h:messages is also used
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, content));
    }

    /**
     * Hides the custom modal message.
     */
    public void hideMessageModal() {
        this.showMessageModal = false;
        this.messageTitle = null;
        this.messageContent = null;
        this.messageSeverity = null;
        // No direct programmatic way to clear FacesMessages from context in JSF after being processed by h:messages.
        // Hiding the modal and letting h:messages clear on next render is standard.
    }
    // --- End Modal Message Helper Methods ---

    // Page navigation methods
    public String dashboard() {
        this.pageMode = "dashboard";
        hideMessageModal(); // Hide any existing message when navigating
        refreshVehicles(); // Refresh data for dashboard view
        return null; // Stay on the current page
    }

    /**
     * Sets the current page mode for navigation.
     * @param mode The mode to set ('add', 'update', 'delete', 'view', 'dashboard').
     * @return null to stay on the current page.
     */
    public String setPageMode(String mode) {
        this.pageMode = mode;
        this.editMode = false; // Reset edit mode when changing page mode
        this.confirmDeleteMode = false; // Reset delete confirmation
        this.selectedVehicle = new Vehicle(); // Clear selected vehicle
        resetFormFields(); // Clear temporary form fields
        hideMessageModal(); // Hide any existing message when navigating
        refreshVehicles(); // Refresh vehicle list for update/delete/view pages
        return null;
    }

    // Add Vehicle
    public void addVehicle() {
        try {
            Vehicle newVehicle = new Vehicle();
            newVehicle.setCubicCapacity(this.cubicCapacity);
            newVehicle.setDateOfManufacture(this.dateOfManufacture);
            newVehicle.setEngineNo(this.engineNo);
            newVehicle.setFuelUsed(this.fuelUsed);
            newVehicle.setManufacturerName(this.manufacturerName);
            newVehicle.setModelNo(this.modelNo);
            newVehicle.setNoOfCylinders(this.noOfCylinders);
            newVehicle.setVehColor(this.vehColor);
            // vehId is typically auto-generated, do not set it for new vehicle
            newVehicle.setVehName(this.vehName);
            newVehicle.setVehType(this.vehType);

            getSessionBean().persistVehicle(newVehicle);
            refreshVehicles(); // Refresh the list of vehicles
            showModalMessage("Success!", "Vehicle '" + this.vehName + "' added successfully!", FacesMessage.SEVERITY_INFO);
            resetFormFields(); // Clear form after successful add
            this.pageMode = "add"; // Stay on add page
        } catch (Exception e) {
            showModalMessage("Error!", "Failed to add vehicle: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            this.pageMode = "add"; // Stay on add page to allow correction
        }
    }

    // Update Vehicle
    public void startEdit() {
        if (selectedVehicle != null && selectedVehicle.getVehId() != null) {
            // Populate form fields with selectedVehicle's data
            this.vehId = selectedVehicle.getVehId(); // Keep the ID for update
            this.cubicCapacity = selectedVehicle.getCubicCapacity();
            this.dateOfManufacture = selectedVehicle.getDateOfManufacture();
            this.engineNo = selectedVehicle.getEngineNo();
            this.fuelUsed = selectedVehicle.getFuelUsed();
            this.manufacturerName = selectedVehicle.getManufacturerName();
            this.modelNo = selectedVehicle.getModelNo();
            this.noOfCylinders = selectedVehicle.getNoOfCylinders();
            this.vehColor = selectedVehicle.getVehColor();
            this.vehName = selectedVehicle.getVehName();
            this.vehType = selectedVehicle.getVehType();

            this.editMode = true;
            this.pageMode = "update"; // Ensure we are on the update section
            hideMessageModal(); // Hide any previous messages
        } else {
            showModalMessage("Warning!", "No vehicle selected for edit.", FacesMessage.SEVERITY_WARN);
            this.editMode = false;
            this.pageMode = "update"; // Stay on update page to show list
        }
    }

    public void saveEdit() {
        try {
            // Ensure selectedVehicle is not null and has the ID set
            if (selectedVehicle == null || selectedVehicle.getVehId() == null) {
                showModalMessage("Error!", "No vehicle selected for update.", FacesMessage.SEVERITY_ERROR);
                this.editMode = false;
                this.pageMode = "update";
                return;
            }

            // Update the selectedVehicle object with current form field values
            selectedVehicle.setCubicCapacity(this.cubicCapacity);
            selectedVehicle.setDateOfManufacture(this.dateOfManufacture);
            selectedVehicle.setEngineNo(this.engineNo);
            selectedVehicle.setFuelUsed(this.fuelUsed);
            selectedVehicle.setManufacturerName(this.manufacturerName);
            selectedVehicle.setModelNo(this.modelNo);
            selectedVehicle.setNoOfCylinders(this.noOfCylinders);
            selectedVehicle.setVehColor(this.vehColor);
            // vehId is already set in selectedVehicle from startEdit
            selectedVehicle.setVehName(this.vehName);
            selectedVehicle.setVehType(this.vehType);

            getSessionBean().mergeVehicle(selectedVehicle);
            refreshVehicles(); // Refresh the list after update
            showModalMessage("Success!", "Vehicle '" + selectedVehicle.getVehName() + "' updated successfully!", FacesMessage.SEVERITY_INFO);

            this.editMode = false; // Exit edit mode
            this.selectedVehicle = new Vehicle(); // Clear selected vehicle
            resetFormFields(); // Clear form fields
            this.pageMode = "update"; // Return to the list view in update section

        } catch (Exception e) {
            showModalMessage("Error!", "Update failed: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            this.editMode = true; // Stay in edit mode for correction
            this.pageMode = "update";
        }
    }

    public void cancelEdit() {
        this.editMode = false;
        this.selectedVehicle = new Vehicle(); // Clear selected vehicle
        resetFormFields(); // Clear form fields
        this.pageMode = "update"; // Go back to the update list view
        hideMessageModal(); // Hide any existing messages
    }

    // Delete Vehicle
    public void askDelete() {
        if (selectedVehicle != null && selectedVehicle.getVehId() != null) {
            this.confirmDeleteMode = true;
            this.pageMode = "delete"; // Stay on the delete section to show confirmation
        } else {
            showModalMessage("Warning!", "No vehicle selected for deletion.", FacesMessage.SEVERITY_WARN);
            this.confirmDeleteMode = false;
            this.pageMode = "delete"; // Stay on delete page to show list
        }
    }

    public void confirmDelete() {
        try {
            if (selectedVehicle != null && selectedVehicle.getVehId() != null) {
                getSessionBean().removeVehicle(selectedVehicle);
                refreshVehicles(); // Refresh the list after deletion
                showModalMessage("Success!", "Vehicle deleted successfully!", FacesMessage.SEVERITY_INFO);
            } else {
                showModalMessage("Error!", "No vehicle selected for deletion.", FacesMessage.SEVERITY_ERROR);
            }
            this.confirmDeleteMode = false; // Hide confirmation dialog
            this.selectedVehicle = new Vehicle(); // Clear selected vehicle
            resetFormFields(); // Clear form fields (though not strictly necessary for delete)
            this.pageMode = "delete"; // Return to the list view in delete section

        } catch (Exception e) {
            showModalMessage("Error!", "Delete failed: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            this.confirmDeleteMode = true; // Keep confirmation dialog if deletion fails
            this.pageMode = "delete";
        }
    }

    public void cancelDelete() {
        this.confirmDeleteMode = false; // Hide confirmation dialog
        this.selectedVehicle = new Vehicle(); // Clear selected vehicle
        resetFormFields(); // Clear form fields
        this.pageMode = "delete"; // Go back to the delete list view
        hideMessageModal(); // Hide any existing messages
    }

    // Search Vehicle
    public void fetchVehicleByVehNo() {
        try {
            if (searchVehNo != null && !searchVehNo.trim().isEmpty()) {
                Vehicle vehicle = getSessionBean().findVehicleDetailsByVehNo(searchVehNo);
                if (vehicle != null) {
                    this.selectedVehicle = vehicle;
                    this.editMode = false; // Not in edit mode for viewing
                    this.pageMode = "view"; // Set page mode to view
                    // Populate form fields for display in the view section
                    this.cubicCapacity = vehicle.getCubicCapacity();
                    this.dateOfManufacture = vehicle.getDateOfManufacture();
                    this.engineNo = vehicle.getEngineNo();
                    this.fuelUsed = vehicle.getFuelUsed();
                    this.manufacturerName = vehicle.getManufacturerName();
                    this.modelNo = vehicle.getModelNo();
                    this.noOfCylinders = vehicle.getNoOfCylinders();
                    this.vehColor = vehicle.getVehColor();
                    this.vehId = vehicle.getVehId();
                    this.vehName = vehicle.getVehName();
                    this.vehType = vehicle.getVehType();
                    showModalMessage("Found!", "Vehicle found for Engine No: " + searchVehNo, FacesMessage.SEVERITY_INFO);
                } else {
                    showModalMessage("Not Found", "No vehicle found with Engine No: " + searchVehNo, FacesMessage.SEVERITY_WARN);
                    this.selectedVehicle = new Vehicle(); // Clear previous selection
                    resetFormFields(); // Clear all form fields
                    this.pageMode = "view"; // Stay on view mode to show the empty state or message
                }
            } else {
                showModalMessage("Input Required", "Please enter an Engine No. to search.", FacesMessage.SEVERITY_WARN);
                this.selectedVehicle = new Vehicle(); // Clear previous selection
                resetFormFields(); // Clear all form fields
                this.pageMode = "view"; // Stay on view mode
            }
        } catch (Exception e) {
            showModalMessage("Error!", "Search failed: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            this.selectedVehicle = new Vehicle(); // Clear previous selection
            resetFormFields(); // Clear all form fields
            this.pageMode = "view"; // Stay on view mode in case of error
        }
    }

    private void resetFormFields() {
        cubicCapacity = null;
        dateOfManufacture = null;
        engineNo = null;
        fuelUsed = null;
        manufacturerName = null;
        modelNo = null;
        noOfCylinders = null;
        vehColor = null;
        vehId = null; // Important to reset ID for new additions
        vehName = null;
        vehType = null;
        searchVehNo = null; // Reset search field as well
    }

    // Helper method to get the EJB session bean
    private RtoSessionEJBLocal getSessionBean() throws NamingException {
        InitialContext ic = new InitialContext();
        // Ensure this JNDI name matches your EJB deployment
        return (RtoSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/RtoSessionEJB");
    }

    /**
     * Retrieves the total count of vehicles.
     * @return The number of vehicles, or 0 if an error occurs.
     */
    public long getVehicleCount() {
        try {
            if (vehicles != null) {
                return vehicles.size();
            }
        } catch (Exception e) {
            // Log the exception if needed
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error getting vehicle count: " + e.getMessage(), null));
        }
        return 0; // Return 0 in case of error or if vehicles list is null
    }

    // --- Getters and Setters for all fields ---

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Vehicle getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(Vehicle selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

        

    // No simple 'setPageMode' here, as the existing 'setPageMode(String mode)' method
    // handles the logic for changing the page mode.

    public boolean isConfirmDeleteMode() {
        return confirmDeleteMode;
    }

    public void setConfirmDeleteMode(boolean confirmDeleteMode) {
        this.confirmDeleteMode = confirmDeleteMode;
    }

    public Long getCubicCapacity() {
        return cubicCapacity;
    }

    public void setCubicCapacity(Long cubicCapacity) {
        this.cubicCapacity = cubicCapacity;
    }

    public Timestamp getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(Timestamp dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(String fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public Long getNoOfCylinders() {
        return noOfCylinders;
    }

    public void setNoOfCylinders(Long noOfCylinders) {
        this.noOfCylinders = noOfCylinders;
    }

    public String getVehColor() {
        return vehColor;
    }

    public void setVehColor(String vehColor) {
        this.vehColor = vehColor;
    }

    public Long getVehId() {
        return vehId;
    }

    public void setVehId(Long vehId) {
        this.vehId = vehId;
    }

    public String getVehName() {
        return vehName;
    }

    public void setVehName(String vehName) { // Corrected method signature (removed duplicate 'void')
        this.vehName = vehName;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    public String getSearchVehNo() {
        return searchVehNo;
    }

    public void setSearchVehNo(String searchVehNo) {
        this.searchVehNo = searchVehNo;
    }

    // Modal properties getters and setters
    public boolean isShowMessageModal() { return showMessageModal; }
    public void setShowMessageModal(boolean showMessageModal) { this.showMessageModal = showMessageModal; }

    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public FacesMessage.Severity getMessageSeverity() { return messageSeverity; }
    public void setMessageSeverity(FacesMessage.Severity messageSeverity) { this.messageSeverity = messageSeverity; }
}