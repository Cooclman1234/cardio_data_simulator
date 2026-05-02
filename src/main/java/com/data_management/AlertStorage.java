package com.data_management;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;

/**
 * Stores and manages all alerts generated.
 * 
 */
public class AlertStorage {
    private List<Alert> alertList;

    /**
     * Initiates an ArrayList object when an AlertStorage object is created
     */
    public AlertStorage() {
        this.alertList = new ArrayList<Alert>();
    }

    /**
     * Stores the alert into the list of alertStorage
     * 
     * @param alert the alert which needs to be stored
     */
    public void storeAlert(Alert alert) {
        this.alertList.add(alert);
    }

    /**
     * Dealets the alert from the list of alertStorage
     * 
     * @param alert the alert which needs to be stored
     */
    public void removeAlert(Alert alert) {
        this.alertList.remove(alert);
    }
    
}
