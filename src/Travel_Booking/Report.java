package Travel_Booking;

import java.sql.Timestamp;

public class Report {
    private int reportId;
    private int adminId;
    private ReportType reportType;
    private Timestamp generatedDate;
    private String parameters;
    private String filePath;

    public enum ReportType {
        BOOKINGS, USERS, FINANCIAL, SERVICES
    }

    // Getters and setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    public Timestamp getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(Timestamp generatedDate) { this.generatedDate = generatedDate; }
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}