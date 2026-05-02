package com.alerts;

/**
 * Represents an alert
 */
public class Alert {
    private String patientId;
    private String condition;
    private long timestamp;
    private String type;

    /**
     * Creates an alert object.
     * 
     * @param patientId the unique patient identifier
     * @param condition the condition or issue of the patient record
     * @param timestamp timestmap at what point in time the condition happend
     * @param type what type of alert this is (e.g. trend alert, rapid drop alert, etc.)
     */
    public Alert(String patientId, String condition, long timestamp, String type) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
        this.type = type;
    }

    /**
     * Creates an alert object.
     * 
     * @param patientId the unique patient identifier
     * @param condition the condition or issue of the patient record
     * @param timestamp timestmap at what point in time the condition happend
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
        
    }


    /**
     * @return the unique patient identifier
     */
    public String getPatientId() {
        return this.patientId;
    }

    /**
     * @return the condition or issue that triggered this alert
     */
    public String getCondition() {
        return this.condition;
    }

    /**
     * @return the timestamp (ms since epoch) when the condition occurred
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * @return the alert type (e.g. "trend alert", "rapid drop alert")
     */
    public String gettype() {
        return this.type;
    }
}
