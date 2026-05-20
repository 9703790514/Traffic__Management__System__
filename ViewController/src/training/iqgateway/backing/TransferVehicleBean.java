package training.iqgateway.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.ejb.EJB;
import training.iqgateway.entities.Offense;
import training.iqgateway.entities.Owner;
import training.iqgateway.entities.Vehicle;
import training.iqgateway.entities.VehicleApplication;
import training.iqgateway.services.ClerkSessionEJBLocal;
import training.iqgateway.services.RtoSessionEJBLocal;


public class TransferVehicleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String vehicleNumber;
    private Owner currentOwner;
    private Vehicle currentVehicle;
    private String transferDate;
    private Long selectedNewOwnerId;
    private List<SelectItem> ownerSelectItems;
    private boolean newOwnerOwnsVehicle;
    private String newOwnerVehicleNumber;
    private Owner newOwnerDetails;
    private List<Offense> pendingOffenses;
    private String offensesMessage;

    private double totalPenalty;
    private boolean showClearConfirmation;


    @EJB
    private RtoSessionEJBLocal rtoSessionEJB;

    @EJB
    private ClerkSessionEJBLocal clerkSessionEJB;

    private VehicleApplication currentApplication;

    public VehicleApplication getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(VehicleApplication currentApplication) {
        this.currentApplication = currentApplication;
    }

    private RtoSessionEJBLocal getRtoSessionBean() {
        if (rtoSessionEJB == null) {
            try {
                InitialContext ic = new InitialContext();
                rtoSessionEJB = (RtoSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/RtoSessionEJB");
            } catch (NamingException e) {
                System.err.println("ERROR: RtoSessionEJB lookup failed: " + e.getMessage());
                throw new RuntimeException("RtoSessionEJB lookup failed", e);
            }
        }
        return rtoSessionEJB;
    }

    private ClerkSessionEJBLocal getClerkSessionBean() {
        if (clerkSessionEJB == null) {
            try {
                InitialContext ic = new InitialContext();
                clerkSessionEJB = (ClerkSessionEJBLocal) ic.lookup("java:comp/env/ejb/local/ClerkSessionEJB");
            } catch (NamingException e) {
                System.err.println("ERROR: ClerkSessionEJB lookup failed: " + e.getMessage());
                throw new RuntimeException("ClerkSessionEJB lookup failed", e);
            }
        }
        return clerkSessionEJB;
    }

    @PostConstruct
    public void init() {
        currentOwner = new Owner();
        currentVehicle = new Vehicle();
        newOwnerDetails = new Owner();
        pendingOffenses = new ArrayList();
        offensesMessage = null;
        totalPenalty = 0.0;
        showClearConfirmation = false;
        loadOwnerSelectItems();
    }

    private void loadOwnerSelectItems() {
        try {
            List<Owner> owners = getRtoSessionBean().getOwnerFindAll();
            ownerSelectItems = new ArrayList();
            for (Owner o : owners) {
                ownerSelectItems.add(
                    new SelectItem(o.getOwnerId(), o.getFname() + " " + o.getLname() + " (" + o.getOwnerId() + ")")
                );
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading owners for dropdown: " + e.getMessage(), null));
            ownerSelectItems = new ArrayList();
            e.printStackTrace();
        }
    }

    public String checkOffenses() {
        System.out.println("DEBUG (TransferVehicleBean): checkOffenses called for vehicleNumber: " + vehicleNumber);

        offensesMessage = null;
        pendingOffenses.clear();
        totalPenalty = 0.0;
        showClearConfirmation = false;

        if (currentVehicle == null || currentVehicle.getVehId() == null || vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Please search for a vehicle first to check offenses.", null));
            return null;
        }

        try {
            List<Offense> allOffensesForVehicle = getClerkSessionBean().findOffensesByVehicleNo(vehicleNumber.trim());
            System.out.println("DEBUG (TransferVehicleBean): findOffensesByVehicleNo returned " + (allOffensesForVehicle != null ? allOffensesForVehicle.size() : 0) + " total offenses for " + vehicleNumber);

            if (allOffensesForVehicle == null) {
                allOffensesForVehicle = new ArrayList();
            }

            for (Offense o : allOffensesForVehicle) {
                System.out.println("DEBUG (TransferVehicleBean): Checking offense ID: " + o.getOffenceDetailId() + ", Status: " + o.getOffenceStatus());
                if (o.getOffenceStatus() != null && o.getOffenceStatus().equalsIgnoreCase("pending")) {
                    pendingOffenses.add(o);
                    if (o.getOffenseDetails() != null) {
                        totalPenalty += o.getOffenseDetails().getPenalty();
                    }
                }
            }

            System.out.println("DEBUG (TransferVehicleBean): After filtering, pendingOffenses size: " + this.pendingOffenses.size());
            System.out.println("DEBUG (TransferVehicleBean): Calculated totalPenalty: " + this.totalPenalty);


            if (!this.pendingOffenses.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, this.pendingOffenses.size() + " pending offense(s) found. Navigating to details.", null));
                return "success";
            } else {
                offensesMessage = "No pending offenses found for this vehicle.";
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, offensesMessage, null));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error checking offenses: " + e.getMessage(), null));
            pendingOffenses = new ArrayList();
            totalPenalty = 0.0;
            offensesMessage = "Error checking offenses.";
            e.printStackTrace();
            return null;
        }
    }

    public String prepareClearAllOffenses() {
        System.out.println("DEBUG (TransferVehicleBean): prepareClearAllOffenses called.");
        if (pendingOffenses.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "No pending offenses to clear.", null));
            showClearConfirmation = false;
            return null;
        }

        totalPenalty = 0.0;
        for (Offense offense : pendingOffenses) {
            if (offense.getOffenseDetails() != null) {
                totalPenalty += offense.getOffenseDetails().getPenalty();
            }
        }

        showClearConfirmation = true;
        return null;
    }

    public String confirmClearAllOffenses() {
        System.out.println("DEBUG (TransferVehicleBean): confirmClearAllOffenses called. Total Penalty: " + totalPenalty);
        if (pendingOffenses.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "No pending offenses to clear.", null));
            showClearConfirmation = false;
            return null;
        }

        try {
            int clearedCount = 0;
            for (Offense offense : pendingOffenses) {
                offense.setOffenceStatus("resolved");
                getClerkSessionBean().mergeOffense(offense);
                clearedCount++;
            }

            pendingOffenses.clear();
            totalPenalty = 0.0;
            offensesMessage = null;
            showClearConfirmation = false;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, clearedCount + " pending offense(s) cleared successfully for a total penalty of $" + String.format("%.2f", totalPenalty) + ".", null));

            return "backToTransferVehicle";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to clear offenses: " + e.getMessage(), null));
            e.printStackTrace();
            showClearConfirmation = false;
            return null;
        }
    }

    public String cancelClearAll() {
        System.out.println("DEBUG (TransferVehicleBean): cancelClearAll called.");
        showClearConfirmation = false;
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Offense clearing cancelled.", null));
        return null;
    }


    public void searchVehicle() {
        System.out.println("DEBUG (TransferVehicleBean): searchVehicle called for vehNo: " + vehicleNumber);

        Iterator<FacesMessage> messages = FacesContext.getCurrentInstance().getMessages(null);
        while (messages.hasNext()) {
            messages.next();
            messages.remove();
        }

        offensesMessage = null;
        totalPenalty = 0.0;
        showClearConfirmation = false;


        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Please enter a vehicle number.", null));
            currentOwner = new Owner();
            currentVehicle = new Vehicle();
            currentApplication = null;
            pendingOffenses = new ArrayList();
            return;
        }

        try {
            VehicleApplication foundApplication = getRtoSessionBean().findVehicleByNo(vehicleNumber.trim());
            if (foundApplication != null) {
                currentApplication = foundApplication;
                currentOwner = foundApplication.getOwner();
                currentVehicle = foundApplication.getVehicle();
                pendingOffenses = new ArrayList();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle details loaded successfully.", null));
                System.out.println("DEBUG (TransferVehicleBean): Vehicle application found for: " + vehicleNumber);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No application found for this vehicle number.", null));
                currentOwner = new Owner();
                currentVehicle = new Vehicle();
                currentApplication = null;
                pendingOffenses = new ArrayList();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error searching vehicle: " + e.getMessage(), null));
            currentOwner = new Owner();
            currentVehicle = new Vehicle();
            currentApplication = null;
            pendingOffenses = new ArrayList();
            e.printStackTrace();
        }
    }

    public void searchNewOwnerVehicle() {
        System.out.println("DEBUG (TransferVehicleBean): searchNewOwnerVehicle called for vehNo: " + newOwnerVehicleNumber);
        if (newOwnerVehicleNumber == null || newOwnerVehicleNumber.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Please enter a vehicle number for new owner.", null));
            newOwnerDetails = new Owner();
            return;
        }
        try {
            Owner found = getRtoSessionBean().findOwnerByVehicleNo(newOwnerVehicleNumber.trim());
            if (found != null) {
                newOwnerDetails = found;
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "New owner details loaded from vehicle number.", null));
                System.out.println("DEBUG (TransferVehicleBean): New owner found by vehicle number: " + newOwnerVehicleNumber);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No owner found for this vehicle number.", null));
                newOwnerDetails = new Owner();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error searching new owner's vehicle: " + e.getMessage(), null));
            newOwnerDetails = new Owner();
            e.printStackTrace();
        }
    }

    public String submitTransfer() {
        System.out.println("DEBUG (TransferVehicleBean): submitTransfer called.");
        if (currentApplication == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please search for a vehicle first.", null));
            return null;
        }

        String panCardNo = newOwnerDetails.getPancardNo();
        if (panCardNo == null || panCardNo.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter the new owner's PAN card number.", null));
            return null;
        }

        try {
            Owner persistedNewOwner = getRtoSessionBean().findOwnerByPanCardNo(panCardNo.trim());
            if (persistedNewOwner == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No owner found with this PAN card number. Please create the owner first if they are new.", null));
                return null;
            }

            List<Offense> latestAllOffenses = getClerkSessionBean().findOffensesByVehicleNo(vehicleNumber.trim());
            List<Offense> filteredLatestPending = new ArrayList();
            if (latestAllOffenses != null) {
                for (Offense o : latestAllOffenses) {
                    if (o.getOffenceStatus() != null && o.getOffenceStatus().equalsIgnoreCase("pending")) {
                        filteredLatestPending.add(o);
                    }
                }
            }

            if (!filteredLatestPending.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vehicle has pending offenses. Cannot transfer ownership.", null));
                this.pendingOffenses = filteredLatestPending;
                totalPenalty = 0.0;
                for(Offense o : pendingOffenses) {
                    if (o.getOffenseDetails() != null) {
                        totalPenalty += o.getOffenseDetails().getPenalty();
                    }
                }
                showClearConfirmation = true;
                return null;
            }

            currentApplication.setOwner(persistedNewOwner);
            getRtoSessionBean().mergeVehicleApplication(currentApplication);

            currentOwner = persistedNewOwner;
            currentVehicle = new Vehicle();
            currentApplication = null;
            pendingOffenses = new ArrayList();
            offensesMessage = null;
            totalPenalty = 0.0;
            showClearConfirmation = false;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle ownership transferred to new owner successfully!", null));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Transfer failed: " + e.getMessage(), null));
            e.printStackTrace();
        }
        return null;
    }


    public String createOwner() {
        System.out.println("DEBUG (TransferVehicleBean): createOwner called.");
        try {
            if (newOwnerDetails.getFname() == null || newOwnerDetails.getFname().trim().isEmpty() ||
                newOwnerDetails.getLname() == null || newOwnerDetails.getLname().trim().isEmpty() ||
                newOwnerDetails.getPancardNo() == null || newOwnerDetails.getPancardNo().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "First Name, Last Name, and Pancard No are required to create a new owner.", null));
                return null;
            }

            Owner existingOwner = getRtoSessionBean().findOwnerByPanCardNo(newOwnerDetails.getPancardNo().trim());
            if (existingOwner != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "An owner with this PAN card number already exists. Details loaded.", null));
                newOwnerDetails = existingOwner;
                return null;
            }

            getRtoSessionBean().persistOwner(newOwnerDetails);
            loadOwnerSelectItems();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "New owner created successfully!", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to create owner: " + e.getMessage(), null));
            e.printStackTrace();
        }
        return null;
    }


    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public Owner getCurrentOwner() { return currentOwner; }
    public void setCurrentOwner(Owner currentOwner) { this.currentOwner = currentOwner; }

    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(Vehicle currentVehicle) { this.currentVehicle = currentVehicle; }

    public String getTransferDate() { return transferDate; }
    public void setTransferDate(String transferDate) { this.transferDate = transferDate; }

    public Long getSelectedNewOwnerId() { return selectedNewOwnerId; }
    public void setSelectedNewOwnerId(Long selectedNewOwnerId) { this.selectedNewOwnerId = selectedNewOwnerId; }

    public List<SelectItem> getOwnerSelectItems() { return ownerSelectItems; }
    public void setOwnerSelectItems(List<SelectItem> ownerSelectItems) { this.ownerSelectItems = ownerSelectItems; }

    public boolean isNewOwnerOwnsVehicle() { return newOwnerOwnsVehicle; }
    public void setNewOwnerOwnsVehicle(boolean newOwnerOwnsVehicle) { this.newOwnerOwnsVehicle = newOwnerOwnsVehicle; }

    public String getNewOwnerVehicleNumber() { return newOwnerVehicleNumber; }
    public void setNewOwnerVehicleNumber(String newOwnerVehicleNumber) { this.newOwnerVehicleNumber = newOwnerVehicleNumber; }

    public Owner getNewOwnerDetails() { return newOwnerDetails; }
    public void setNewOwnerDetails(Owner newOwnerDetails) { this.newOwnerDetails = newOwnerDetails; }

    public List<Offense> getPendingOffenses() {
        return pendingOffenses;
    }

    public void setPendingOffenses(List<Offense> pendingOffenses) {
        this.pendingOffenses = pendingOffenses;
    }

    public String getOffensesMessage() {
        return offensesMessage;
    }

    public void setOffensesMessage(String offensesMessage) {
        this.offensesMessage = offensesMessage;
    }

    public double getTotalPenalty() {
        return totalPenalty;
    }

    public void setTotalPenalty(double totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    public boolean isShowClearConfirmation() {
        return showClearConfirmation;
    }

    public void setShowClearConfirmation(boolean showClearConfirmation) {
        this.showClearConfirmation = showClearConfirmation;
    }
}
