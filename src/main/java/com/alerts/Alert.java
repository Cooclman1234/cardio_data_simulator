package com.alerts;

/**
 * Represents an alert
 */
public interface Alert {

    /**
     * @return the unique patient identifier
     */
    String getPatientId();

    /**
     * @return the condition or issue that triggered this alert
     */
    String getCondition();

    /**
     * @return the timestamp (ms since epoch) when the condition occurred
     */
    long getTimestamp(); 

    /**
     * @return the alert type (e.g. "trend alert", "rapid drop alert")
     */
    String getType();
}
