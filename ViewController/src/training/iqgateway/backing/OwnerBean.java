import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import training.iqgateway.services.RtoSessionEJBLocal;
import training.iqgateway.entities.Owner;



public class OwnerBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private RtoSessionEJBLocal rtoSessionEJB;

    // --- Properties for Add/Edit Form ---
    private String addProofName;
    private Timestamp dateofbirth;
    private String fname;
    private String lname;
    private String gender;
    private String mobileNo;
    private String landlineNo;
    private String occupation;
    private String pancardNo;
    private String permAddr;
    private String tempAddr;
    private Long pincode;

    // --- Properties for Page Navigation and UI State ---
    private String pageMode = "none";
    private boolean editMode = false;
    private boolean confirmDeleteMode = false;
    private Owner selectedOwner;

    // --- Properties for View/Search ---
    private List<Owner> owners;
    private List<Owner> foundOwners;
    private String searchFname;
    // >>> ADDED THIS PROPERTY <<<
    private boolean searched = false; // Initialize to false

    // --- Properties for Message Modal ---
    private boolean showMessageModal = false;
    private String messageTitle;
    private String messageContent;

    // Also in your constructor or @PostConstruct, add a print to see initial state
        @PostConstruct
        public void init() {
            loadAllOwners();
            System.out.println("OwnerBean initialized. Initial pageMode: " + pageMode);
        }
    private void loadAllOwners() {
        try {
            owners = rtoSessionEJB.getOwnerFindAll();
        } catch (Exception e) {
            addErrorMessage("Error loading owners: " + e.getMessage());
            e.printStackTrace();
            owners = new ArrayList();
        }
    }

    // --- Action Methods ---

    // In training.iqgateway.backing.OwnerBean.java

    public void navigateToMode() {
        // This line will execute AFTER f:setPropertyActionListener has set pageMode
        System.out.println("OwnerBean.navigateToMode() called. Current pageMode: " + pageMode);

        if (!"update".equals(pageMode) && !"delete".equals(pageMode)) {
            resetFormFields();
            cancelEdit();
            cancelDelete();
        }
        if ("view".equals(pageMode) || "update".equals(pageMode) || "delete".equals(pageMode)) {
            loadAllOwners();
        }
        if ("search".equals(pageMode)) {
            clearSearch();
        }
        addInfoMessage("Navigated to " + pageMode.toUpperCase() + " mode.");
        System.out.println("OwnerBean.navigateToMode() finished.");
    }

    
    public String dashboard() {
        setPageMode("dashboard");
        //long count = rtoSessionEJB.getOwnerCount();
        addInfoMessage("Welcome to the Dashboard! Total Owners: " + 0);
        return null;
    }

    public void addOwner() {
        try {
            Owner newOwner = new Owner();
            newOwner.setAddProofName(addProofName);
            newOwner.setDateofbirth(dateofbirth);
            newOwner.setFname(fname);
            newOwner.setLname(lname);
            newOwner.setGender(gender);
            newOwner.setMobileNo(mobileNo);
            newOwner.setLandlineNo(landlineNo);
            newOwner.setOccupation(occupation);
            newOwner.setPancardNo(pancardNo);
            newOwner.setPermAddr(permAddr);
            newOwner.setTempAddr(tempAddr);
            newOwner.setPincode(pincode);

            rtoSessionEJB.persistOwner(newOwner);
            addInfoMessage("Owner " + fname + " " + lname + " added successfully!");
            resetFormFields();
            setPageMode("add");
            loadAllOwners();
        } catch (Exception e) {
            addErrorMessage("Error adding owner: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startEdit() {
        if (selectedOwner != null && selectedOwner.getOwnerId() != null) {
            Owner freshOwner = rtoSessionEJB.findOwnerById(selectedOwner.getOwnerId());
            if (freshOwner != null) {
                this.selectedOwner = freshOwner;
                this.addProofName = freshOwner.getAddProofName();
                this.dateofbirth = freshOwner.getDateofbirth();
                this.fname = freshOwner.getFname();
                this.lname = freshOwner.getLname();
                this.gender = freshOwner.getGender();
                this.mobileNo = freshOwner.getMobileNo();
                this.landlineNo = freshOwner.getLandlineNo();
                this.occupation = freshOwner.getOccupation();
                this.pancardNo = freshOwner.getPancardNo();
                this.permAddr = freshOwner.getPermAddr();
                this.tempAddr = freshOwner.getTempAddr();
                this.pincode = freshOwner.getPincode();
                setEditMode(true);
                addInfoMessage("Editing owner ID: " + selectedOwner.getOwnerId());
            } else {
                addErrorMessage("Could not find owner to edit (ID: " + selectedOwner.getOwnerId() + ") in the database. It might have been deleted.");
                loadAllOwners();
            }
        } else {
            addWarnMessage("Please select an owner to edit.");
        }
    }

    public void saveEdit() {
        if (selectedOwner != null) {
            try {
                selectedOwner.setAddProofName(addProofName);
                selectedOwner.setDateofbirth(dateofbirth);
                selectedOwner.setFname(fname);
                selectedOwner.setLname(lname);
                selectedOwner.setGender(gender);
                selectedOwner.setMobileNo(mobileNo);
                selectedOwner.setLandlineNo(landlineNo);
                selectedOwner.setOccupation(occupation);
                selectedOwner.setPancardNo(pancardNo);
                selectedOwner.setPermAddr(permAddr);
                selectedOwner.setTempAddr(tempAddr);
                selectedOwner.setPincode(pincode);

                rtoSessionEJB.mergeOwner(selectedOwner);
                setEditMode(false);
                resetFormFields();
                addInfoMessage("Owner ID " + selectedOwner.getOwnerId() + " updated successfully!");
                loadAllOwners();
            } catch (Exception e) {
                addErrorMessage("Error saving owner changes: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void cancelEdit() {
        setEditMode(false);
        setSelectedOwner(null);
        resetFormFields();
        addInfoMessage("Edit operation cancelled.");
    }

    public void askDelete() {
        if (selectedOwner != null) {
            setConfirmDeleteMode(true);
            addWarnMessage("Confirm deletion for owner: " + selectedOwner.getFname() + " " + selectedOwner.getLname() + " (ID: " + selectedOwner.getOwnerId() + ")");
        } else {
            addWarnMessage("Please select an owner to delete.");
        }
    }

    public void confirmDelete() {
        if (selectedOwner != null && selectedOwner.getOwnerId() != null) {
            try {
                rtoSessionEJB.removeOwner(selectedOwner);
                addInfoMessage("Owner " + selectedOwner.getFname() + " " + selectedOwner.getLname() + " deleted successfully!");
                cancelDelete();
                loadAllOwners();
            } catch (Exception e) {
                addErrorMessage("Error deleting owner: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void cancelDelete() {
        setConfirmDeleteMode(false);
        setSelectedOwner(null);
        addInfoMessage("Delete operation cancelled.");
    }

    public void searchOwnerByFirstName() {
        if (searchFname != null && !searchFname.trim().isEmpty()) {
            try {
                //foundOwners = rtoSessionEJB.searchOwnersByFirstName(searchFname.trim());
                if (foundOwners.isEmpty()) {
                    addWarnMessage("No owners found with first name containing '" + searchFname + "'.");
                } else {
                    addInfoMessage(foundOwners.size() + " owner(s) found.");
                }
            } catch (Exception e) {
                addErrorMessage("Error during search: " + e.getMessage());
                e.printStackTrace();
                foundOwners = new ArrayList();
            }
        } else {
            addWarnMessage("Please enter a first name to search.");
            foundOwners = new ArrayList();
        }
        setSearched(true); // Set to true after a search attempt
    }

    public void clearSearch() {
        searchFname = null;
        foundOwners = null;
        setSearched(false); // Reset to false when search is cleared
        addInfoMessage("Search cleared.");
    }

    // --- Helper Methods ---
    private void resetFormFields() {
        this.addProofName = null;
        this.dateofbirth = null;
        this.fname = null;
        this.lname = null;
        this.gender = null;
        this.mobileNo = null;
        this.landlineNo = null;
        this.occupation = null;
        this.pancardNo = null;
        this.permAddr = null;
        this.tempAddr = null;
        this.pincode = null;
    }

    private void addInfoMessage(String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
        setMessageTitle("Information");
        setMessageContent(summary);
        setShowMessageModal(true);
    }

    private void addWarnMessage(String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, null));
        setMessageTitle("Warning");
        setMessageContent(summary);
        setShowMessageModal(true);
    }

    private void addErrorMessage(String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
        setMessageTitle("Error");
        setMessageContent(summary);
        setShowMessageModal(true);
    }

    public void hideMessageModal() {
        setShowMessageModal(false);
        setMessageTitle(null);
        setMessageContent(null);
    }

    // --- Getters and Setters ---
    public String getAddProofName() { return addProofName; }
    public void setAddProofName(String addProofName) { this.addProofName = addProofName; }

    public Timestamp getDateofbirth() { return dateofbirth; }
    public void setDateofbirth(Timestamp dateofbirth) { this.dateofbirth = dateofbirth; }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getLandlineNo() { return landlineNo; }
    public void setLandlineNo(String landlineNo) { this.landlineNo = landlineNo; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getPancardNo() { return pancardNo; }
    public void setPancardNo(String pancardNo) { this.pancardNo = pancardNo; }

    public String getPermAddr() { return permAddr; }
    public void setPermAddr(String permAddr) { this.permAddr = permAddr; }

    public String getTempAddr() { return tempAddr; }
    public void setTempAddr(String tempAddr) { this.tempAddr = tempAddr; }

    public Long getPincode() { return pincode; }
    public void setPincode(Long pincode) { this.pincode = pincode; }

    public String getPageMode() { return pageMode; }
    public void setPageMode(String pageMode) { this.pageMode = pageMode; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    public boolean isConfirmDeleteMode() { return confirmDeleteMode; }
    public void setConfirmDeleteMode(boolean confirmDeleteMode) { this.confirmDeleteMode = confirmDeleteMode; }

    public Owner getSelectedOwner() { return selectedOwner; }
    public void setSelectedOwner(Owner selectedOwner) { this.selectedOwner = selectedOwner; }

    public List<Owner> getOwners() {
        return owners;
    }

    public List<Owner> getFoundOwners() { return foundOwners; }
    public void setFoundOwners(List<Owner> foundOwners) { this.foundOwners = foundOwners; }

    public String getSearchFname() { return searchFname; }
    public void setSearchFname(String searchFname) { this.searchFname = searchFname; }

    // >>> ADDED GETTER AND SETTER FOR 'searched' <<<
    public boolean isSearched() { return searched; }
    public void setSearched(boolean searched) { this.searched = searched; }

    public boolean isShowMessageModal() { return showMessageModal; }
    public void setShowMessageModal(boolean showMessageModal) { this.showMessageModal = showMessageModal; }

    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public long getOwnerCount() {
        if (rtoSessionEJB != null) {
          //  return rtoSessionEJB.getOwnerCount();
        }
        return 0;
    }
    

 

   

}