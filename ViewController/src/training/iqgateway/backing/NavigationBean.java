package training.iqgateway.backing;

public class NavigationBean {
    
    private String pageMode = "none";
    
    public String getPageMode() {
        return pageMode;
    }

    public void setPageMode(String pageMode) {
        this.pageMode = pageMode;
    }
    
    public String prepareAddOwner() {
        this.pageMode = "add";
        return "owner";
    }
    
    public String prepareDeleteOwner() {
        this.pageMode = "delete";
        return "owner";
    }
    
    public String prepareUpdateOwner() {
        this.pageMode = "update";
        return "owner";
    }
    
    public String viewOwners() {
        this.pageMode = "view";
        return "owner";
    }
    
    
    public String prepareAddVehicle() {
        this.pageMode = "add";
        return "vehicle";
    }
    
    public String prepareDeleteVehicle() {
        this.pageMode = "delete";
        return "vehicle";
    }
    
    public String prepareUpdateVehicle() {
        this.pageMode = "update";
        return "vehicle";
    }
    
    public String viewVehicles() {
        this.pageMode = "view";
        return "vehicle";
    }
    
    
    public String prepareAddOffenceType() {
        this.pageMode = "add";
        return "offencetypes";
    }
    
    public String prepareDeleteOffenceType() {
        this.pageMode = "delete";
        return "offencetypes";
    }
    
    public String prepareUpdateOffenceType() {
        this.pageMode = "update";
        return "offencetypes";
    }
    
    public String viewOffenceTypes() {
        this.pageMode = "view";
        return "offencetypes";
    }
    
    public String aboutus(){
        return "about";
    }
    
    public String contactus(){
        return "contact";
    }
    
    
    public String adminAbout(){
        return "adminbcak";
    }
    
    public String adminContact(){
        return "adminback";
    }
    
    
    public String admind(){
        return "admind";
    }
    
    
    public String dashboard(){
        return "dashboard";
    }
    
    
    public String registration(){
        return "registration";
    }
    
    
    public String transfer(){
        return "transfer";
    }
    
    public String generateR(){
        return "generate";
    }
    
    
    
    
    
    
    
  
    


}
