package training.iqgateway.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator; // Import Iterator
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import training.iqgateway.entities.OffenseDetails;
// import training.iqgateway.services.ClerkSessionEJBLocal; // Not used in this bean, keeping it for context if needed elsewhere
import training.iqgateway.services.RtoSessionEJBLocal;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.application.FacesMessage.SEVERITY_WARN;
import static javax.faces.application.FacesMessage.SEVERITY_FATAL;



public class OffenceDetailsBean implements Serializable {

    private List<OffenseDetails> offenseDetails;
    private List<OffenseDetails> foundOffenses; // For search results
    private OffenseDetails selectedOffense = new OffenseDetails();
    private boolean editMode = false;
    private String pageMode = "none";
    private boolean confirmDeleteMode = false;

    // Form fields for Add/Update
    private Long offenceId; // Typically auto-generated for new records, but might be used for lookups
    private String offenceType;
    private Long penalty;
    private String vehType;

    // For search functionality
    private String searchOffenceType;
    private boolean searched = false; // To indicate if a search has been performed

    // For modal messages
    private boolean showMessageModal = false;
    private String messageTitle;
    private String messageContent;
    private FacesMessage.Severity messageSeverity; // To dynamically set modal style

    @PostConstruct
    public void init() {
        refreshOffenceDetails();
    }

    private void refreshOffenceDetails() {
        try {
            offenseDetails = getSessionBean().getOffenseDetailsFindAll();
            // Ensure offenseDetails is never null to prevent NullPointerExceptions in JSP
            if (offenseDetails == null) {
                offenseDetails = new ArrayList();
            }
        } catch (Exception e) {
            offenseDetails = new ArrayList(); // Initialize to empty list on error
            // Log the error
            System.err.println("Error loading all offenses: " + e.getMessage());
            showModalMessage("Error", "Failed to load all offense details: " + e.getMessage(), SEVERITY_ERROR);
        }
    }
    
    // --- Navigation Actions (no parameters in action method itself) ---
    public String navigateToMode() {
        // This method is called after f:setPropertyActionListener sets the pageMode
        // No specific logic needed here if simply changing the view.
        // Returning null keeps the user on the same page.
        clearMessagesAndSearch(); // Clear any previous messages/search results when navigating
        return null;
    }
    
    public long getOffenseCount() {
        if (offenseDetails != null) {
            return offenseDetails.size();
        }
        return 0;
    }


    // --- CRUD Operations ---
    public void addOffense() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            OffenseDetails newOffense = new OffenseDetails(null, offenceType, penalty, vehType); // ID usually auto-generated
            getSessionBean().persistOffenseDetails(newOffense);
           // refreshOffenseDetails();
            showModalMessage("Success", "Offense added successfully!", SEVERITY_INFO);
            resetFormFields();
            this.pageMode = "add"; // Stay on add page or navigate elsewhere
        } catch (Exception e) {
            System.err.println("Error adding offense: " + e.getMessage());
            showModalMessage("Error", "Add failed: " + e.getMessage(), SEVERITY_ERROR);
            this.pageMode = "add"; // Stay on add page
        }
    }

    public void startEdit() {
        this.editMode = true;
        this.pageMode = "update";
        // Copy properties from selectedOffense to form fields for editing
        if (selectedOffense != null) {
            this.offenceType = selectedOffense.getOffenceType();
            this.penalty = selectedOffense.getPenalty();
            this.vehType = selectedOffense.getVehType();
        }
    }

    public void saveEdit() {
        try {
            // Update selectedOffense with values from form fields
            selectedOffense.setOffenceType(this.offenceType);
            selectedOffense.setPenalty(this.penalty);
            selectedOffense.setVehType(this.vehType);

            getSessionBean().mergeOffenseDetails(selectedOffense);
            refreshOffenceDetails();
            showModalMessage("Success", "Offense updated successfully!", SEVERITY_INFO);
            this.editMode = false;
            this.selectedOffense = new OffenseDetails(); // Clear selected offense
            resetFormFields(); // Clear form fields
            this.pageMode = "update"; // Stay on update page
        } catch (Exception e) {
            System.err.println("Error saving edit: " + e.getMessage());
            showModalMessage("Error", "Update failed: " + e.getMessage(), SEVERITY_ERROR);
            this.editMode = true; // Stay in edit mode on error
            this.pageMode = "update";
        }
    }

    public void cancelEdit() {
        this.editMode = false;
        this.selectedOffense = new OffenseDetails();
        resetFormFields(); // Clear form fields
        this.pageMode = "update";
    }

    public void askDelete() {
        this.confirmDeleteMode = true;
        this.pageMode = "delete";
    }

    public void confirmDelete() {
        try {
            if (selectedOffense != null && selectedOffense.getOffenceId() != null) {
                getSessionBean().removeOffenseDetails(selectedOffense);
                refreshOffenceDetails();
                showModalMessage("Success", "Offense deleted successfully!", SEVERITY_INFO);
            } else {
                 showModalMessage("Error", "No offense selected for deletion.", SEVERITY_ERROR);
            }
            this.confirmDeleteMode = false;
            this.selectedOffense = new OffenseDetails();
            this.pageMode = "delete";
        } catch (Exception e) {
            System.err.println("Error deleting offense: " + e.getMessage());
            showModalMessage("Error", "Delete failed: " + e.getMessage(), SEVERITY_ERROR);
            this.confirmDeleteMode = true; // Keep confirm dialog open on error
            this.pageMode = "delete";
        }
    }

    public void cancelDelete() {
        this.confirmDeleteMode = false;
        this.selectedOffense = new OffenseDetails();
        this.pageMode = "delete";
    }
    
    public void searchOffenseByType() {
        this.searched = true; // Mark that a search has been performed
        try {
            if (searchOffenceType != null && !searchOffenceType.trim().isEmpty()) {
               // foundOffenses = getSessionBean().getOffenseDetailsByOffenseType(searchOffenceType.trim());
                if (foundOffenses == null || foundOffenses.isEmpty()) {
                    showModalMessage("Info", "No offenses found for type: " + searchOffenceType, SEVERITY_INFO);
                    foundOffenses = new ArrayList(); // Ensure it's not null
                } else {
                    showModalMessage("Success", foundOffenses.size() + " offense(s) found.", SEVERITY_INFO);
                }
            } else {
                foundOffenses = new ArrayList(); // Clear results if search term is empty
                showModalMessage("Warning", "Please enter an offense type to search.", SEVERITY_WARN);
                refreshOffenceDetails(); // Show all if search term cleared
            }
        } catch (Exception e) {
            System.err.println("Error searching offense: " + e.getMessage());
            showModalMessage("Error", "Search failed: " + e.getMessage(), SEVERITY_ERROR);
            foundOffenses = new ArrayList(); // Clear results on error
        }
    }


    // --- Helper Methods ---
    private void resetFormFields() {
        this.offenceId = null;
        this.offenceType = null;
        this.penalty = null;
        this.vehType = null;
    }

    private void clearMessagesAndSearch() {
        // Clear all FacesMessages (global and for components)
        // This is the correct way to clear messages in JSF for JDK 6.
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Iterator<String> clientIds = facesContext.getClientIdsWithMessages();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            Iterator<FacesMessage> messages = facesContext.getMessages(clientId);
            while (messages.hasNext()) {
                messages.next(); // Get the message
                messages.remove(); // Remove it
            }
        }
        
        // Also ensure any global messages are cleared
        Iterator<FacesMessage> globalMessages = facesContext.getMessages(null);
         while (globalMessages.hasNext()) {
            globalMessages.next(); // Get the message
            globalMessages.remove(); // Remove it
        }
        
        this.showMessageModal = false;
        this.messageTitle = null;
        this.messageContent = null;
        this.messageSeverity = null;
        this.foundOffenses = null; // Clear search results
        this.searchOffenceType = null; // Clear search input
        this.searched = false;
        refreshOffenceDetails(); // Reload all offenses when changing modes
    }

    // --- Modal Message Logic ---
    public void showModalMessage(String title, String content, FacesMessage.Severity severity) {
        this.messageTitle = title;
        this.messageContent = content;
        this.messageSeverity = severity;
        this.showMessageModal = true;
    }

    public void hideMessageModal() {
        this.showMessageModal = false;
        this.messageTitle = null;
        this.messageContent = null;
        this.messageSeverity = null;
    }

    private RtoSessionEJBLocal getSessionBean() throws NamingException {
        InitialContext ic = new InitialContext();
        return (RtoSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/RtoSessionEJB");
    }

    // --- Getters and Setters ---
    public List<OffenseDetails> getOffenseDetails() {
        // Ensure data is refreshed only when really needed for display
        // For example, if you navigate to 'view' mode, it should refresh.
        // But for modal display, don't trigger full refresh here.
        if (pageMode.equals("view") && (offenseDetails == null || offenseDetails.isEmpty())) {
            refreshOffenceDetails();
        } else if (pageMode.equals("update") || pageMode.equals("delete")) {
            refreshOffenceDetails(); // Ensure list is up-to-date for selection
        }
        return offenseDetails;
    }

    public void setOffenseDetails(List<OffenseDetails> offenseDetails) {
        this.offenseDetails = offenseDetails;
    }

    public List<OffenseDetails> getFoundOffenses() {
        return foundOffenses;
    }

    public void setFoundOffenses(List<OffenseDetails> foundOffenses) {
        this.foundOffenses = foundOffenses;
    }

    public OffenseDetails getSelectedOffense() {
        return selectedOffense;
    }

    public void setSelectedOffense(OffenseDetails selectedOffense) {
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
        clearMessagesAndSearch(); // Clear messages and search results when page mode changes
    }

    public boolean isConfirmDeleteMode() {
        return confirmDeleteMode;
    }

    public void setConfirmDeleteMode(boolean confirmDeleteMode) {
        this.confirmDeleteMode = confirmDeleteMode;
    }

    public Long getOffenceId() {
        return offenceId;
    }

    public void setOffenceId(Long offenceId) {
        this.offenceId = offenceId;
    }

    public String getOffenceType() {
        return offenceType;
    }

    public void setOffenceType(String offenceType) {
        this.offenceType = offenceType;
    }

    public Long getPenalty() {
        return penalty;
    }

    public void setPenalty(Long penalty) {
        this.penalty = penalty;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    public String getSearchOffenceType() {
        return searchOffenceType;
    }

    public void setSearchOffenceType(String searchOffenceType) {
        this.searchOffenceType = searchOffenceType;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public boolean isShowMessageModal() {
        return showMessageModal;
    }

    public void setShowMessageModal(boolean showMessageModal) {
        this.showMessageModal = showMessageModal;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public FacesMessage.Severity getMessageSeverity() {
        return messageSeverity;
    }

    public void setMessageSeverity(FacesMessage.Severity messageSeverity) {
        this.messageSeverity = messageSeverity;
    }
}