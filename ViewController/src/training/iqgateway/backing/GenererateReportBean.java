package training.iqgateway.backing;

import java.awt.Desktop;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.Serializable;

import org.eclipse.birt.report.engine.api.EngineException;

/**
 * Backing bean for report generation functionality.
 * Handles BIRT report generation and display operations.
 * 
 * @author TMS Development Team
 * @version 1.0
 */
public class GenererateReportBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String offenceType;
    private boolean reportGenerated = false;
    private String statusMessage = "";
    private final String outputFilePath = "D:/birt_reports/reportRes"; // Update as needed

    // Getter and Setter for offenceType
    public String getOffenceType() {
        return offenceType;
    }
    public void setOffenceType(String offenceType) {
        this.offenceType = offenceType;
    }

    // Getter for reportGenerated (for button disabling)
    public boolean isReportGenerated() {
        return reportGenerated;
    }

    // Getter for statusMessage (for status bar)
    public String getStatusMessage() {
        return statusMessage;
    }

    // Action method for "Generate Report" button
    public String generateReport() {
        try {
            ExecuteReportParam.executeReport1(
                "C:\\Users\\rambabu.routhu\\workspace\\TMSReports\\TMSReports\\reportedByOffenceType.rptdesign",
                outputFilePath, // e.g., "D:/birt_reports/reportRes.html"
                offenceType,
                "offenceType"
            );
            statusMessage = "Report generated successfully!";
            reportGenerated = true;
        } catch (EngineException ex) {
            statusMessage = "Report generation failed: " + ex.getMessage();
            reportGenerated = false;
        }
        return null;
    }


    // Action method for "Open Report" button
    public String openReport() {
        try {
            File reportFile = new File(outputFilePath);
            if (reportFile.exists()) {
                // Redirect to a JSF resource handler (e.g., /downloadReport)
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().redirect(context.getExternalContext().getRequestContextPath() + "/downloadReport");
                context.responseComplete();
            } else {
                statusMessage = "Report file not found. Please generate the report first.";
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, statusMessage, null));
            }
        } catch (Exception ex) {
            statusMessage = "Failed to open report: " + ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, statusMessage, null));
        }
        return null;
    }



    /**
     * Checks if the given offence type exists in the system.
     * Note: This is a simplified implementation. In production, this should query
     * the database through the OffenseDetails service to validate the offense type.
     * 
     * @param offenceType The offense type to validate
     * @return true if the offense type exists, false otherwise
     */
    private boolean offenceTypeExists(String offenceType) {
        // Simplified validation - accepts common offense types
        // Production code should use: getOffenseDetailsEJB().findByType(offenceType) != null
        return offenceType != null && !offenceType.trim().isEmpty();
    }
}
