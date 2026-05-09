package com.alerts;

public class AlertDecorator implements Alert {
    protected final Alert wrappedAlert;

    public AlertDecorator(Alert wrappedAlert) {
        this.wrappedAlert = wrappedAlert;
    }

    @Override
    public int getPatientId() { 
        return wrappedAlert.getPatientId(); 
    }

    @Override
    public String getCondition() { 
        return wrappedAlert.getCondition(); 
    }

    @Override
    public long getTimestamp() { 
        return wrappedAlert.getTimestamp(); 
    }

    @Override
    public String getType() { 
        return wrappedAlert.getType(); 
    }
    
}
