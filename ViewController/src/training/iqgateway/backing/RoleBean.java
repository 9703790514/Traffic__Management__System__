// RoleBean.java
package training.iqgateway.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
// import javax.faces.application.FacesMessage; // No longer directly used
// import javax.faces.context.FacesContext;    // No longer directly used
import javax.naming.InitialContext;
import javax.naming.NamingException;
import training.iqgateway.entities.Roles;
import training.iqgateway.services.AdminSessionEJBLocal; // Assuming AdminSessionEJB handles Roles

public class RoleBean implements Serializable {

    private String rolename;
    private String roleDesc;
    private Roles selectedRole = new Roles();
    private List<Roles> roles;

    private String pageMode = "none"; // Options: none, add, update, delete, view, dashboard
    private boolean editMode = false;
    private boolean confirmDeleteMode = false;

    // --- REINTRODUCED PROPERTIES FOR MESSAGE MODAL ---
    private boolean showMessageModal;
    private String messageTitle;
    private String messageContent;
    // --- END REINTRODUCED PROPERTIES ---

    @EJB
    private AdminSessionEJBLocal adminSessionEJB;

    @PostConstruct
    public void init() {
        refreshRoles();
    }

    public String addRole() {
        try {
            if (rolename == null || rolename.isEmpty()) {
                showMessage("Input Error", "Role Name is required.");
                return null;
            }
            if (roleDesc == null || roleDesc.isEmpty()) {
                showMessage("Input Error", "Role Description is required.");
                return null;
            }

            // Check if role already exists
            // Assuming AdminSessionEJB has a method like findRoleByRolename
            Roles existingRole = getSessionBean().findRoleByRolename(rolename);
            if (existingRole != null) {
                 showMessage("Add Failed", "Role with name '" + rolename + "' already exists.");
                return null;
            }

            Roles newRole = new Roles(rolename, roleDesc); // Assuming constructor (rolename, roleDesc)
            getSessionBean().persistRoles(newRole);
            showMessage("Success!", "Role added successfully!");
            clearRoleFields();
            refreshRoles();
        } catch (Exception e) {
            showMessage("Error!", "Error adding role: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Stay on the same page
    }

    public void startEdit() {
        editMode = true;
        pageMode = "update"; // Ensure the update panel is shown
    }

    public String saveEdit() {
        try {
            if (selectedRole != null) {
                // Assuming selectedRole.getRolename() is the primary key and doesn't change
                // And selectedRole.getRoleDesc() is updated directly by the inputText value
                getSessionBean().mergeRoles(selectedRole);
                showMessage("Success!", "Role updated!");
                refreshRoles();
            }
        } catch (Exception e) {
            showMessage("Error!", "Error updating role: " + e.getMessage());
            e.printStackTrace();
        }
        editMode = false;
        selectedRole = new Roles(); // Clear selected role
        return null; // Stay on the same page
    }

    public void cancelEdit() {
        editMode = false;
        selectedRole = new Roles();
        pageMode = "update"; // Stay on the update page
    }

    public void askDelete() {
        confirmDeleteMode = true;
        pageMode = "delete"; // Ensure the delete panel is shown
    }

    public String confirmDelete() {
        try {
            if (selectedRole != null) {
                getSessionBean().removeRoles(selectedRole);
                showMessage("Success!", "Role deleted!");
                refreshRoles();
            }
        } catch (Exception e) {
            showMessage("Error!", "Error deleting role: " + e.getMessage());
            e.printStackTrace();
        }
        confirmDeleteMode = false;
        selectedRole = new Roles(); // Clear selected role
        return null; // Stay on the same page
    }

    public void cancelDelete() {
        confirmDeleteMode = false;
        selectedRole = new Roles();
        pageMode = "delete"; // Stay on the delete page
    }

    public List<Roles> getRoles() {
        return roles;
    }

    private void refreshRoles() {
        try {
            roles = getSessionBean().getRolesFindAll();
        } catch (Exception e) {
            roles = new ArrayList(); // Initialize to avoid null pointer
            showMessage("Initialization Error", "Could not load roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearRoleFields() {
        rolename = null;
        roleDesc = null;
        selectedRole = new Roles(); // Reset selected role
    }

    // --- Navigation Methods for Navbar ---
    public String addRolePage() { this.pageMode = "add"; return null; }
    public String updateRolePage() { this.pageMode = "update"; return null; }
    public String deleteRolePage() { this.pageMode = "delete"; return null; }
    public String viewRolesPage() { this.pageMode = "view"; return null; }
    public String dashboardPage() { this.pageMode = "dashboard"; return null; } // New dashboard method

    // NEW: Getter for roleCount to avoid method call in EL
    public int getRoleCount() {
        return roles != null ? roles.size() : 0;
    }
    

    // --- Navigation Methods (as called from the Admin Dashboard JSP) ---
    // These methods set the internal pageMode property, which the main JSP uses for rendering
    // the correct content within the 'main-content' area.
    public String showAddRole() {
        this.pageMode = "add";
        clearRoleFields(); // Clear fields when navigating to add
        return "addd";
    }

    public String showUpdateRole() {
        this.pageMode = "update";
        editMode = false; // Reset edit mode when entering update page
        refreshRoles(); // Ensure roles list is fresh for selection
        return "updatee";
    }

    public String showDeleteRole() {
        this.pageMode = "delete";
        confirmDeleteMode = false; // Reset delete confirmation mode
        refreshRoles(); // Ensure roles list is fresh for selection
        return "deletee";
    }

    public String showViewRoles() {
        this.pageMode = "view";
        refreshRoles(); // Refresh roles just in case
        return "view";
    }

    // A specific method to show the dashboard for this bean's context
    public String showRoleDashboard() {
        this.pageMode = "dashboard";
        return "dasboard";
    }

    // --- Helper Methods ---


    // --- REINTRODUCED Message Modal Control ---
    private void showMessage(String title, String content) {
        this.messageTitle = title;
        this.messageContent = content;
        this.showMessageModal = true;
    }

    public String hideMessageModal() {
        this.showMessageModal = false;
        this.messageTitle = null;
        this.messageContent = null;
        return null; // Stay on the current page, hide modal
    }
    // --- END REINTRODUCED Message Modal Control ---

    // --- EJB Helper (Handling @EJB injection or JNDI lookup) ---
    private AdminSessionEJBLocal getSessionBean() {
        if (adminSessionEJB != null) {
            return adminSessionEJB;
        }
        try {
            InitialContext ic = new InitialContext();
            return (AdminSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/AdminSessionEJB");
        } catch (NamingException e) {
            System.err.println("EJB lookup failed for AdminSessionEJB: " + e.getMessage());
            throw new RuntimeException("Failed to lookup AdminSessionEJB: " + e.getMessage(), e);
        }
    }

    // --- Getters and Setters ---
    public String getRolename() { return rolename; }
    public void setRolename(String rolename) { this.rolename = rolename; }
    public String getRoleDesc() { return roleDesc; }
    public void setRoleDesc(String roleDesc) { this.roleDesc = roleDesc; }
    public Roles getSelectedRole() { return selectedRole; }
    public void setSelectedRole(Roles selectedRole) { this.selectedRole = selectedRole; }
    public void setRoles(List<Roles> roles) { this.roles = roles; }
    public String getPageMode() { return pageMode; }
    public void setPageMode(String pageMode) { this.pageMode = pageMode; }
    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
    public boolean isConfirmDeleteMode() { return confirmDeleteMode; }
    public void setConfirmDeleteMode(boolean confirmDeleteMode) { this.confirmDeleteMode = confirmDeleteMode; }
    // NEW Getters/Setters for message modal
    public boolean isShowMessageModal() { return showMessageModal; }
    public void setShowMessageModal(boolean showMessageModal) { this.showMessageModal = showMessageModal; }
    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
}