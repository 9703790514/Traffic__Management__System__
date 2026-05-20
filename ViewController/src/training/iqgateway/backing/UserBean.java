// UserBean.java
package training.iqgateway.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.faces.model.SelectItem; // Import SelectItem
import training.iqgateway.entities.Roles;
import training.iqgateway.entities.Users;
import training.iqgateway.services.AdminSessionEJBLocal;

public class UserBean implements Serializable {

    private String username;
    private String password;
    private String roleName;
    private Users selectedUser = new Users();
    private List<Users> users;

    private String pageMode = "none"; // Options: none, add, update, delete, view, dashboard
    private boolean editMode = false;
    private boolean confirmDeleteMode = false;

    // --- NEW PROPERTIES FOR MESSAGE MODAL ---
    private boolean showMessageModal;
    private String messageTitle;
    private String messageContent;
    // --- END NEW PROPERTIES ---

    private static final String[] ROLE_NAMES = {"ADMIN", "CLERK", "COP", "RTO", "OWNER"};

    @EJB
    private AdminSessionEJBLocal adminSessionEJB;

    @PostConstruct
    public void init() {
        refreshUsers();
    }

    public String addUser() {
        this.pageMode = "add"; // Stay on the add page

        try {
            if (username == null || username.isEmpty() || password == null || password.isEmpty() || roleName == null || roleName.isEmpty()) {
                showMessage("Input Error", "All fields are required.");
                return null;
            }
            
            // Check if user already exists
            Users existingUser = getSessionBean().findUserByUsername(username); // Assuming you have this EJB method
            if (existingUser != null) {
                showMessage("Add Failed", "User with username '" + username + "' already exists. Please choose a different username.");
                return null;
            }

            Roles roleEntity = new Roles();
            roleEntity.setRolename(roleName); // Assuming Role entity only needs rolename for lookup/persistence

            Users newUser = new Users(password, roleEntity, username);
            getSessionBean().persistUsers(newUser);
            showMessage("Success!", "User added successfully!");
            clearUserFields();
            refreshUsers();
        } catch (Exception e) {
            showMessage("Error!", "Error adding user: " + e.getMessage());
        }
        return null; // Stay on the same page
    }

    public void startEdit() {
        if (selectedUser != null && selectedUser.getRoles() != null) {
            roleName = selectedUser.getRoles().getRolename();
        }
        editMode = true;
        pageMode = "update";
    }

    public String saveEdit() {
        this.pageMode = "update"; // Stay on the update page
        try {
            if (selectedUser != null) {
                // Fetch the actual Role entity from the database if you need to set a managed entity
                // For simplicity, assuming setting rolename is enough for merge if roles is unmanaged
                Roles roleEntity = new Roles();
                roleEntity.setRolename(roleName);
                selectedUser.setRoles(roleEntity); // Set the selected role to the user object
                
                getSessionBean().mergeUsers(selectedUser);
                showMessage("Success!", "User updated!");
                refreshUsers();
            }
        } catch (Exception e) {
            showMessage("Error!", "Error updating user: " + e.getMessage());
        }
        editMode = false;
        selectedUser = new Users(); // Clear selected user
        return null; // Stay on the same page
    }

    public void cancelEdit() {
        editMode = false;
        selectedUser = new Users();
        pageMode = "update";
    }

    public void askDelete() {
        confirmDeleteMode = true;
        pageMode = "delete";
    }

    public String confirmDelete() {
        this.pageMode = "delete"; // Stay on the delete page
        try {
            if (selectedUser != null) {
                getSessionBean().removeUsers(selectedUser);
                showMessage("Success!", "User deleted!");
                refreshUsers();
            }
        } catch (Exception e) {
            showMessage("Error!", "Error deleting user: " + e.getMessage());
        }
        confirmDeleteMode = false;
        selectedUser = new Users(); // Clear selected user
        return null; // Stay on the same page
    }

    public void cancelDelete() {
        confirmDeleteMode = false;
        selectedUser = new Users();
        pageMode = "delete";
    }

    // --- Message Modal Control ---
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
    // --- End Message Modal Control ---

    public List<Users> getUsers() {
        // Users are refreshed in init and after CRUD operations, so no need for null check here
        return users;
    }

    private void refreshUsers() {
        try {
            users = getSessionBean().getUsersFindAll();
        } catch (Exception e) {
            // Log error, or show a message, but prevent null pointer for 'users'
            users = new ArrayList();
            showMessage("Initialization Error", "Could not load users: " + e.getMessage());
        }
    }

    private void clearUserFields() {
        username = null; // Changed from "" to null for consistency
        password = null;
        roleName = null;
        selectedUser = null;
    }

    // --- Navigation Methods for Navbar ---
    public String addUserr() { this.pageMode = "add"; return null; }
    public String updateUserr() { this.pageMode = "update"; return null; }
    public String deleteUserr() { this.pageMode = "delete"; return null; }
    public String viewUsers() { this.pageMode = "view"; return null; }
    public String dashboardPage() { this.pageMode = "dashboard"; return null; } // New dashboard method

    // NEW: Getter for userCount to display on dashboard
    public int getUserCount() {
        return users != null ? users.size() : 0;
    }

    // --- EJB Helper (Handling @EJB injection or JNDI lookup) ---
    private AdminSessionEJBLocal getSessionBean() {
        if (adminSessionEJB != null) {
            return adminSessionEJB;
        }
        // Fallback to JNDI lookup if @EJB injection somehow fails (e.g. not a true EE container)
        try {
            InitialContext ic = new InitialContext();
            return (AdminSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/AdminSessionEJB");
        } catch (NamingException e) {
            // Log this severely, as it indicates a deployment issue
            System.err.println("EJB lookup failed: " + e.getMessage());
            throw new RuntimeException("Failed to lookup AdminSessionEJB: " + e.getMessage(), e);
        }
    }

    // --- Getters and Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public Users getSelectedUser() { return selectedUser; }
    public void setSelectedUser(Users selectedUser) { this.selectedUser = selectedUser; }
    public void setUsers(List<Users> users) { this.users = users; }
    public String getPageMode() { return pageMode; }
    public void setPageMode(String pageMode) { this.pageMode = pageMode; }
    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
    public boolean isConfirmDeleteMode() { return confirmDeleteMode; }
    public void setConfirmDeleteMode(boolean confirmDeleteMode) { this.confirmDeleteMode = confirmDeleteMode; }

    // For hardcoded roles dropdown
    public List<SelectItem> getRoleNames() {
        List<SelectItem> items = new ArrayList();
        // Add a default "Select Role" item
        items.add(new SelectItem("", "-- Select Role --"));
        for (String r : ROLE_NAMES) {
            items.add(new SelectItem(r, r)); // Value is role name, label is role name
        }
        return items;
    }
    
    public String showAddUser() {
            this.pageMode = "add";
            clearUserFields(); // Clear any previous input
            return "add";
        }

        /**
         * Navigates to the 'Update User' list/form.
         * @return null to stay on the current page and re-render.
         */
        public String showUpdateUser() {
            this.pageMode = "update";
            editMode = false; // Ensure not in edit mode initially
            refreshUsers(); // Load users for selection
            return "update";
        }

        /**
         * Navigates to the 'Delete User' list/confirmation.
         * @return null to stay on the current page and re-render.
         */
        public String showDeleteUser() {
            this.pageMode = "delete";
            confirmDeleteMode = false; // Ensure not in delete confirmation initially
            refreshUsers(); // Load users for selection
            return "delete";
        }

        /**
         * Navigates to the 'View Users' table.
         * @return null to stay on the current page and re-render.
         */
        public String showViewUsers() {
            this.pageMode = "view";
            refreshUsers(); // Ensure users list is up-to-date
            return "view";
        }

     

    // NEW Getters/Setters for message modal
    public boolean isShowMessageModal() { return showMessageModal; }
    public void setShowMessageModal(boolean showMessageModal) { this.showMessageModal = showMessageModal; }
    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
}
